/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.tasks;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftsingularity.event.network.CustomPayloadEvent;
import net.minecraftsingularity.network.singularityPacketHandler;
import net.minecraftsingularity.network.NetworkInitialization;
import net.minecraftsingularity.network.config.ConfigurationTaskContext;
import net.minecraftsingularity.network.packets.Acknowledge;
import net.minecraftsingularity.network.packets.RegistryData;
import net.minecraftsingularity.network.packets.RegistryList;
import net.minecraftsingularity.registries.singularityRegistry.Snapshot;
import net.minecraftsingularity.registries.RegistryManager;

/**
 * Sends the list of known channels to the client as well as their specific versions.
 * Allows the client to do compatibility checking.
 */
@ApiStatus.Internal
public class SyncRegistriesTask implements ConfigurationTask {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("SYNC_REGISTRIES_TASK");
    public static final Type TYPE = new Type("singularity:sync_registries");

    private ConfigurationTaskContext taskCtx;
    private Map<Identifier, Snapshot> snapshot;
    private singularityPacketHandler handler;
    private int expectedToken;

    @Override
    public void start(ConfigurationTaskContext ctx) {
        this.taskCtx = ctx;
        var con = ctx.getConnection();
        handler = con.channel().attr(NetworkInitialization.CONTEXT).get();

        // If we're in memory, then don't actually sync the registries.
        if (!ctx.getConnection().isMemoryConnection())
            this.snapshot = RegistryManager.ACTIVE.takeSnapshot(false);

        expectedToken = handler.expectAck(this::sendRegistries);
        NetworkInitialization.CONFIG.send(new RegistryList(expectedToken), con);
    }

    private void sendRegistries(Acknowledge msg, CustomPayloadEvent.Context ctx) {
        if (msg.token() != expectedToken) {
            LOGGER.error(MARKER, "Received unknown acknowledgement received {} exptected {}", msg.token(), expectedToken);
            ctx.getConnection().disconnect(Component.literal("Illegal Acknowledge packet received, unknown token: " + msg.token()));
            return;
        }

        // We've got no more to send, so finish this task!
        if (this.snapshot == null || this.snapshot.isEmpty()) {
            taskCtx.finish(type());
            return;
        }

        // Send the next registry!
        var name = this.snapshot.keySet().iterator().next();
        var data = this.snapshot.remove(name);
        expectedToken = this.handler.expectAck(this::sendRegistries);
        NetworkInitialization.CONFIG.reply(new RegistryData(expectedToken, name, data), ctx);
    }

    @Override
    public void start(Consumer<Packet<?>> send) {
        throw new IllegalStateException("This should never be called");
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
