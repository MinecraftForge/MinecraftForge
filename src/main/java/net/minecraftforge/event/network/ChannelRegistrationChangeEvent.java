/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.network;

import java.util.Collections;
import java.util.Set;

import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when the channel registration (see minecraft custom channel documentation) changes.
 *
 * It seems plausible that this will fire multiple times for the same state, depending on what the server is doing.
 * It just directly dispatches upon receipt.
 */
public class ChannelRegistrationChangeEvent extends Event {
    public enum Type {
        REGISTER, UNREGISTER;
    }

    private final Connection source;
    private final Type changeType;
    private final Set<ResourceLocation> channels;

    public ChannelRegistrationChangeEvent(Connection source, Type changeType, Set<ResourceLocation> channels) {
        this.source = source;
        this.changeType = changeType;
        this.channels = Collections.unmodifiableSet(channels);
    }

    public Type getType() {
        return changeType;
    }

    public Connection getSource() {
        return source;
    }

    public Set<ResourceLocation> getChannels() {
        return channels;
    }
}