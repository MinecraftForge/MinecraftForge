/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.network;

import java.util.Collections;
import java.util.Set;

import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;

/**
 * Fired when the channel registration (see minecraft custom channel documentation) changes.
 * <br>
 * It seems plausible that this will fire multiple times for the same state, depending on what the server is doing.
 * It just directly dispatches upon receipt.
 *
 * @param getType The type of change, either {@link Type#REGISTER} or {@link Type#UNREGISTER}
 */
public record ChannelRegistrationChangeEvent(
        Connection getSource,
        Type getType,
        Set<ResourceLocation> getChannels
) implements RecordEvent {
    public static final EventBus<ChannelRegistrationChangeEvent> BUS = EventBus.create(ChannelRegistrationChangeEvent.class);

    public enum Type {
        REGISTER, UNREGISTER
    }

    public ChannelRegistrationChangeEvent {
        getChannels = Collections.unmodifiableSet(getChannels);
    }
}
