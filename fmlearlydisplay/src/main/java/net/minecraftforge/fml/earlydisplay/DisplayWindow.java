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
import org.lwjgl.opengl.GL;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.system.MemoryStack;
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
import java.util.Objects;
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

import static org.lwjgl.opengl.GL32C.*;

import static org.lwjgl.sdl.SDLError.*;
import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLHints.*;
import static org.lwjgl.sdl.SDLInit.*;
import static org.lwjgl.sdl.SDLVideo.*;

/**
 * The Loading Window that is opened Immediately after Forge starts.
 * It is called from the ModDirTransformerDiscoverer, the soonest method that ModLauncher calls into Forge code.
 * In this way, we can be sure that this will not run before any transformer or injection.
 *
 * The window itself is spun off into a secondary thread, and is handed off to the main game by Forge.
 *
 * Because it is created so early, this thread will "absorb" the context from OpenGL.
 * Therefore, it is of utmost importance that the Context is made Current for the main thread before handoff,
 * otherwise OS X will crash out.
 *
 * Based on the prior ClientVisualization, with some personal touches.
 */
public class DisplayWindow implements ImmediateWindowProvider {
    private static final int[][] GL_VERSIONS = new int[][] {{4,6}, {4,5}, {4,4}, {4,3}, {4,2}, {4,1}, {4,0}, {3,3}};
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");
    private final AtomicBoolean animationTimerTrigger = new AtomicBoolean(true);

    private ColourScheme colourScheme = ColourScheme.RED;
    private ElementShader elementShader;

    private RenderElement.DisplayContext context;
    private List<RenderElement> elements;
    private int framecount;
    private EarlyFramebuffer framebuffer;
    private ScheduledFuture<?> windowTick;
    private ScheduledFuture<?> initializationFuture;

    private PerformanceInfo performanceInfo;
    // The GL ID of the window. Used for all operations
    private long window;
    // The SDL Context we use for switching between threads
    private long sdlContext;
    // The thread that contains and ticks the window while Forge is loading mods
    private static final ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = Executors.defaultThreadFactory().newThread(r);
        thread.setName("EarlyDisplay");
        thread.setDaemon(true);
        return thread;
    });
    private int fbWidth;
    private int fbHeight;
    private int fbScale;
    private int winWidth;
    private int winHeight;
    private int winX;
    private int winY;

    private final Semaphore renderLock = new Semaphore(1);
    private boolean maximized;
    private String glVersion;
    private SimpleFont font;
    private Runnable repaintTick = ()->{};

    @Override
    public String name() {
        return "fmlearlywindow";
    }

    @Override
    public ImmediateWindowProvider selectBackend(String backend) {
        // We only support opengl
        //if ("default".equals(backend) || "opengl".equals(backend))
        //    return this;
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
                // check the options file for the color scheme
                var optionLines = Files.readAllLines(FMLPaths.GAMEDIR.get().resolve(Path.of("options.txt")));
                var keyName = "darkMojangStudiosBackground:";
                for (String line : optionLines) {
                    if (line.startsWith(keyName)) {
                        this.colourScheme = line.startsWith("true", keyName.length()) ? ColourScheme.BLACK : ColourScheme.RED;
                        break;
                    }
                }
            } catch (IOException e) {
                this.colourScheme = ColourScheme.RED; // fallback to red colourScheme
            }
        }
        this.maximized = parsed.has(maximizedopt) || FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_MAXIMIZED);

        StartupNotificationManager.modLoaderConsumer().ifPresent(c->c.accept("Forge loading " + FMLLoader.versionInfo().forgeVersion()));
        performanceInfo = new PerformanceInfo();
        return start(mcVersion, forgeVersion);
    }

    private static final long MINFRAMETIME = TimeUnit.MILLISECONDS.toNanos(10); // This is the FPS cap on the window - note animation is capped at 20FPS via the tickTimer
    private long nextFrameTime = 0;
    /**
     * The main render loop.
     * renderThread executes this.
     *
     * Performs initialization and then ticks the screen at 20 fps.
     * When the thread is killed, context is destroyed.
     */
    private void renderThreadFunc() {
        if (!renderLock.tryAcquire()) {
            return;
        }
        try {
            long nt;
            if ((nt = System.nanoTime()) < nextFrameTime) {
                return;
            }
            nextFrameTime = nt + MINFRAMETIME;
            SDL_GL_MakeCurrent(window, sdlContext);
            framebuffer.activate();
            glViewport(0, 0, this.context.scaledWidth(), this.context.scaledHeight());
            this.context.elementShader().activate();
            this.context.elementShader().updateScreenSizeUniform(this.context.scaledWidth(), this.context.scaledHeight());
            glClearColor(colourScheme.background().redf(), colourScheme.background().greenf(), colourScheme.background().bluef(), 1f);
            paintFramebuffer();
            this.context.elementShader().clear();
            framebuffer.deactivate();
            glViewport(0, 0, fbWidth, fbHeight);
            framebuffer.draw(this.fbWidth, this.fbHeight);
            // Swap buffers; we're done
            SDL_GL_SwapWindow(window);
        } catch (Throwable t) {
            LOGGER.error("BARF", t);
        } finally {
            if (this.windowTick != null)
                SDL_GL_MakeCurrent(window, 0); // we release the gl context IF we're running off the main thread
            renderLock.release();
        }
    }

    /**
     * Render initialization methods called by the Render Thread.
     * It compiles the fragment and vertex shaders for rendering text with STB, and sets up basic render framework.
     *
     * Nothing fancy, we just want to draw and render text.
     */
    private void initRender(final @Nullable String mcVersion, final String forgeVersion) {
        this.sdlContext = SDL_GL_CreateContext(window);
        if (sdlContext == 0)
            throw new IllegalStateException("Failed to create OpenGL context: " + SDL_GetError());

        // This thread owns the GL render context now. We should make a note of that.
        bindGlContext();
        GL.createCapabilities();

        // Wait for one frame to be complete before swapping; enable vsync in other words.
        SDL_GL_SetSwapInterval(1);

        LOGGER.info("GL info: " + glGetString(GL_RENDERER) + " GL version " + glGetString(GL_VERSION) + ", " + glGetString(GL_VENDOR));

        elementShader = new ElementShader();
        try {
            elementShader.init();
        } catch (Throwable t) {
            LOGGER.error("Crash during shader initialization", t);
            crashElegantly("An error occurred initializing shaders.");
        }

        // Set the clear color based on the color scheme
        glClearColor(colourScheme.background().redf(), colourScheme.background().greenf(), colourScheme.background().bluef(), 1f);

        // we always render to an 854x480 texture and then fit that to the screen - with a scale factor
        this.context = new RenderElement.DisplayContext(854, 480, fbScale, elementShader, colourScheme, performanceInfo);
        framebuffer = new EarlyFramebuffer(this.context);
        try {
            this.font = new SimpleFont("Monocraft.ttf", fbScale, 200000, 1 + RenderElement.INDEX_TEXTURE_OFFSET);
        } catch (Throwable t) {
            LOGGER.error("Crash during font initialization", t);
            crashElegantly("An error occurred initializing a font for rendering. "+t.getMessage());
        }
        this.elements = new ArrayList<>(List.of(
            RenderElement.anvil(font),
            RenderElement.logMessageOverlay(font),
            RenderElement.forgeVersionOverlay(font, mcVersion + "-" + forgeVersion),
            RenderElement.performanceBar(font),
            RenderElement.progressBars(font)
        ));

        var date = Calendar.getInstance();
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SQUIR) || (date.get(Calendar.MONTH) == Calendar.APRIL && date.get(Calendar.DAY_OF_MONTH) == 1))
            this.elements.addFirst(RenderElement.squir());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        releaseGlContext();

        this.windowTick = renderScheduler.scheduleAtFixedRate(this::renderThreadFunc, 50, 50, TimeUnit.MILLISECONDS);

        // Update the performance information every 1/2 second
        renderScheduler.scheduleAtFixedRate(performanceInfo::update, 0, 500, TimeUnit.MILLISECONDS);
        // schedule a 50 ms ticker to try and smooth out the rendering
        //renderScheduler.scheduleAtFixedRate(() -> animationTimerTrigger.set(true), 1, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Called every frame by the Render Thread to draw to the screen.
     */
    void paintFramebuffer() {
        // Clear the screen to our color
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (var itr = this.elements.iterator(); itr.hasNext(); ) {
            var element = itr.next();
            if (!element.render(context, framecount))
                itr.remove();
        }
        if (animationTimerTrigger.compareAndSet(true, false)) // we only increment the framecount on a periodic basis
            framecount++;
    }


    public void render(int alpha) {
        var currentVAO = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        var currentFB = glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        glViewport(0, 0, this.context.scaledWidth(), this.context.scaledHeight());
        RenderElement.globalAlpha = alpha;
        framebuffer.activate();
        glClearColor(colourScheme.background().redf(), colourScheme.background().greenf(), colourScheme.background().bluef(), alpha / 255f);
        elementShader.activate();
        elementShader.updateScreenSizeUniform(this.context.scaledWidth(), this.context.scaledHeight());
        paintFramebuffer();
        elementShader.clear();
        framebuffer.deactivate();
        glBindVertexArray(currentVAO);
        glBindFramebuffer(GL_FRAMEBUFFER, currentFB);
    }

    /**
     * Start the window and Render Thread; we're ready to go.
     */
    public Runnable start(@Nullable String mcVersion, final String forgeVersion) {
        initWindow(mcVersion);
        this.initializationFuture = renderScheduler.schedule(() -> initRender(mcVersion, forgeVersion), 1, TimeUnit.MILLISECONDS);
        return this::periodicTick;
    }

    private static final String ERROR_URL = "https://links.minecraftforge.net/early-display-errors";
    @Override
    public String getGLVersion() {
        return this.glVersion;
    }

    private static void crashElegantly(String errorDetails) {
        StringBuilder msgBuilder = new StringBuilder(2000);
        msgBuilder.append("Failed to initialize graphics window with current settings.\n");
        msgBuilder.append("\n\n");
        msgBuilder.append("Failure details:\n");
        msgBuilder.append(errorDetails);
        msgBuilder.append("\n\n");
        msgBuilder.append("If you click yes, we will try and open " + ERROR_URL + " in your default browser");
        LOGGER.error("ERROR DISPLAY\n"+msgBuilder);
        // we show the display on a new dedicated thread
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

    /**
     * Called to initialize the window when preparing for the Render Thread.
     *
     * The act of calling glfwInit here creates a concurrency issue; GL doesn't know whether we're gonna call any
     * GL functions from the secondary thread and the main thread at the same time.
     *
     * It's then our job to make sure this doesn't happen, only calling GL functions where the Context is Current.
     * As long as we can verify that, then GL (and things like OS X) have no complaints with doing this.
     *
     * @param mcVersion Minecraft Version
     */
    public void initWindow(@Nullable String mcVersion) {
        // Initialize a time guard, in case something goes wrong
        long initBegin = System.nanoTime();
        if (!SDL_Init(SDL_INIT_VIDEO)) {
            crashElegantly("We are unable to initialize the graphics system.\nSDL_Init failed.\n");
            throw new IllegalStateException("Unable to initialize SDL");
        }
        long initEnd = System.nanoTime();

        if (initEnd - initBegin > 1e9)
            LOGGER.error("WARNING : Window init took {} seconds to start.", (initEnd - initBegin) / 1.0e9);

        // Set window hints for the new window we're gonna create.
        SDL_ResetHints();

        String vanillaWindowTitle = "Minecraft* ";
        if (mcVersion != null)
            vanillaWindowTitle += mcVersion;

        var primaryMonitor = SDL_GetPrimaryDisplay();
        if (primaryMonitor == 0) {
            LOGGER.error("Failed to find a primary monitor - this means LWJGL isn't working properly");
            crashElegantly("Failed to locate a primary monitor.\nSDL_GetPrimaryDisplay failed.\n");
            throw new IllegalStateException("Can't find a primary monitor");
        }

        var vidmode = SDL_GetDesktopDisplayMode(primaryMonitor);
        if (vidmode == null) {
            LOGGER.error("Failed to get the current display video mode.");
            crashElegantly("Failed to get current display resolution.\nSDL_GetDesktopDisplayMode failed.\n");
            throw new IllegalStateException("Can't get a resolution");
        }

        long window = 0;
        var successfulWindow = new AtomicBoolean(false);
        var windowFailFuture = renderScheduler.schedule(() -> {
            if (!successfulWindow.get())
                crashElegantly("Timed out trying to setup the Game Window.");
        }, 10, TimeUnit.SECONDS);

        var skipVersions = FMLConfig.<String>getListConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SKIP_GL_VERSIONS);
        boolean showHelpLog = FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_LOG_HELP_MSG);

        record Attempt(String vesion, String error) {}
        final List<Attempt> attempts = new ArrayList<>();
        String requestedVersion = null;

        for (int x = 0; x < GL_VERSIONS.length; x++) {
            var version = GL_VERSIONS[x];
            requestedVersion = version[0] + "." + version[1];

            if (skipVersions.contains(requestedVersion)) {
                LOGGER.info("Skipping GL version " + requestedVersion + " because of configuration");
                continue;
            }

            LOGGER.info("Trying GL version " + requestedVersion);
            if (showHelpLog && x == 0) {
                LOGGER.info("""
                If this message is the only thing at the bottom of your log before a crash, you probably have a driver issue.

                Possible solutions:
                A) Make sure Minecraft is set to prefer high performance graphics in the OS and/or driver control panel
                B) Check for driver updates on the graphics brand's website
                C) Try reinstalling your graphics drivers
                D) If still not working after trying all of the above, ask for further help on the Forge forums or Discord

                You can safely ignore this message if the game starts up successfully.""");
            }

            SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION,    version[0]);
            SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION,    version[1]);
            SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK,     SDL_GL_CONTEXT_PROFILE_CORE);
            SDL_GL_SetAttribute(SDL_GL_CONTEXT_FLAGS,            SDL_GL_CONTEXT_FORWARD_COMPATIBLE_FLAG);
            SDL_GL_SetAttribute(SDL_GL_FRAMEBUFFER_SRGB_CAPABLE, GL_TRUE);
            window = SDL_CreateWindow(vanillaWindowTitle, winWidth, winHeight,
                  SDL_WINDOW_OPENGL
                | SDL_WINDOW_RESIZABLE
                | SDL_WINDOW_HIGH_PIXEL_DENSITY
            );

            if (window == 0) {
                var error = Objects.requireNonNullElse(SDLError.SDL_GetError(), "<no error>");
                attempts.add(new Attempt(requestedVersion, error));
                LOGGER.trace("Failed to create window using {}.{}: {}", version[0], version[1], error);
            } else {
                SDLVideo.SDL_SetWindowMinimumSize(window, 320, 240);
                LOGGER.info("Created window using SDL video driver: {}", SDLVideo.SDL_GetCurrentVideoDriver());
                break; // We got a valid window!
            }
        }

        if (window == 0) {
            LOGGER.error("Failed to find any valid GLFW profile.");
            var message = new StringBuilder()
                .append("Failed to find a valid GLFW profile.\n");

            if (attempts.isEmpty()) {
                message.append("All GL Versions were skipped");
            } else {
                message.append("We tried:");
                for (var attempt : attempts)
                    message.append("\n\t").append(attempt.vesion).append(':').append(attempt.error);
            }

            crashElegantly(message.toString());
            throw new IllegalStateException("Failed to create a GLFW window with any profile");
        }

        successfulWindow.set(true);
        if (!windowFailFuture.cancel(true))
            throw new IllegalStateException("We died but didn't somehow?");

        try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
            var majBuf = stack.mallocInt(1);
            var minBuf = stack.mallocInt(1);
            SDL_GL_GetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, majBuf);
            SDL_GL_GetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, minBuf);
           this.glVersion = majBuf.get(0) + "." + minBuf.get(0);
        }

        LOGGER.info("Requested GL version " + requestedVersion + " got version " + this.glVersion);
        this.window = window;

        if (showHelpLog)
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_LOG_HELP_MSG, false);

        if (this.maximized)
            SDL_MaximizeWindow(window);

        var size = getWindowSize(window);
        this.onWindowResize(size.x, size.y);

        var monitor = getMonitorSize(primaryMonitor);
        if (monitor != null)
            SDL_SetWindowPosition(window, (monitor.width - size.x) / 2 + monitor.x, (monitor.height - size.y) / 2 + monitor.y);

        pollEvents();

        SDL_ShowWindow(window);
    }

    private record Point(int x, int y) {}
    private record Rect(int x, int y, int width, int height) {}

    private static @Nullable Rect getMonitorSize(int monitor) {
        try (var bounds = SDL_Rect.malloc()) {
            // Fetch the display bounds for the current index
            if (SDL_GetDisplayBounds(monitor, bounds)) {
                int x = bounds.x();
                int y = bounds.y();
                int width = bounds.w();
                int height = bounds.h();
                LOGGER.info("Display {} Bounds -> X: {}, Y: {}, Width: {}, Height: {}", monitor, x, y, width, height);
                return new Rect(x, y, width, height);
            } else {
                LOGGER.warn("Could not get bounds for display: " + SDL_GetError());
                return null;
            }
        }
    }

    private static Point getWindowSize(long window) {
        try (var stack = MemoryStack.stackPush()) {
            var width  = stack.mallocInt(1);
            var height = stack.mallocInt(1);
            SDL_GetWindowSize(window, width, height);
            return new Point(width.get(0), height.get(0));
        }
    }

    /**
     * Hand-off the window to the vanilla game.
     * Called on the main thread instead of the game's initialization.
     *
     * @return the Window we own.
     */
    @Override
    public long setupMinecraftWindow(final int width, final int height, final String title, final Supplier<Object> backend) {
        // wait for the window to actually be initialized
        try {
            this.initializationFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            Thread.dumpStack();
            crashElegantly("We seem to be having trouble initializing the window, waited for 30 seconds");
        }

        // we have to spin wait for the window ticker
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
        // we don't want the lock, just making sure it's back on the main thread
        renderLock.release();

        SDL_GL_MakeCurrent(window, sdlContext);
        // Set the title to what the game wants
        SDL_SetWindowTitle(window, title);
        SDL_GL_SetSwapInterval(0); // Disable vsync, let Minecraft control it
        this.repaintTick = this::renderThreadFunc; // the repaint will continue to be called until the overlay takes over
        this.windowTick = null; // this tells the render thread that the async ticker is done
        return window;
    }

    @Override
    public boolean positionWindow(final Optional<Object> monitor, final IntConsumer widthSetter, final IntConsumer heightSetter, final IntConsumer xSetter, final IntConsumer ySetter) {
        widthSetter.accept(this.winWidth);
        heightSetter.accept(this.winHeight);
        xSetter.accept(this.winX);
        ySetter.accept(this.winY);
        return true;
    }

    @Override
    public void updateFramebufferSize(final IntConsumer width, final IntConsumer height) {
        width.accept(this.fbWidth);
        height.accept(this.fbHeight);
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

    public int getFramebufferTextureId() {
        return framebuffer.getTexture();
    }

    public RenderElement.DisplayContext context() {
        return this.context;
    }

    @Override
    public void periodicTick() {
        pollEvents();
        repaintTick.run();
    }

    public void addMojangTexture(final int textureId) {
        this.elements.addFirst(RenderElement.mojang(textureId, framecount));
    }

    public void close() {
        // Close the Render Scheduler thread
        renderScheduler.shutdown();
        this.framebuffer.close();
        this.context.elementShader().close();
        SimpleBufferBuilder.destroy();
    }

    private void bindGlContext() {
        SDL_GL_MakeCurrent(window, sdlContext);
        //if (!SDL_GL_MakeCurrent(window, sdlContext))
        //    throw new IllegalStateException("Failed to make OpenGL context current: " + SDL_GetError());
    }

    private void releaseGlContext() {
        SDL_GL_MakeCurrent(window, 0);
    }


    private void pollEvents() {
        try (var event = SDL_Event.malloc()) {
            while (SDL_PollEvent(event)) {
                switch (event.type()) {
                    case SDL_EVENT_WINDOW_MOVED:
                        this.onWindowMove(event.window().data1(), event.window().data2());
                        break;
                    case SDL_EVENT_WINDOW_RESIZED:
                        this.onWindowResize(event.window().data1(), event.window().data2());
                        break;
                    case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED:
                        this.onFrameBufferResize(event.window().data1(), event.window().data2());
                        break;

                    /*
                    We were told to quit for some reason, find a way to abort?
                    case SDL_EVENT_QUIT:
                    case SDL_EVENT_WINDOW_CLOSE_REQUESTED:
                    case SDL_EVENT_TERMINATING:

                    Window was minimized or restored, maybe stop rendering?
                    case SDL_EVENT_WINDOW_MINIMIZED:
                    case SDL_EVENT_WINDOW_MAXIMIZED:
                    case SDL_EVENT_WINDOW_RESTORED:
                    */
                }
            }
        }
    }

    private void onWindowResize(int width, int height) {
        if (width == 0 || height == 0)
            return;

        this.winWidth = width;
        this.winHeight = height;
    }

    private void onFrameBufferResize(int width, int height) {
        if (width == 0 || height == 0)
            return;

        this.fbWidth = width;
        this.fbHeight = height;
    }

    private void onWindowMove(int x, int y) {
        this.winX = x;
        this.winY = y;
    }

}
