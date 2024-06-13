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
public final class NetworkInitialization {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("FORGE_NETWORK");
    public static final ResourceLocation LOGIN_NAME = ResourceLocation.fromNamespaceAndPath("forge", "login");
    private static final ResourceLocation HANDSHAKE_NAME = ResourceLocation.fromNamespaceAndPath("forge", "handshake");
    //private static final ResourceLocation PLAY_NAME = new ResourceLocation("forge", "network");
    public static final AttributeKey<ForgePacketHandler> CONTEXT = AttributeKey.newInstance(HANDSHAKE_NAME.toString());

    public static final SimpleChannel LOGIN = ChannelBuilder
        .named(LOGIN_NAME)
        .optional()
        .networkProtocolVersion(0)
        .attribute(CONTEXT, ForgePacketHandler::new) // Shared across all of our channels
        .simpleChannel()
            .login()
                .serverbound()
                    .add(LoginWrapper.class, LoginWrapper.STREAM_CODEC, ctx(ForgePacketHandler::handleLoginWrapper))
        .build();

    public static final SimpleChannel CONFIG = ChannelBuilder
        .named(HANDSHAKE_NAME)
        .optional()
        .networkProtocolVersion(0)
        .simpleChannel()
            .configuration()
                .serverbound()
                    .add(Acknowledge.class, Acknowledge.STREAM_CODEC, ctx(ForgePacketHandler::handleClientAck))
                .bidirectional()
                    .add(ModVersions.class, ModVersions.STREAM_CODEC, ctx(ForgePacketHandler::handleModVersions))
                    .add(ChannelVersions.class, ChannelVersions.STREAM_CODEC, ctx(ForgePacketHandler::handleChannelVersions))
                .clientbound()
                    .add(RegistryList.class, RegistryList.STREAM_CODEC, ctx(ForgePacketHandler::handleRegistryList))
                    .add(RegistryData.class, RegistryData.STREAM_CODEC, ctx(ForgePacketHandler::handleRegistryData))
                    .add(ConfigData.class, ConfigData.STREAM_CODEC, ctx(ForgePacketHandler::handleConfigSync))
                    .add(MismatchData.class, MismatchData.STREAM_CODEC, ctx(ForgePacketHandler::handleModMismatchData))
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
        void handle(ForgePacketHandler handler, MSG msg, CustomPayloadEvent.Context ctx);
    }

    private static <MSG> BiConsumer<MSG, CustomPayloadEvent.Context> ctx(Handler<MSG> handler) {
        return (msg, ctx) -> {
            var inst = ctx.getConnection().channel().attr(CONTEXT).get();
            handler.handle(inst, msg, ctx);
        };
    }
}
