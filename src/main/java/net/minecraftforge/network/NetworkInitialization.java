/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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

        handshakeChannel.messageBuilder(HandshakeMessages.S2CModData.class, 5, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.S2CModData::decode).
                encoder(HandshakeMessages.S2CModData::encode).
                markAsLoginPacket().
                consumer(HandshakeHandler.biConsumerFor(HandshakeHandler::handleModData)).
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

        handshakeChannel.messageBuilder(HandshakeMessages.S2CModMismatchData.class, 6, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(HandshakeMessages.S2CModMismatchData::decode).
                encoder(HandshakeMessages.S2CModMismatchData::encode).
                consumer(HandshakeHandler.biConsumerFor(HandshakeHandler::handleModMismatchData)).
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
