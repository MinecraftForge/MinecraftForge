/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;

/**
 * Fired when the client queues a {@link Toast} message to be shown onscreen.
 * Toasts are small popups that appear on the top right of the screen, for certain actions such as unlocking Advancements and Recipes.
 *
 * <p>This event is {@linkplain Cancellable cancellable}.
 * Cancelling the event stops the toast from being queued, which means it never renders.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public record ToastAddEvent(Toast getToast) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<ToastAddEvent> BUS = CancellableEventBus.create(ToastAddEvent.class);
}
