/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;

import io.netty.util.AttributeKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.packets.Acknowledge;
import net.minecraftforge.network.packets.ChannelVersions;
import net.minecraftforge.network.packets.LoginWrapper;
import net.minecraftforge.network.packets.OpenContainer;
import net.minecraftforge.network.packets.RegistryList;
import net.minecraftforge.network.packets.RegistryData;
import net.minecraftforge.network.packets.ConfigData;
import net.minecraftforge.network.packets.MismatchData;
import net.minecraftforge.network.packets.ModVersions;
import net.minecraftforge.network.packets.SpawnEntity;

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
        .simpleChannel()

        .messageBuilder(LoginWrapper.class)
            .decoder(LoginWrapper::decode)
            .encoder(LoginWrapper::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleLoginWrapper)
            .add();

    public static SimpleChannel PLAY = ChannelBuilder
        .named(HANDSHAKE_NAME)
        .optional()
        .networkProtocolVersion(0)
        .attribute(CONTEXT, ForgePacketHandler::new)
        .simpleChannel()

        .messageBuilder(Acknowledge.class, NetworkDirection.PLAY_TO_SERVER)
            .decoder(Acknowledge::decode)
            .encoder(Acknowledge::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleClientAck)
            .add()

        .messageBuilder(ModVersions.class)
            .decoder(ModVersions::decode)
            .encoder(ModVersions::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleModVersions)
            .add()

        .messageBuilder(ChannelVersions.class)
            .decoder(ChannelVersions::decode)
            .encoder(ChannelVersions::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleChannelVersions)
            .add()

        .messageBuilder(RegistryList.class, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(RegistryList::decode)
            .encoder(RegistryList::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleRegistryList)
            .add()

        .messageBuilder(RegistryData.class, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(RegistryData::decode)
            .encoder(RegistryData::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleRegistryData)
            .add()

        .messageBuilder(ConfigData.class, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(ConfigData::decode)
            .encoder(ConfigData::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleConfigSync)
            .add()

        .messageBuilder(MismatchData.class, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(MismatchData::decode)
            .encoder(MismatchData::encode)
            .consumerNetworkThread(CONTEXT, ForgePacketHandler::handleModMismatchData)
            .add()

        .messageBuilder(SpawnEntity.class, NetworkDirection.PLAY_TO_CLIENT)
           .decoder(SpawnEntity::decode)
           .encoder(SpawnEntity::encode)
           .consumerMainThread(SpawnEntity::handle)
           .add()

       .messageBuilder(OpenContainer.class)
           .decoder(OpenContainer::decode)
           .encoder(OpenContainer::encode)
           .consumerMainThread(OpenContainer::handle)
           .add();

    public static void init() {
        for (var channel : new Channel[]{ LOGIN, PLAY, ChannelListManager.REGISTER, ChannelListManager.UNREGISTER})
            LOGGER.debug(MARKER, "Registering Network {} v{}", channel.getName(), channel.getProtocolVersion());
    }

    public static int getVersion() {
        return PLAY.getProtocolVersion();
    }
}
