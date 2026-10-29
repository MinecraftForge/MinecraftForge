/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when pause is about to change
 *
 * <p>These events are fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ClientPauseChangeEvent.Pre
 * @see ClientPauseChangeEvent.Post
 *
 * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 */
@NullMarked
@Deprecated(forRemoval = true, since = "26.4")
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
