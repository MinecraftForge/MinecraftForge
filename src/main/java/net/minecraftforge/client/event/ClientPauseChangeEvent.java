/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when pause is about to change
 *
 * <p>These events are fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ClientPauseChangeEvent.Pre
 * @see ClientPauseChangeEvent.Post
 */
@NullMarked
public sealed interface ClientPauseChangeEvent extends InheritableEvent {
    EventBus<ClientPauseChangeEvent> BUS = EventBus.create(ClientPauseChangeEvent.class);

    /**
     * {@return whether the game is paused}
     */
    boolean isPaused();

    /**
     * Fired when {@linkplain Minecraft#pause pause} is going to change
     *
     * <p>This event is {@linkplain Cancellable cancellable}.</p>
     * Cancelling this event will prevent the game change pause state even if the conditions match
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Pre(boolean isPaused) implements Cancellable, RecordEvent, ClientPauseChangeEvent {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
    }

    /**
     * Fired when {@linkplain Minecraft#pause pause} is already changed
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Post(boolean isPaused) implements RecordEvent, ClientPauseChangeEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);
    }
}
