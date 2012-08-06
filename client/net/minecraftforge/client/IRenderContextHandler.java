/*
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */
package net.minecraftforge.client;

public interface IRenderContextHandler
{
    /** Run before the specified rendering context.
     */
    public void beforeRenderContext();

    /** Run after the specified rendering context.
     */
    public void afterRenderContext();
}

