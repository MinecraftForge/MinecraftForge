/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.fml.loading.progress;

import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


class ClientVisualization implements EarlyProgressVisualization.Visualization {
    private final int screenWidth = 854;
    private final int screenHeight = 480;
    private long window;
    private Thread renderThread = new Thread(this::renderThreadFunc);

    private boolean running = true;
    private GLFWFramebufferSizeCallback framebufferSizeCallback;
    private int[] fbSize;

    private void initWindow(@Nullable String mcVersion) {
        GLFWErrorCallback.createPrint(System.err).set();

        long glfwInitBegin = System.nanoTime();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long glfwInitEnd = System.nanoTime();

        if (glfwInitEnd - glfwInitBegin > 1e9) {
            LogManager.getLogger().fatal("WARNING : glfwInit took {} seconds to start.", (glfwInitEnd-glfwInitBegin) / 1.0e9);
        }

        // Clear the Last Exception (#7285 - Prevent Vanilla throwing an IllegalStateException due to invalid controller mappings)
        handleLastGLFWError((error, description) -> LogManager.getLogger().error(String.format("Suppressing Last GLFW error: [0x%X]%s", error, description)));

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        if (mcVersion != null)
        {
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
        framebufferSizeCallback = GLFWFramebufferSizeCallback.create(this::fbResize);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer monPosLeft = stack.mallocInt(1);
            IntBuffer monPosTop = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);

            // try to center the window, this is a best-effort as there may not be
            // a primary monitor and we might not even be on the primary monitor...
            long primaryMonitor = glfwGetPrimaryMonitor();
            if (primaryMonitor != NULL)
            {
                GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);
                glfwGetMonitorPos(primaryMonitor, monPosLeft, monPosTop);
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2 + monPosLeft.get(0),
                        (vidmode.height() - pHeight.get(0)) / 2 + monPosTop.get(0)
                );
            }
            IntBuffer iconWidth = stack.mallocInt(1);
            IntBuffer iconHeight = stack.mallocInt(1);
            IntBuffer iconChannels = stack.mallocInt(1);
            final GLFWImage.Buffer glfwImages = GLFWImage.mallocStack(1, stack);
            byte[] icon;
            try {
                icon = ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("forge_icon.png"));
                final ByteBuffer iconBuf = stack.malloc(icon.length);
                iconBuf.put(icon);
                ((Buffer)iconBuf).position(0);
                final ByteBuffer imgBuffer = STBImage.stbi_load_from_memory(iconBuf, iconWidth, iconHeight, iconChannels, 4);
                if (imgBuffer == null) {
                    throw new NullPointerException("Failed to load window icon"); // fall down to catch block
                }
                glfwImages.position(0);
                glfwImages.width(iconWidth.get(0));
                glfwImages.height(iconHeight.get(0));
                ((Buffer)imgBuffer).position(0);
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
        fbSize = new int[] {w[0], h[0]};
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
        glfwShowWindow(window);
        glfwPollEvents();
    }

    private void renderProgress() {
        // glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // glMatrixMode(GL_PROJECTION);
        // glLoadIdentity();
        // glOrtho(0.0D, screenWidth, screenHeight, 0.0D, -1000.0D, 1000.0D);
        // glMatrixMode(GL_MODELVIEW);
        // glLoadIdentity();
        //
        // glEnableClientState(GL11.GL_VERTEX_ARRAY);
        // glEnable(GL_BLEND);
        // renderBackground();
        // renderMessages();
        // glfwSwapBuffers(window);
    }

    private static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    private static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    private static int hsvToRGB(float hue, float saturation, float value) {
        int i = (int)(hue * 6.0F) % 6;
        float f = hue * 6.0F - (float)i;
        float f1 = value * (1.0F - saturation);
        float f2 = value * (1.0F - f * saturation);
        float f3 = value * (1.0F - (1.0F - f) * saturation);
        float f4;
        float f5;
        float f6;
        switch(i) {
            case 0:
                f4 = value;
                f5 = f3;
                f6 = f1;
                break;
            case 1:
                f4 = f2;
                f5 = value;
                f6 = f1;
                break;
            case 2:
                f4 = f1;
                f5 = value;
                f6 = f3;
                break;
            case 3:
                f4 = f1;
                f5 = f2;
                f6 = value;
                break;
            case 4:
                f4 = f3;
                f5 = f1;
                f6 = value;
                break;
            case 5:
                f4 = value;
                f5 = f1;
                f6 = f2;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }

        int j = clamp((int)(f4 * 255.0F), 0, 255);
        int k = clamp((int)(f5 * 255.0F), 0, 255);
        int l = clamp((int)(f6 * 255.0F), 0, 255);
        return j << 16 | k << 8 | l;
    }

    private void renderBackground() {
        glBegin(GL_QUADS);
        glColor4f(239F / 255F, 50F / 255F, 61F / 255F, 255F / 255F); //Color from ResourceLoadProgressGui
        glVertex2f(0, 0);
        glVertex2f(0, screenHeight);
        glVertex2f(screenWidth, screenHeight);
        glVertex2f(screenWidth, 0);
        glEnd();
    }

    private void fbResize(long window, int width, int height) {
        if (window == this.window && width != 0 && height != 0) {
            fbSize = new int[] {width, height};
        }
    }

    private void handleLastGLFWError(BiConsumer<Integer, String> handler) {
        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
            int error = GLFW.glfwGetError(pointerbuffer);
            if (error != GLFW_NO_ERROR) {
                long pDescription = pointerbuffer.get();
                String description = pDescription == 0L ? "" : MemoryUtil.memUTF8(pDescription);
                handler.accept(error, description);
            }
        }
    }

    private void renderMessages() {
        List<Pair<Integer, StartupMessageManager.Message>> messages = StartupMessageManager.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            final Pair<Integer, StartupMessageManager.Message> pair = messages.get(i);
            final float fade = clamp((4000.0f - (float) pair.getLeft() - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
            if (fade <0.01f) continue;
            StartupMessageManager.Message msg = pair.getRight();
            renderMessage(msg.getText(), msg.getTypeColour(), ((screenHeight - 15) / 20) - i, fade);
        }
        renderMemoryInfo();
    }

    @Override
    public void updateFBSize(final IntConsumer width, final IntConsumer height) {
        width.accept(this.fbSize[0]);
        height.accept(this.fbSize[1]);
    }

    private static final float[] memorycolour = new float[] { 0.0f, 0.0f, 0.0f};

    private void renderMemoryInfo() {
        final MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final float pctmemory = (float) heapusage.getUsed() / heapusage.getMax();
        String memory = String.format("Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, pctmemory * 100.0, offheapusage.getUsed() >> 20);

        final int i = hsvToRGB((1.0f - (float)Math.pow(pctmemory, 1.5f)) / 3f, 1.0f, 0.5f);
        memorycolour[2] = ((i) & 0xFF) / 255.0f;
        memorycolour[1] = ((i >> 8 ) & 0xFF) / 255.0f;
        memorycolour[0] = ((i >> 16 ) & 0xFF) / 255.0f;
        renderMessage(memory, memorycolour, 1, 1.0f);
    }

    private void renderMessage(final String message, final float[] colour, int row, float alpha) {
        ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);

        glVertexPointer(3, GL11.GL_FLOAT, 16, charBuffer);
        glEnable(GL_BLEND);
        GL14.glBlendColor(0,0,0, alpha);
        glBlendFunc(GL14.GL_CONSTANT_ALPHA, GL14.GL_ONE_MINUS_CONSTANT_ALPHA);
        glColor3f(colour[0], colour[1], colour[2]);
        glPushMatrix();
        glTranslatef(10, row * 20, 0);
        glScalef(2, 2, 1);
        glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
        glPopMatrix();
        MemoryUtil.memFree(charBuffer);
    }

    private void renderThreadFunc() {
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        while (running) {
            renderProgress();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
                break;
            }
        }
        glfwMakeContextCurrent(0);
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
        glfwSetWindowSize(window, width.getAsInt(), height.getAsInt());
        if (monitorSupplier.getAsLong() != 0L)
            glfwSetWindowMonitor(window, monitorSupplier.getAsLong(), 0, 0, width.getAsInt(), height.getAsInt(), GLFW_DONT_CARE);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        renderProgress();
        glfwSwapInterval(0);
        glfwSwapBuffers(window);
        glfwSwapInterval(1);
        final GLFWFramebufferSizeCallback previous = glfwSetFramebufferSizeCallback(window, null);
        previous.free();
        return window;
    }
}
*/
