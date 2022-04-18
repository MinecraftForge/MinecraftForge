/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryManager;

import java.util.Arrays;
import java.util.List;

class NetworkInitialization {

    public static SimpleChannel getHandshakeChannel() {
        SimpleChannel handshakeChannel = NetworkRegistry.ChannelBuilder.
                named(NetworkConstants.FML_HANDSHAKE_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkConstants.NETVERSION).
                simpleChannel();

        handshakeChannel.messageBuilder(HandshakeMessages.C2SAcknowledge.class, 99, NetworkDirection.LOGIN_TO_SERVER).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.C2SAcknowledge::decode).
                encoder(HandshakeMessages.C2SAcknowledge::encode).
                consumer(HandshakeHandler.indexFirst(HandshakeHandler::handleClientAck)).
                add();

        handshakeChannel.messageBuilder(HandshakeMessages.S2CModList.class, 1, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.S2CModList::decode).
                encoder(HandshakeMessages.S2CModList::encode).
                markAsLoginPacket().
                consumer(HandshakeHandler.biConsumerFor(HandshakeHandler::handleServerModListOnClient)).
                add();

        handshakeChannel.messageBuilder(HandshakeMessages.C2SModListReply.class, 2, NetworkDirection.LOGIN_TO_SERVER).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.C2SModListReply::decode).
                encoder(HandshakeMessages.C2SModListReply::encode).
                consumer(HandshakeHandler.indexFirst(HandshakeHandler::handleClientModListOnServer)).
                add();

        handshakeChannel.messageBuilder(HandshakeMessages.S2CRegistry.class, 3, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.S2CRegistry::decode).
                encoder(HandshakeMessages.S2CRegistry::encode).
                buildLoginPacketList(RegistryManager::generateRegistryPackets). //TODO: Make this non-static, and store a cache on the client.
                consumer(HandshakeHandler.biConsumerFor(HandshakeHandler::handleRegistryMessage)).
                add();

        handshakeChannel.messageBuilder(HandshakeMessages.S2CConfigData.class, 4, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.S2CConfigData::decode).
                encoder(HandshakeMessages.S2CConfigData::encode).
                buildLoginPacketList(ConfigSync.INSTANCE::syncConfigs).
                consumer(HandshakeHandler.biConsumerFor(HandshakeHandler::handleConfigSync)).
                add();

        return handshakeChannel;
    }

    public static SimpleChannel getPlayChannel() {
         SimpleChannel playChannel = NetworkRegistry.ChannelBuilder.
                named(NetworkConstants.FML_PLAY_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkConstants.NETVERSION).
                simpleChannel();

        playChannel.messageBuilder(PlayMessages.SpawnEntity.class, 0).
                decoder(PlayMessages.SpawnEntity::decode).
                encoder(PlayMessages.SpawnEntity::encode).
                consumer(PlayMessages.SpawnEntity::handle).
                add();

        playChannel.messageBuilder(PlayMessages.OpenContainer.class,1).
                decoder(PlayMessages.OpenContainer::decode).
                encoder(PlayMessages.OpenContainer::encode).
                consumer(PlayMessages.OpenContainer::handle).
                add();

        return playChannel;
    }

    public static List<EventNetworkChannel> buildMCRegistrationChannels() {
        final EventNetworkChannel mcRegChannel = NetworkRegistry.ChannelBuilder.
                named(NetworkConstants.MC_REGISTER_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkConstants.NETVERSION).
                eventNetworkChannel();
        mcRegChannel.addListener(MCRegisterPacketHandler.INSTANCE::registerListener);
        final EventNetworkChannel mcUnregChannel = NetworkRegistry.ChannelBuilder.
                named(NetworkConstants.MC_UNREGISTER_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkConstants.NETVERSION).
                eventNetworkChannel();
        mcUnregChannel.addListener(MCRegisterPacketHandler.INSTANCE::unregisterListener);
        return Arrays.asList(mcRegChannel, mcUnregChannel);
    }
}
