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
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL32C.*;

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
    private static final int[][] GL_VERSIONS = new int[][] {{4,6}, {4,5}, {4,4}, {4,3}, {4,2}, {4,1}, {4,0}, {3,3}, {3,2}};
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");
    private final AtomicBoolean animationTimerTrigger = new AtomicBoolean(true);

    private ColourScheme colourScheme;
    private ElementShader elementShader;

    private RenderElement.DisplayContext context;
    private List<RenderElement> elements;
    private int framecount;
    private EarlyFramebuffer framebuffer;
    private ScheduledFuture<?> windowTick;

    private PerformanceInfo performanceInfo;
    private ScheduledFuture<?> performanceTick;
    // The GL ID of the window. Used for all operations
    private long window;
    // The thread that contains and ticks the window while Forge is loading mods
    private ScheduledExecutorService renderScheduler;
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
    public Runnable initialize(String[] arguments) {
        final OptionParser parser = new OptionParser();
        var mcversionopt = parser.accepts("fml.mcVersion").withRequiredArg().ofType(String.class);
        var forgeversionopt = parser.accepts("fml.forgeVersion").withRequiredArg().ofType(String.class);
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
        if (System.getenv("FML_EARLY_WINDOW_DARK")!= null) {
            this.colourScheme = ColourScheme.BLACK;
        } else {
            try {
                var optionLines = Files.readAllLines(FMLPaths.GAMEDIR.get().resolve(Paths.get("options.txt")));
                var options = optionLines.stream().map(l -> l.split(":")).filter(a -> a.length == 2).collect(Collectors.toMap(a -> a[0], a -> a[1]));
                var colourScheme = Boolean.parseBoolean(options.getOrDefault("darkMojangStudiosBackground", "false"));
                this.colourScheme = colourScheme ? ColourScheme.BLACK : ColourScheme.RED;
            } catch (IOException ioe) {
                // No options
                this.colourScheme = ColourScheme.RED; // default to red colourscheme
            }
        }
        this.maximized = parsed.has(maximizedopt) || FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_MAXIMIZED);

        var forgeVersion = parsed.valueOf(forgeversionopt);
        StartupNotificationManager.modLoaderConsumer().ifPresent(c->c.accept("Forge loading "+ forgeVersion));
        performanceInfo = new PerformanceInfo();
        return start(parsed.valueOf(mcversionopt), forgeVersion);
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
            glfwMakeContextCurrent(window);
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
            glfwSwapBuffers(window);
        } catch (Throwable t) {
            LOGGER.error("BARF", t);
        } finally {
            if (this.windowTick != null) glfwMakeContextCurrent(0); // we release the gl context IF we're running off the main thread
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
        // This thread owns the GL render context now. We should make a note of that.
        glfwMakeContextCurrent(window);
        // Wait for one frame to be complete before swapping; enable vsync in other words.
        glfwSwapInterval(1);
        createCapabilities();
        LOGGER.info("GL info: "+ glGetString(GL_RENDERER) + " GL version " + glGetString(GL_VERSION) + ", " + glGetString(GL_VENDOR));

        elementShader = new ElementShader();
        try {
            elementShader.init();
        } catch (Throwable t) {
            LOGGER.error("Crash during shader initialization", t);
            crashElegantly("An error occurred initializing shaders.");
        }

        // Set the clear color based on the colour scheme
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
        this.elements = new ArrayList<>(Arrays.asList(
                RenderElement.anvil(font),
                RenderElement.logMessageOverlay(font),
                RenderElement.forgeVersionOverlay(font, mcVersion+"-"+forgeVersion.split("-")[0]),
                RenderElement.performanceBar(font),
                RenderElement.progressBars(font)
        ));

        var date = Calendar.getInstance();
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SQUIR) || (date.get(Calendar.MONTH) == Calendar.APRIL && date.get(Calendar.DAY_OF_MONTH) == 1))
            this.elements.add(0, RenderElement.squir());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glfwMakeContextCurrent(0);
        this.windowTick = renderScheduler.scheduleAtFixedRate(this::renderThreadFunc, 50, 50, TimeUnit.MILLISECONDS);
        this.performanceTick = renderScheduler.scheduleAtFixedRate(performanceInfo::update, 0, 500, TimeUnit.MILLISECONDS);
        // schedule a 50 ms ticker to try and smooth out the rendering
        renderScheduler.scheduleAtFixedRate(()-> animationTimerTrigger.set(true), 1, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Called every frame by the Render Thread to draw to the screen.
     */
    void paintFramebuffer() {
        // Clear the screen to our color
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.elements.removeIf(element -> !element.render(context, framecount));
        if (animationTimerTrigger.compareAndSet(true, false)) // we only increment the framecount on a periodic basis
            framecount++;
    }


    public void render(int alpha) {
        var currentVAO = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        var currentFB = glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        glfwSwapInterval(0);
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
        renderScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final var thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        initWindow(mcVersion);
        renderScheduler.schedule(() -> initRender(mcVersion, forgeVersion), 1, TimeUnit.MILLISECONDS);
        return this::periodicTick;
    }

    private static final String ERROR_URL = "https://links.minecraftforge.net/early-display-errors";
    @Override
    public String getGLVersion() {
        return this.glVersion;
    }

    private void crashElegantly(String errorDetails) {
        String qrText;
        try (var is = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/glfailure.txt")))) {
            qrText = is.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            qrText = "";
        }

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
            var res = TinyFileDialogs.tinyfd_messageBox("Minecraft: Forge", msgBuilder.toString(), "yesno", "error", false);
            if (res) {
                try {
                    Desktop.getDesktop().browse(URI.create(ERROR_URL));
                } catch (IOException ioe) {
                    TinyFileDialogs.tinyfd_messageBox("Minecraft: Forge", "Sadly, we couldn't open your browser.\nVisit " + ERROR_URL, "ok", "error", false);
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
     * @return The selected GL profile as an integer pair
     */
    public void initWindow(@Nullable String mcVersion) {
        // Initialize GLFW with a time guard, in case something goes wrong
        long glfwInitBegin = System.nanoTime();
        if (!glfwInit()) {
            crashElegantly("We are unable to initialize the graphics system.\nglfwInit failed.\n");
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long glfwInitEnd = System.nanoTime();

        if (glfwInitEnd - glfwInitBegin > 1e9) {
            LOGGER.error("WARNING : glfwInit took {} seconds to start.", (glfwInitEnd - glfwInitBegin) / 1.0e9);
        }

        // Clear the Last Exception (#7285 - Prevent Vanilla throwing an IllegalStateException due to invalid controller mappings)
        handleLastGLFWError((error, description) -> LOGGER.error(String.format("Suppressing Last GLFW error: [0x%X]%s", error, description)));

        // Set window hints for the new window we're gonna create.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        if (mcVersion != null) {
            // this emulates what we would get without early progress window
            // as vanilla never sets these, so GLFW uses the first window title
            // set them explicitly to avoid it using "FML early loading progress" as the class
            String vanillaWindowTitle = "Minecraft* " + mcVersion;
            glfwWindowHintString(GLFW_X11_CLASS_NAME, vanillaWindowTitle);
            glfwWindowHintString(GLFW_X11_INSTANCE_NAME, vanillaWindowTitle);
        }

        long primaryMonitor = glfwGetPrimaryMonitor();
        if (primaryMonitor == 0) {
            LOGGER.error("Failed to find a primary monitor - this means LWJGL isn't working properly");
            crashElegantly("Failed to locate a primary monitor.\nglfwGetPrimaryMonitor failed.\n");
            throw new IllegalStateException("Can't find a primary monitor");
        }
        GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);

        if (vidmode == null) {
            LOGGER.error("Failed to get the current display video mode.");
            crashElegantly("Failed to get current display resolution.\nglfwGetVideoMode failed.\n");
            throw new IllegalStateException("Can't get a resolution");
        }
        long window = 0;
        var successfulWindow = new AtomicBoolean(false);
        var windowFailFuture = renderScheduler.schedule(()->{
            if (!successfulWindow.get()) crashElegantly("Timed out trying to setup the Game Window.");
        }, 10, TimeUnit.SECONDS);
        int versidx = 0;
        var skipVersions = FMLConfig.<String>getListConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SKIP_GL_VERSIONS);
        final String[] lastGLError=new String[GL_VERSIONS.length];
        do {
            final var glVersionToTry = GL_VERSIONS[versidx][0] + "." + GL_VERSIONS[versidx][1];
            if (skipVersions.contains(glVersionToTry)) {
                LOGGER.info("Skipping GL version "+ glVersionToTry+" because of configuration");
                versidx++;
                continue;
            }
            LOGGER.info("Trying GL version " + glVersionToTry);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, GL_VERSIONS[versidx][0]); // we try our versions one at a time
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, GL_VERSIONS[versidx][1]);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
            window = glfwCreateWindow(winWidth, winHeight, "Minecraft: Forge Loading...", 0L, 0L);
            var erridx = versidx;
            handleLastGLFWError((error, description) -> lastGLError[erridx] = String.format("Trying %d.%d: GLFW error: [0x%X]%s", GL_VERSIONS[erridx][0], GL_VERSIONS[erridx][1], error, description));
            if (lastGLError[versidx] != null) {
                LOGGER.trace(lastGLError[versidx]);
            }
            versidx++;
        } while (window == 0 && versidx < GL_VERSIONS.length);
//        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(12));
        if (versidx== GL_VERSIONS.length && window == 0) {
            LOGGER.error("Failed to find any valid GLFW profile. "+lastGLError[0]);

            crashElegantly("Failed to find a valid GLFW profile.\nWe tried "+
                    Arrays.stream(GL_VERSIONS).map(p->p[0]+"."+p[1]).filter(o -> !skipVersions.contains(o))
                            .collect(Collector.of(()->new StringJoiner(", ").setEmptyValue("no versions"), StringJoiner::add, StringJoiner::merge, StringJoiner::toString))+
                    " but none of them worked.\n"+ Arrays.stream(lastGLError).filter(Objects::nonNull).collect(Collectors.joining("\n")));
            throw new IllegalStateException("Failed to create a GLFW window with any profile");
        }
        successfulWindow.set(true);
        if (!windowFailFuture.cancel(true)) throw new IllegalStateException("We died but didn't somehow?");
        var requestedVersion = GL_VERSIONS[versidx-1][0]+"."+GL_VERSIONS[versidx-1][1];
        var maj = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MAJOR);
        var min = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MINOR);
        var gotVersion = maj+"."+min;
        LOGGER.info("Requested GL version "+requestedVersion+" got version "+gotVersion);
        this.glVersion = gotVersion;
        this.window = window;

        int[] x = new int[1];
        int[] y = new int[1];
        glfwGetMonitorPos(primaryMonitor, x, y);
        int monitorX = x[0];
        int monitorY = y[0];
//        glfwSetWindowSizeLimits(window, 854, 480, GLFW_DONT_CARE, GLFW_DONT_CARE);
        if (this.maximized) {
            glfwMaximizeWindow(window);
        }

        glfwGetWindowSize(window, x, y);
        this.winWidth = x[0];
        this.winHeight = y[0];

        glfwSetWindowPos(window, (vidmode.width() - this.winWidth) / 2 + monitorX, (vidmode.height() - this.winHeight) / 2 + monitorY);

        // Attempt setting the icon
        int[] channels = new int[1];
        try (var glfwImgBuffer = GLFWImage.create(MemoryUtil.getAllocator().malloc(GLFWImage.SIZEOF), 1)) {
            final ByteBuffer imgBuffer;
            try (GLFWImage glfwImages = GLFWImage.malloc()) {
                imgBuffer = STBHelper.loadImageFromClasspath("forge_logo.png", 20000, x, y, channels);
                glfwImgBuffer.put(glfwImages.set(x[0], y[0], imgBuffer));
                glfwSetWindowIcon(window, glfwImgBuffer);
                STBImage.stbi_image_free(imgBuffer);
            }
        } catch (NullPointerException e) {
            System.err.println("Failed to load forge logo");
        }
        handleLastGLFWError((error, description) -> LOGGER.debug(String.format("Suppressing GLFW icon error: [0x%X]%s", error, description)));

        glfwSetFramebufferSizeCallback(window, this::fbResize);
        glfwSetWindowPosCallback(window, this::winMove);
        glfwSetWindowSizeCallback(window, this::winResize);

        // Show the window
        glfwShowWindow(window);
        glfwGetWindowPos(window, x, y);
        this.winX = x[0];
        this.winY = y[0];
        glfwGetFramebufferSize(window, x, y);
        this.fbWidth = x[0];
        this.fbHeight = y[0];
        glfwPollEvents();
    }

    private void badWindowHandler(final int code, final long desc) {
        LOGGER.error("Got error from GLFW window init: "+code+ " "+MemoryUtil.memUTF8(desc));
    }

    private void winResize(long window, int width, int height) {
        if (window == this.window && width != 0 && height != 0) {
            this.winWidth = width;
            this.winHeight = height;
        }
    }
    private void fbResize(long window, int width, int height) {
        if (window == this.window && width != 0 && height != 0) {
            this.fbWidth = width;
            this.fbHeight = height;
        }
    }

    private void winMove(long window, int x, int y) {
        if (window == this.window) {
            this.winX = x;
            this.winY = y;
        }
    }
    private void handleLastGLFWError(BiConsumer<Integer, String> handler) {
        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
            int error = glfwGetError(pointerbuffer);
            if (error != GLFW_NO_ERROR) {
                long pDescription = pointerbuffer.get();
                String description = pDescription == 0L ? "" : MemoryUtil.memUTF8(pDescription);
                handler.accept(error, description);
            }
        }
    }

    /**
     * Hand-off the window to the vanilla game.
     * Called on the main thread instead of the game's initialization.
     *
     * @return the Window we own.
     */
    public long setupMinecraftWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitorSupplier) {
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

        glfwMakeContextCurrent(window);
        // Set the title to what the game wants
        glfwSetWindowTitle(window, title.get());
        glfwSwapInterval(0);
        // Clean up our hooks
        glfwSetFramebufferSizeCallback(window, null).free();
        glfwSetWindowPosCallback(window, null).free();
        glfwSetWindowSizeCallback(window, null).free();
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
        var fm = layer.findModule("forge").orElseThrow();
        getClass().getModule().addReads(fm);
        var clz = FMLLoader.getGameLayer().findModule("forge").map(l->Class.forName(l, "net.minecraftforge.client.loading.ForgeLoadingOverlay")).orElseThrow();
        var methods = Arrays.stream(clz.getMethods()).filter(m-> Modifier.isStatic(m.getModifiers())).collect(Collectors.toMap(Method::getName, Function.identity()));
        loadingOverlay = methods.get("newInstance");
    }

    public int getFramebufferTextureId() {
        return framebuffer.getTexture();
    }

    public RenderElement.DisplayContext context() {
        return this.context;
    }

    @Override
    public void periodicTick() {
        glfwPollEvents();
        repaintTick.run();
    }

    public void addMojangTexture(final int textureId) {
        this.elements.add(0, RenderElement.mojang(textureId, framecount));
//        this.elements.get(0).retire(framecount + 1);
    }

    public void close() {
        // Close the Render Scheduler thread
        renderScheduler.shutdown();
        this.framebuffer.close();
        this.context.elementShader().close();
        SimpleBufferBuilder.destroy();
    }
}