/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before any {@link Screen} will be opened, to allow changing or preventing it from being opened. All screen
 * layers on the screen are closed before this event is fired.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the {@code Screen} shall be prevented from opening and any previous screen
 * will remain open. However, cancelling this event will not prevent the closing of screen layers which happened before
 * this event fired.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
@Cancelable
public class ScreenOpenEvent extends Event
{
    private Screen screen;

    /**
     * @hidden
     */
    public ScreenOpenEvent(Screen screen)
    {
        this.setScreen(screen);
    }

    /**
     * {@return the screen that will be opened, if the event is not cancelled}
     */
    public Screen getScreen()
    {
        return screen;
    }

    /**
     * Sets the new screen to be opened, if the event is not cancelled.
     *
     * @param screen the new screen
     */
    public void setScreen(Screen screen)
    {
        this.screen = screen;
    }
}
