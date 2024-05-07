/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import io.netty.util.AttributeKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.packets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

@ApiStatus.Internal
public class NetworkInitialization {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("FORGE_NETWORK");
    public static final ResourceLocation LOGIN_NAME = new ResourceLocation("forge", "login");
    public static final ResourceLocation HANDSHAKE_NAME = new ResourceLocation("forge", "handshake");
    public static final ResourceLocation PLAY_NAME = new ResourceLocation("forge", "play");
    public static final AttributeKey<ForgePacketHandler> CONTEXT = AttributeKey.newInstance(HANDSHAKE_NAME.toString());

    public static SimpleChannel LOGIN = ChannelBuilder
        .named(LOGIN_NAME)
        .optional()
        .networkProtocolVersion(0)
        .attribute(CONTEXT, ForgePacketHandler::new) // Shared across all of our channels
        .simpleChannel()
        .login()
            .serverbound()
                .add(LoginWrapper.class, LoginWrapper.STREAM_CODEC, ctx(ForgePacketHandler::handleLoginWrapper))
        .build();

    public static SimpleChannel CONFIG = ChannelBuilder
        .named(HANDSHAKE_NAME)
        .optional()
        .networkProtocolVersion(0)
        .simpleChannel()
        .configuration()
            .bidirectional()
                .add(ModVersions.class, ModVersions.STREAM_CODEC, ctx(ForgePacketHandler::handleModVersions))
                .add(ChannelVersions.class, ChannelVersions.STREAM_CODEC, ctx(ForgePacketHandler::handleChannelVersions))
            .serverbound()
                .add(Acknowledge.class, Acknowledge.STREAM_CODEC, ctx(ForgePacketHandler::handleClientAck))
            .clientbound()
                .add(RegistryList.class, RegistryList.STREAM_CODEC, ctx(ForgePacketHandler::handleRegistryList))
                .add(RegistryData.class, RegistryData.STREAM_CODEC, ctx(ForgePacketHandler::handleRegistryData))
                .add(ConfigData.class, ConfigData.STREAM_CODEC, ctx(ForgePacketHandler::handleConfigSync))
                .add(MismatchData.class, MismatchData.STREAM_CODEC, ctx(ForgePacketHandler::handleModMismatchData))
        .build();

    public static SimpleChannel PLAY = ChannelBuilder
        .named(PLAY_NAME)
        .optional()
        .networkProtocolVersion(0)
        .simpleChannel()
        .play()
            .clientbound()
                .add(SpawnEntity.class, SpawnEntity.STREAM_CODEC, SpawnEntity::handle)
            .bidirectional()
                // This was being registered bidirectionally on the original code. Seems its suppose to be S2C only
                .add(OpenContainer.class, OpenContainer.STREAM_CODEC, OpenContainer::handle)
        .build();

    public static void init() {
        for (var channel : new Channel[]{ LOGIN, CONFIG, PLAY, ChannelListManager.REGISTER, ChannelListManager.UNREGISTER})
            LOGGER.debug(MARKER, "Registering Network {} v{}", channel.getName(), channel.getProtocolVersion());
    }

    public static int getVersion() {
        return PLAY.getProtocolVersion();
    }

    private interface Handler<MSG> {
        void handle(ForgePacketHandler handler, MSG msg, CustomPayloadEvent.Context ctx);
    }

    private static <MSG> BiConsumer<MSG, CustomPayloadEvent.Context> ctx(Handler<MSG> handler) {
        return (msg, ctx) -> {
            var inst = ctx.getConnection().channel().attr(CONTEXT).get();
            handler.handle(inst, msg, ctx);
        };
    }
}
