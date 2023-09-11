/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.network;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a network connection is started, either on the server when it receives the
 * ClientIntentionPacket or on the client when the channel is first activated. This is
 * intended to allow modders to attach things to the channel that can be used in the future.
 *
 * As this is a blocking event modders can also do things like load data. Need some example uses.
 */
public class ConnectionStartEvent extends Event {
    private final Connection connection;

    @ApiStatus.Internal
    public ConnectionStartEvent(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isClient() {
        return this.connection.getSending() == PacketFlow.SERVERBOUND;
    }
}
