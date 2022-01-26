package net.minecraftforge.fml.loading.gui;

import com.google.common.io.ByteStreams;
import net.minecraftforge.fml.client.loading.IStartupMessageRenderer;
import net.minecraftforge.fml.client.loading.StartupMessageRendererManager;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.progress.Visualization;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings({"UnstableApiUsage", "BusyWait"}) //Yes we use these.
public class EarlyLoadingGuiVisualization implements Visualization
{
    private int screenWidth;
    private int screenHeight;
    private final boolean isDarkMode;
    private       long   window;
    private final Thread renderThread = new Thread(this::renderThreadFunc);

    private boolean running = true;

    public EarlyLoadingGuiVisualization() {
        final Path gameDir = FMLLoader.getGamePath();
        Path optionsPath = gameDir.resolve("options.txt");
        boolean isDarkMode = false;
        int overrideWidth = 0;
        int overrideHeight = 0;
        try {
            List<String> options = Files.readAllLines(optionsPath);
            for (String option : options) {
                String[] split = option.split(":", 2);
                if (split.length != 2) {
                    continue;
                }
                String key = split[0];
                String value = split[1];
                if (key.equals("darkMojangStudiosBackground")) {
                    isDarkMode = "true".equals(value);
                }
                if (key.equals("overrideWidth")) {
                    try {
                        overrideWidth = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (key.equals("overrideHeight")) {
                    try {
                        overrideHeight = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            // Swallow
        }
        this.isDarkMode = isDarkMode;
        if (overrideWidth > 0 && overrideHeight > 0) {
            screenWidth = overrideWidth;
            screenHeight = overrideHeight;
        } else {
            screenWidth = 854;
            screenHeight = 480;
        }
    }

    private void initWindow(@Nullable String mcVersion) {
        GLFWErrorCallback.createPrint(System.err).set();

        long glfwInitBegin = System.nanoTime();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long glfwInitEnd = System.nanoTime();

        if (glfwInitEnd - glfwInitBegin > 1e9) {
            LogManager.getLogger().fatal("WARNING : glfwInit took {} seconds to start.", (glfwInitEnd - glfwInitBegin) / 1.0e9);
        }

        // Clear the Last Exception (#7285 - Prevent Vanilla throwing an IllegalStateException due to invalid controller mappings)
        handleLastGLFWError((error, description) -> LogManager.getLogger().error(String.format("Suppressing Last GLFW error: [0x%X]%s", error, description)));

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        if (mcVersion != null) {
            // this emulates what we would get without early progress window
            // as vanilla never sets these, so GLFW uses the first window title
            // set them explicitly to avoid it using "FML early loading progress" as the class
            String vanillaWindowTitle = "Minecraft* " + mcVersion;
            glfwWindowHintString(GLFW_X11_CLASS_NAME, vanillaWindowTitle);
            glfwWindowHintString(GLFW_X11_INSTANCE_NAME, vanillaWindowTitle);
        }

        window = glfwCreateWindow(screenWidth, screenHeight, "FML early loading progress", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window"); // ignore it and make the GUI optional?
        }
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(this::fbResize);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer monPosLeft = stack.mallocInt(1);
            IntBuffer monPosTop = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);

            // try to center the window, this is a best-effort as there may not be
            // a primary monitor and we might not even be on the primary monitor...
            long primaryMonitor = glfwGetPrimaryMonitor();
            if (primaryMonitor != NULL) {
                GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);
                glfwGetMonitorPos(primaryMonitor, monPosLeft, monPosTop);
                glfwSetWindowPos(
                  window,
                  (Objects.requireNonNull(vidmode).width() - pWidth.get(0)) / 2 + monPosLeft.get(0),
                  (vidmode.height() - pHeight.get(0)) / 2 + monPosTop.get(0)
                );
            }
            IntBuffer iconWidth = stack.mallocInt(1);
            IntBuffer iconHeight = stack.mallocInt(1);
            IntBuffer iconChannels = stack.mallocInt(1);
            final GLFWImage.Buffer glfwImages = GLFWImage.mallocStack(1, stack);
            byte[] icon;
            try {
                icon = ByteStreams.toByteArray(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("forge_icon.png")));
                final ByteBuffer iconBuf = stack.malloc(icon.length);
                iconBuf.put(icon);
                ((Buffer) iconBuf).position(0);
                final ByteBuffer imgBuffer = STBImage.stbi_load_from_memory(iconBuf, iconWidth, iconHeight, iconChannels, 4);
                if (imgBuffer == null) {
                    throw new NullPointerException("Failed to load window icon"); // fall down to catch block
                }
                glfwImages.position(0);
                glfwImages.width(iconWidth.get(0));
                glfwImages.height(iconHeight.get(0));
                ((Buffer) imgBuffer).position(0);
                glfwImages.pixels(imgBuffer);
                glfwImages.position(0);
                glfwSetWindowIcon(window, glfwImages);
                STBImage.stbi_image_free(imgBuffer);
            } catch (NullPointerException | IOException e) {
                System.err.println("Failed to load forge logo");
            }
        }
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetFramebufferSize(window, w, h);
        screenWidth = w[0];
        screenHeight = h[0];
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
        glfwShowWindow(window);
        glfwPollEvents();
    }

    private void fbResize(long window, int width, int height) {
        if (window == this.window && width != 0 && height != 0) {
            screenWidth = width;
            screenHeight = height;
        }
    }

    private void handleLastGLFWError(BiConsumer<Integer, String> handler) {
        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
            int error = org.lwjgl.glfw.GLFW.glfwGetError(pointerbuffer);
            if (error != GLFW_NO_ERROR) {
                long pDescription = pointerbuffer.get();
                String description = pDescription == 0L ? "" : MemoryUtil.memUTF8(pDescription);
                handler.accept(error, description);
            }
        }
    }

    @Override
    public void updateFBSize(final IntConsumer width, final IntConsumer height) {
        width.accept(screenWidth);
        height.accept(screenHeight);
    }

    @Override
    public int windowWidth() {
        return screenWidth;
    }

    @Override
    public int windowHeight() {
        return screenHeight;
    }

    private IStartupMessageRenderer renderer;

    private void renderThreadFunc() {
        try {
            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            GL.createCapabilities();

            glClearColor(isDarkMode ? 0 : (239F / 255F), isDarkMode ? 0 : (50F / 255F), isDarkMode ? 0 : (61F / 255F), 1);

            renderer = StartupMessageRendererManager.getInstance().get().orElse(null);
            if (renderer != null) renderer.startup();

            while (running) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glViewport(0, 0, screenWidth, screenHeight);
                if (renderer != null) renderer.render(screenWidth, screenHeight, 2, isDarkMode);
                glfwSwapBuffers(window);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            glfwMakeContextCurrent(0);
        }
    }

    @Override
    public Runnable start(@Nullable String mcVersion) {
        initWindow(mcVersion);
        renderThread.setDaemon(true); // Don't hang the game if it terminates before handoff (i.e. datagen)
        renderThread.start();
        return org.lwjgl.glfw.GLFW::glfwPollEvents;
    }

    @Override
    public long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitorSupplier) {
        running = false;
        try {
            renderThread.join();
        } catch (InterruptedException ignored) {
        }
        glfwSetWindowTitle(window, title.get());
        if (monitorSupplier.getAsLong() != 0L) {
            glfwSetWindowMonitor(window, monitorSupplier.getAsLong(), 0, 0, width.getAsInt(), height.getAsInt(), GLFW_DONT_CARE);
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glClear(GL_COLOR_BUFFER_BIT);
        if (renderer != null) {
            renderer.render(screenWidth, screenHeight, 2, isDarkMode);
            //TODO: Add shutdown hook before GL Context is cleared.
        }
        glfwSwapInterval(0);
        glfwSwapBuffers(window);
        glfwSwapInterval(1);
        final GLFWFramebufferSizeCallback previous = glfwSetFramebufferSizeCallback(window, null);
        Objects.requireNonNull(previous).free();
        return window;
    }
}
