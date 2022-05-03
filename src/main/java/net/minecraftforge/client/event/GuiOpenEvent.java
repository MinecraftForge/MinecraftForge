/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.gui.screen.Screen;

/**
 * This event is called before any Gui will open.
 * If you don't want this to happen, cancel the event.
 * If you want to override this Gui, simply set the gui variable to your own Gui.
 *
 */
@Cancelable
public class GuiOpenEvent extends Event
{
    private Screen gui;
    public GuiOpenEvent(Screen gui)
    {
        this.setGui(gui);
    }

    public Screen getGui()
    {
        return gui;
    }

    public void setGui(Screen gui)
    {
        this.gui = gui;
    }
}
