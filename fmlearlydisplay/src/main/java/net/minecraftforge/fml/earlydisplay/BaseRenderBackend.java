/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import java.nio.ByteBuffer;

/**
 * Abstract base class for render backends.
 * <p>
 * Each backend (OpenGL, Vulkan) implements this to provide backend-specific
 * rendering operations. DisplayWindow delegates all GPU work to the active backend.
 * <p>
 * When a backend is removed (e.g. OpenGL deprecation), simply delete its subclass.
 */
public abstract class BaseRenderBackend {
    protected BaseShader shader;
    protected BaseFramebuffer framebuffer;
    protected ColourScheme colourScheme;
    protected RenderElement.DisplayContext context;
    protected String version;

    // ===== Window creation =====

    public abstract void applyWindowHints();

    public abstract long createWindow(int winWidth, int winHeight, String title);

    // ===== Lifecycle =====

    public abstract void initialize(long window, ColourScheme colours, int fbScale,
                                    PerformanceInfo perfInfo, String mcVersion);

    public abstract void close();

    // ===== Context management =====

    public abstract void makeCurrent(long window);

    public abstract void releaseCurrent();

    public abstract void setVsync(boolean enabled);

    // ===== Frame rendering =====

    public abstract void beginFrame();

    public abstract void endFrame(int fbWidth, int fbHeight);

    // ===== Overlay rendering (handoff phase) =====

    public abstract void beginOverlay(int alpha);

    public abstract void endOverlay();

    // ===== Resource factory =====

    public abstract BaseShader createShader();

    public abstract BaseFramebuffer createFramebuffer(int width, int height, int scale, ColourScheme colours);

    public abstract VertexDataBuilder createBufferBuilder();

    public abstract void uploadTexture(ByteBuffer pixels, int width, int height, int slot);

    public abstract void uploadFontTexture(ByteBuffer alphaBitmap, int width, int height, int slot);

    // ===== Texture binding =====

    public abstract void bindTexture(long handle);

    public abstract void unbindTexture();

    // ===== Handoff =====

    public abstract void prepareHandoff(long window);

    // ===== Accessors =====

    public abstract String getVersion();

    public abstract long getFramebufferTextureHandle();

    public RenderElement.DisplayContext context() {
        return context;
    }
}
