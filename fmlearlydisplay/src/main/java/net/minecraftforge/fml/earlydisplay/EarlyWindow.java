/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles GLFW window lifecycle, independent of the render backend.
 * <p>
 * Backend-specific window hints and creation are delegated to {@link BaseRenderBackend}.
 * Window geometry, callbacks, and positioning are managed here.
 */
public class EarlyWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");

    private long handle;
    private int fbWidth;
    private int fbHeight;
    private int winWidth;
    private int winHeight;
    private int winX;
    private int winY;

    public void init(int winWidth, int winHeight, boolean maximized, String mcVersion,
                     BaseRenderBackend backend, ScheduledExecutorService scheduler,
                     Consumer<String> errorHandler) {
        long glfwInitBegin = System.nanoTime();
        if (!glfwInit()) {
            errorHandler.accept("We are unable to initialize the graphics system.\nglfwInit failed.\n");
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long glfwInitEnd = System.nanoTime();
        if (glfwInitEnd - glfwInitBegin > 1e9) {
            LOGGER.error("WARNING : glfwInit took {} seconds to start.", (glfwInitEnd - glfwInitBegin) / 1.0e9);
        }

        handleLastGLFWError((error, description) -> LOGGER.error(String.format("Suppressing Last GLFW error: [0x%X]%s", error, description)));

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        backend.applyWindowHints();

        String vanillaWindowTitle = "Minecraft* ";
        if (mcVersion != null) vanillaWindowTitle += mcVersion;

        glfwWindowHintString(GLFW_X11_CLASS_NAME, vanillaWindowTitle);
        glfwWindowHintString(GLFW_X11_INSTANCE_NAME, vanillaWindowTitle);

        long primaryMonitor = glfwGetPrimaryMonitor();
        if (primaryMonitor == 0) {
            LOGGER.error("Failed to find a primary monitor - this means LWJGL isn't working properly");
            errorHandler.accept("Failed to locate a primary monitor.\nglfwGetPrimaryMonitor failed.\n");
            throw new IllegalStateException("Can't find a primary monitor");
        }
        GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);
        if (vidmode == null) {
            LOGGER.error("Failed to get the current display video mode.");
            errorHandler.accept("Failed to get current display resolution.\nglfwGetVideoMode failed.\n");
            throw new IllegalStateException("Can't get a resolution");
        }

        var successfulWindow = new AtomicBoolean(false);
        var windowFailFuture = scheduler.schedule(() -> {
            if (!successfulWindow.get()) errorHandler.accept("Timed out trying to setup the Game Window.");
        }, 10, TimeUnit.SECONDS);

        long window = backend.createWindow(winWidth, winHeight, vanillaWindowTitle);

        if (window == 0) {
            errorHandler.accept("Failed to create a window with the render backend.\n");
            throw new IllegalStateException("Failed to create a window");
        }

        successfulWindow.set(true);
        if (!windowFailFuture.cancel(true)) throw new IllegalStateException("We died but didn't somehow?");
        this.handle = window;

        int[] x = new int[1];
        int[] y = new int[1];
        glfwGetMonitorPos(primaryMonitor, x, y);
        int monitorX = x[0];
        int monitorY = y[0];

        if (maximized) {
            glfwMaximizeWindow(handle);
        }

        glfwGetWindowSize(handle, x, y);
        this.winWidth = x[0];
        this.winHeight = y[0];

        glfwSetWindowPos(handle, (vidmode.width() - this.winWidth) / 2 + monitorX, (vidmode.height() - this.winHeight) / 2 + monitorY);

        glfwSetFramebufferSizeCallback(handle, this::fbResize);
        glfwSetWindowPosCallback(handle, this::winMove);
        glfwSetWindowSizeCallback(handle, this::winResize);

        glfwShowWindow(handle);
        glfwGetWindowPos(handle, x, y);
        this.winX = x[0];
        this.winY = y[0];
        glfwGetFramebufferSize(handle, x, y);
        this.fbWidth = x[0];
        this.fbHeight = y[0];
        glfwPollEvents();
    }

    private void winResize(long window, int width, int height) {
        if (window == this.handle && width != 0 && height != 0) {
            this.winWidth = width;
            this.winHeight = height;
        }
    }

    private void fbResize(long window, int width, int height) {
        if (window == this.handle && width != 0 && height != 0) {
            this.fbWidth = width;
            this.fbHeight = height;
        }
    }

    private void winMove(long window, int x, int y) {
        if (window == this.handle) {
            this.winX = x;
            this.winY = y;
        }
    }

    static void handleLastGLFWError(BiConsumer<Integer, String> handler) {
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

    public long handle() { return handle; }
    public int fbWidth() { return fbWidth; }
    public int fbHeight() { return fbHeight; }
    public int winWidth() { return winWidth; }
    public int winHeight() { return winHeight; }
    public int winX() { return winX; }
    public int winY() { return winY; }

    public void setTitle(String title) { glfwSetWindowTitle(handle, title); }
    public void pollEvents() { glfwPollEvents(); }

    public void freeCallbacks() {
        glfwSetFramebufferSizeCallback(handle, null).free();
        glfwSetWindowPosCallback(handle, null).free();
        glfwSetWindowSizeCallback(handle, null).free();
    }
}
