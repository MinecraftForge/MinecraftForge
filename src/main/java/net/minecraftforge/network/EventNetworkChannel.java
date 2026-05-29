/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.event.network.CustomPayloadEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An event-bus like object on which {@link CustomPayloadEvent}s are posted.
 *
 * These events are fired from the network thread, and so should not interact with most game state by default.
 * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or client
 * thread.
 *
 * @see ChannelBuilder#newEventChannel(Identifier, Supplier, Predicate, Predicate)
 */
public class EventNetworkChannel extends Channel<FriendlyByteBuf> {
    EventNetworkChannel(NetworkInstance instance) {
        super(instance);
    }

    public EventNetworkChannel addListener(Consumer<CustomPayloadEvent> eventListener) {
        instance.addListener(eventListener);
        return this;
    }

    @Override
    public void encode(FriendlyByteBuf out, FriendlyByteBuf message) {
        out.writeBytes(message.slice());
    }
}
