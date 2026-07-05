/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

public abstract class BaseShader implements AutoCloseable {
    public enum RenderType {
        FONT, TEXTURE, BAR;
    }

    public abstract void init();
    public abstract void activate();
    public abstract void clear();
    public abstract void updateTextureUniform(int textureNumber);
    public abstract void updateScreenSizeUniform(int width, int height);
    public abstract void updateRenderTypeUniform(RenderType type);
    public abstract void close();
}
