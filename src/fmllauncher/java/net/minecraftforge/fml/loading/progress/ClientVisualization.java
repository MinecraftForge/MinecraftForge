/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading.progress;

import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
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
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

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

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        long glfwInitBegin = System.nanoTime();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long glfwInitEnd = System.nanoTime();

        if (glfwInitEnd - glfwInitBegin > 1e9) {
            LogManager.getLogger().fatal("WARNING : glfwInit took {} seconds to start.", (glfwInitEnd-glfwInitBegin) / 1.0e9);
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(screenWidth, screenHeight, "FML early loading progress", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window"); // ignore it and make the GUI optional?
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer monPosLeft = stack.mallocInt(1);
            IntBuffer monPosTop = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwGetMonitorPos(glfwGetPrimaryMonitor(), monPosLeft, monPosTop);
            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2 + monPosLeft.get(0),
                    (vidmode.height() - pHeight.get(0)) / 2 + monPosTop.get(0)
            );
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
        glfwShowWindow(window);
        glfwPollEvents();
    }

    private void renderProgress() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0D, screenWidth, screenHeight, 0.0D, -1000.0D, 1000.0D);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnableClientState(GL11.GL_VERTEX_ARRAY);
        glEnable(GL_BLEND);
        renderBackground();
        renderMessages();
        glfwSwapBuffers(window);
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
    public Runnable start() {
        initWindow();
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
        return window;
    }
}
