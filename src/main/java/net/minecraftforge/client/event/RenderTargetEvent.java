/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Super event for all render target events.
 */
public abstract class RenderTargetEvent extends Event
{
    private final int width;
    private final int height;
    private final boolean isOnOSX;

    @ApiStatus.Internal
    protected RenderTargetEvent(int width, int height, boolean isOnOSX)
    {
        this.width = width;
        this.height = height;
        this.isOnOSX = isOnOSX;
    }

    /**
     * The width of the screen in pixels.
     *
     * @return The screen width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * The height of the screen in pixels.
     *
     * @return The screen height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Indicates if the game is running on OSX.
     *
     * @return True if the game is running on OSX, false otherwise
     */
    public boolean isOnOSX()
    {
        return isOnOSX;
    }

    /**
     * Event fired when render target should be created
     */
    public static class Create extends RenderTargetEvent implements IModBusEvent
    {
        @ApiStatus.Internal
        public Create(int width, int height, boolean isOnOSX)
        {
            super(width, height, isOnOSX);
        }
    }

    /**
     * Event fired when your custom render targets created during {@link Create} should be resized
     */
    public static class Resize extends RenderTargetEvent
    {
        @ApiStatus.Internal
        public Resize(int width, int height, boolean isOnOSX)
        {
            super(width, height, isOnOSX);
        }
    }
}
