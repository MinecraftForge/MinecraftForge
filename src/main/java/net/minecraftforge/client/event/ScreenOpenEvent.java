/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.gui.screens.Screen;

/**
 * This event is called before any Screen will open.
 * If you don't want this to happen, cancel the event.
 * If you want to override this Screen, simply set the screen variable to your own Screen.
 *
 */
@Cancelable
public class ScreenOpenEvent extends Event
{
    private Screen screen;
    public ScreenOpenEvent(Screen screen)
    {
        this.setScreen(screen);
    }

    public Screen getScreen()
    {
        return screen;
    }

    public void setScreen(Screen screen)
    {
        this.screen = screen;
    }
}
