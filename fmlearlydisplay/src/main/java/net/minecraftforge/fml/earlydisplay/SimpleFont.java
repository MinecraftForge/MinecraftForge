/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

/**
 * OpenGL-specific font class that extends {@link FontRasterizer} with GL texture upload.
 * <p>
 * This class can be removed when OpenGL support is dropped. In the Vulkan path,
 * {@link FontRasterizer} is used directly and the backend handles texture upload.
 */
public class SimpleFont extends FontRasterizer {
    public SimpleFont(String fontName, int bufferSize, int textureSlot, BaseRenderBackend backend) {
        super(fontName, bufferSize);
        backend.uploadFontTexture(alphaBitmap(), bitmapWidth(), bitmapHeight(), textureSlot);
        setTextureSlot(textureSlot);
    }
}
