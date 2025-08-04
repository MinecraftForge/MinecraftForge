/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when pause is about to change
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ClientPauseChangeEvent.Pre
 * @see ClientPauseChangeEvent.Post
 */
public abstract sealed class ClientPauseChangeEvent extends MutableEvent implements InheritableEvent {
    public static final EventBus<ClientPauseChangeEvent> BUS = EventBus.create(ClientPauseChangeEvent.class);

    private final boolean pause;

    public ClientPauseChangeEvent(boolean pause) {
        this.pause = pause;
    }

    /**
     * Fired when {@linkplain Minecraft#pause pause} is going to change
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     * Cancelling this event will prevent the game change pause state even if the conditions match
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static final class Pre extends ClientPauseChangeEvent implements Cancellable {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

        public Pre(boolean pause) {
            super(pause);
        }
    }

    /**
     * Fired when {@linkplain Minecraft#pause pause} is already changed
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static final class Post extends ClientPauseChangeEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        public Post(boolean pause) {
            super(pause);
        }
    }

    /**
     * {@return game is paused}
     */
    public boolean isPaused() {
        return pause;
    }
}