/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network;

import io.netty.util.AttributeKey;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.event.network.CustomPayloadEvent;
import net.minecraftsingularity.network.packets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

@ApiStatus.Internal
public final class NetworkInitialization {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("singularity_NETWORK");
    public static final Identifier LOGIN_NAME = Identifier.fromNamespaceAndPath("singularity", "login");
    private static final Identifier HANDSHAKE_NAME = Identifier.fromNamespaceAndPath("singularity", "handshake");
    //private static final Identifier PLAY_NAME = new Identifier("singularity", "network");
    public static final AttributeKey<singularityPacketHandler> CONTEXT = AttributeKey.newInstance(HANDSHAKE_NAME.toString());

    public static final SimpleChannel LOGIN = ChannelBuilder
        .named(LOGIN_NAME)
        .optional()
        .networkProtocolVersion(0)
        .attribute(CONTEXT, singularityPacketHandler::new) // Shared across all of our channels
        .simpleChannel()
            .login()
                .serverbound()
                    .add(LoginWrapper.class, LoginWrapper.STREAM_CODEC, ctx(singularityPacketHandler::handleLoginWrapper))
        .build();

    public static final SimpleChannel CONFIG = ChannelBuilder
        .named(HANDSHAKE_NAME)
        .optional()
        .networkProtocolVersion(0)
        .simpleChannel()
            .configuration()
                .serverbound()
                    .add(Acknowledge.class, Acknowledge.STREAM_CODEC, ctx(singularityPacketHandler::handleClientAck))
                .bidirectional()
                    .add(ModVersions.class, ModVersions.STREAM_CODEC, ctx(singularityPacketHandler::handleModVersions))
                    .add(ChannelVersions.class, ChannelVersions.STREAM_CODEC, ctx(singularityPacketHandler::handleChannelVersions))
                .clientbound()
                    .add(RegistryList.class, RegistryList.STREAM_CODEC, ctx(singularityPacketHandler::handleRegistryList))
                    .add(RegistryData.class, RegistryData.STREAM_CODEC, ctx(singularityPacketHandler::handleRegistryData))
                    .add(ConfigData.class, ConfigData.STREAM_CODEC, ctx(singularityPacketHandler::handleConfigSync))
                    .add(MismatchData.class, MismatchData.STREAM_CODEC, ctx(singularityPacketHandler::handleModMismatchData))
            .play() // TODO: Move to it's own channel, so that we can keep the core handshake channel clean/simple and thus not need to bump the version ever As it is the one responsible for validating versions
                .clientbound()
                    .addMain(SpawnEntity.class, SpawnEntity.STREAM_CODEC, SpawnEntity::handle)
                    .addMain(OpenContainer.class, OpenContainer.STREAM_CODEC, OpenContainer::handle)
        .build();

    public static final SimpleChannel PLAY = CONFIG;;

    public static void init() {
        for (var channel : new Channel[]{ LOGIN, CONFIG, PLAY, ChannelListManager.CHANNEL})
            LOGGER.debug(MARKER, "Registering Network {} v{}", channel.getName(), channel.getProtocolVersion());
    }

    public static int getVersion() {
        return CONFIG.getProtocolVersion();
    }

    private interface Handler<MSG> {
        void handle(singularityPacketHandler handler, MSG msg, CustomPayloadEvent.Context ctx);
    }

    private static <MSG> BiConsumer<MSG, CustomPayloadEvent.Context> ctx(Handler<MSG> handler) {
        return (msg, ctx) -> {
            var inst = ctx.getConnection().channel().attr(CONTEXT).get();
            handler.handle(inst, msg, ctx);
        };
    }
}
