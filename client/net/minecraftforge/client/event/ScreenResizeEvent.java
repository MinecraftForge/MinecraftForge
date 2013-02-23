/*
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client.event;

import net.minecraftforge.event.Event;

/**
 * 
 * @author zsawyer
 * 
 */
public class ScreenResizeEvent extends Event {
    public final int width;
    public final int height;

    public ScreenResizeEvent(int width, int height)
    {
        super();
        this.width = width;
        this.height = height;
    }    
}
