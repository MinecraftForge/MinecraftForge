/*
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */
package net.minecraft.src.forge;

import net.minecraft.src.RenderGlobal;

public interface IRenderWorldLastHandler
{
    /** Called after rendering all the 3D data of the world.  This is
     * called before the user's tool is rendered, but otherwise after all
     * 3D content.  It is called twice in anaglyph mode.  This is intended
     * for rendering visual effect overlays into the world.
     */
    void onRenderWorldLast(RenderGlobal renderer, float partialTicks);
}

