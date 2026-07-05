/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import joptsimple.OptionParser;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.fml.loading.ImmediateWindowProvider;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * The Loading Window that is opened Immediately after Forge starts.
 * <p>
 * This class is now a thin orchestrator that delegates all GPU work to {@link BaseRenderBackend}
 * and all window lifecycle to {@link EarlyWindow}. This enables swapping render backends
 * (OpenGL, Vulkan) without touching this class.
 */
public class DisplayWindow implements ImmediateWindowProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");
    private final AtomicBoolean animationTimerTrigger = new AtomicBoolean(true);

    private ColourScheme colourScheme = ColourScheme.RED;
    private String selectedBackend = "opengl";

    private BaseRenderBackend backend;
    private EarlyWindow earlyWindow;
    private RenderElement.DisplayContext context;
    private FontRasterizer font;
    private List<RenderElement> elements;
    private int framecount;
    private ScheduledFuture<?> windowTick;
    private ScheduledFuture<?> initializationFuture;

    private PerformanceInfo performanceInfo;
    @SuppressWarnings("unused")
    private ScheduledFuture<?> performanceTick;

    private static final ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = Executors.defaultThreadFactory().newThread(r);
        thread.setName("EarlyDisplay");
        thread.setDaemon(true);
        return thread;
    });
    private int fbScale;
    private int winWidth;
    private int winHeight;
    private boolean maximized;

    private final Semaphore renderLock = new Semaphore(1);
    private Runnable repaintTick = ()->{};

    @Override
    public String name() {
        return "fmlearlywindow";
    }

    @Override
    public ImmediateWindowProvider selectBackend(String backend) {
        if ("default".equals(backend) || "opengl".equals(backend)) {
            this.selectedBackend = "opengl";
            return this;
        }
        if ("vulkan".equals(backend)) {
            this.selectedBackend = "vulkan";
            return this;
        }
        return ImmediateWindowProvider.getFallbackHandler();
    }

    @Override
    public Runnable initialize(String[] arguments) {
        String mcVersion = FMLLoader.versionInfo().mcVersion();
        String forgeVersion = FMLLoader.versionInfo().forgeVersion();

        final OptionParser parser = new OptionParser();
        var widthopt = parser.accepts("width")
                .withRequiredArg().ofType(Integer.class)
                .defaultsTo(FMLConfig.getIntConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_WIDTH));
        var heightopt = parser.accepts("height")
                .withRequiredArg().ofType(Integer.class)
                .defaultsTo(FMLConfig.getIntConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_HEIGHT));
        var maximizedopt = parser.accepts("earlywindow.maximized");
        parser.allowsUnrecognizedOptions();
        var parsed = parser.parse(arguments);
        winWidth = parsed.valueOf(widthopt);
        winHeight = parsed.valueOf(heightopt);
        FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_WIDTH, winWidth);
        FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_HEIGHT, winHeight);
        fbScale = FMLConfig.getIntConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_FBSCALE);
        if (System.getenv("FML_EARLY_WINDOW_DARK") != null) {
            this.colourScheme = ColourScheme.BLACK;
        } else {
            try {
                var optionLines = Files.readAllLines(FMLPaths.GAMEDIR.get().resolve(Path.of("options.txt")));
                var keyName = "darkMojangStudiosBackground:";
                for (String line : optionLines) {
                    if (line.startsWith(keyName)) {
                        this.colourScheme = line.startsWith("true", keyName.length()) ? ColourScheme.BLACK : ColourScheme.RED;
                        break;
                    }
                }
            } catch (IOException e) {
                this.colourScheme = ColourScheme.RED;
            }
        }
        this.maximized = parsed.has(maximizedopt) || FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_MAXIMIZED);

        StartupNotificationManager.modLoaderConsumer().ifPresent(c->c.accept("Forge loading " + FMLLoader.versionInfo().forgeVersion()));
        performanceInfo = new PerformanceInfo();

        this.backend = RenderBackendFactory.create(selectedBackend);
        this.earlyWindow = new EarlyWindow();

        return start(mcVersion, forgeVersion);
    }

    private static final long MINFRAMETIME = TimeUnit.MILLISECONDS.toNanos(10);
    private long nextFrameTime = 0;

    private void renderFrame() {
        if (!renderLock.tryAcquire()) {
            return;
        }
        try {
            long nt;
            if ((nt = System.nanoTime()) < nextFrameTime) {
                return;
            }
            nextFrameTime = nt + MINFRAMETIME;
            backend.makeCurrent(earlyWindow.handle());
            backend.beginFrame();
            paintElements();
            backend.endFrame(earlyWindow.fbWidth(), earlyWindow.fbHeight());
        } catch (Throwable t) {
            LOGGER.error("BARF", t);
        } finally {
            if (this.windowTick != null) backend.releaseCurrent();
            renderLock.release();
        }
    }

    private void paintElements() {
        for (var itr = this.elements.iterator(); itr.hasNext(); ) {
            var element = itr.next();
            if (!element.render(context, framecount))
                itr.remove();
        }
        if (animationTimerTrigger.compareAndSet(true, false))
            framecount++;
    }

    public void render(int alpha) {
        backend.beginOverlay(alpha);
        paintElements();
        backend.endOverlay();
    }

    public Runnable start(@Nullable String mcVersion, final String forgeVersion) {
        earlyWindow.init(winWidth, winHeight, maximized, mcVersion, backend, renderScheduler, DisplayWindow::crashElegantly);
        this.initializationFuture = renderScheduler.schedule(() -> initRender(mcVersion, forgeVersion), 1, TimeUnit.MILLISECONDS);
        return this::periodicTick;
    }

    private void initRender(final @Nullable String mcVersion, final String forgeVersion) {
        try {
            backend.initialize(earlyWindow.handle(), colourScheme, fbScale, performanceInfo, mcVersion);
        } catch (RuntimeException e) {
            crashElegantly(e.getMessage());
            return;
        }

        try {
            this.font = new FontRasterizer("Monocraft.ttf", 200000);
            backend.uploadFontTexture(font.alphaBitmap(), font.bitmapWidth(), font.bitmapHeight(), 1 + RenderElement.INDEX_TEXTURE_OFFSET);
            font.setTextureSlot(1 + RenderElement.INDEX_TEXTURE_OFFSET);
        } catch (Throwable t) {
            LOGGER.error("Crash during font initialization", t);
            crashElegantly("An error occurred initializing a font for rendering. "+t.getMessage());
        }

        this.context = backend.context();
        this.elements = new ArrayList<>(List.of(
            RenderElement.anvil(font, backend),
            RenderElement.logMessageOverlay(font),
            RenderElement.forgeVersionOverlay(font, mcVersion + "-" + forgeVersion),
            RenderElement.performanceBar(font),
            RenderElement.progressBars(font)
        ));

        var date = Calendar.getInstance();
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SQUIR) || (date.get(Calendar.MONTH) == Calendar.APRIL && date.get(Calendar.DAY_OF_MONTH) == 1))
            this.elements.addFirst(RenderElement.squir(backend));

        backend.releaseCurrent();
        this.windowTick = renderScheduler.scheduleAtFixedRate(this::renderFrame, 50, 50, TimeUnit.MILLISECONDS);
        this.performanceTick = renderScheduler.scheduleAtFixedRate(performanceInfo::update, 0, 500, TimeUnit.MILLISECONDS);
        renderScheduler.scheduleAtFixedRate(()-> animationTimerTrigger.set(true), 1, 50, TimeUnit.MILLISECONDS);
    }

    private static final String ERROR_URL = "https://links.minecraftforge.net/early-display-errors";

    @Override
    public String getGLVersion() {
        return backend.getVersion();
    }

    static void crashElegantly(String errorDetails) {
        StringBuilder msgBuilder = new StringBuilder(2000);
        msgBuilder.append("Failed to initialize graphics window with current settings.\n");
        msgBuilder.append("\n\n");
        msgBuilder.append("Failure details:\n");
        msgBuilder.append(errorDetails);
        msgBuilder.append("\n\n");
        msgBuilder.append("If you click yes, we will try and open " + ERROR_URL + " in your default browser");
        LOGGER.error("ERROR DISPLAY\n"+msgBuilder);
        Executors.newSingleThreadExecutor().submit(()-> {
            var res = TinyFileDialogs.tinyfd_messageBox("Minecraft: Forge", msgBuilder.toString(), "yesno", "error", 0);
            if (res != 0) {
                try {
                    Desktop.getDesktop().browse(URI.create(ERROR_URL));
                } catch (IOException ioe) {
                    TinyFileDialogs.tinyfd_messageBox("Minecraft: Forge", "Sadly, we couldn't open your browser.\nVisit " + ERROR_URL, "ok", "error", 0);
                }
            }
            System.exit(1);
        });
    }

    public long setupMinecraftWindow(final int width, final int height, final String title, final long monitor, final Supplier<Object> backend) {
        try {
            this.initializationFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            Thread.dumpStack();
            crashElegantly("We seem to be having trouble initializing the window, waited for 30 seconds");
        }

        ImmediateWindowHandler.updateProgress("Initializing Game Graphics");
        while (!this.windowTick.isDone()) {
            this.windowTick.cancel(false);
        }
        var tries = 0;
        var renderlockticket = false;
        do {
            try {
                renderlockticket = renderLock.tryAcquire(100, TimeUnit.MILLISECONDS);
                if (++tries > 9) {
                    Thread.dumpStack();
                    crashElegantly("We seem to be having trouble handing off the window, tried for 1 second");
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        } while (!renderlockticket);
        renderLock.release();

        this.backend.prepareHandoff(earlyWindow.handle());
        earlyWindow.setTitle(title);
        this.backend.setVsync(false);
        earlyWindow.freeCallbacks();
        this.repaintTick = this::renderFrame;
        this.windowTick = null;
        return earlyWindow.handle();
    }

    @Override
    public boolean positionWindow(final Optional<Object> monitor, final IntConsumer widthSetter, final IntConsumer heightSetter, final IntConsumer xSetter, final IntConsumer ySetter) {
        widthSetter.accept(earlyWindow.winWidth());
        heightSetter.accept(earlyWindow.winHeight());
        xSetter.accept(earlyWindow.winX());
        ySetter.accept(earlyWindow.winY());
        return true;
    }

    @Override
    public void updateFramebufferSize(final IntConsumer width, final IntConsumer height) {
        width.accept(earlyWindow.fbWidth());
        height.accept(earlyWindow.fbHeight());
    }

    private Method loadingOverlay;

    @SuppressWarnings("unchecked")
    @Override
    public <T> Supplier<T> loadingOverlay(final Supplier<?> mc, final Supplier<?> ri, final Consumer<Optional<Throwable>> ex, final boolean fade) {
        try {
            return (Supplier<T>)loadingOverlay.invoke(null, mc, ri, ex, this);
        } catch (Throwable e) {
            throw new IllegalStateException("How did you get here?", e);
        }
    }

    @Override
    public void updateModuleReads(final ModuleLayer layer) {
        final var FORGE_MODULE = "net.minecraftforge.forge";
        var forge_module = layer.findModule(FORGE_MODULE).orElse(null);
        if (forge_module == null)
            throw new IllegalStateException("Could not find " + FORGE_MODULE + " in " + layer);

        getClass().getModule().addReads(forge_module);

        var clz = Class.forName(forge_module, "net.minecraftforge.client.loading.ForgeLoadingOverlay");

        for (var mtd : clz.getDeclaredMethods()) {
            if (Modifier.isStatic(mtd.getModifiers()) && "newInstance".equals(mtd.getName())) {
                this.loadingOverlay = mtd;
                break;
            }
        }

        if (loadingOverlay == null)
            throw new IllegalStateException("Could not find static newInstace method in " + clz.getName());
    }

    public long getFramebufferTextureHandle() {
        return backend.getFramebufferTextureHandle();
    }

    public RenderElement.DisplayContext context() {
        return backend.context();
    }

    @Override
    public void periodicTick() {
        earlyWindow.pollEvents();
        repaintTick.run();
    }

    public void addMojangTexture(final long textureHandle) {
        this.elements.addFirst(RenderElement.mojang(textureHandle, framecount));
    }

    public void close() {
        renderScheduler.shutdown();
        backend.close();
    }
}
