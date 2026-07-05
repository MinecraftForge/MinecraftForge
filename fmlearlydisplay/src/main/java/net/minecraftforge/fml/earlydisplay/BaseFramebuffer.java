/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

public abstract class BaseFramebuffer implements AutoCloseable {
    public abstract void activate();
    public abstract void deactivate();
    public abstract void draw(int windowFBWidth, int windowFBHeight);
    public abstract long getTextureHandle();
    public abstract void close();
}
