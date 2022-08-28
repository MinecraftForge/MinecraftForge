/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when the client queues a {@link Toast} message to be shown onscreen.
 * Toasts are small popups that appear on the top right of the screen, for certain actions such as unlocking Advancements and Recipes.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * Cancelling the event stops the toast from being queued, which means it never renders.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@Cancelable
public class ToastAddEvent extends Event
{
    private final Toast toast;

    public ToastAddEvent(Toast toast)
    {
        this.toast = toast;
    }

    public Toast getToast()
    {
        return toast;
    }
}
