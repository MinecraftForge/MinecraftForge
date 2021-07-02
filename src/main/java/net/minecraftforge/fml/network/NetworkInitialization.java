/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.network;

import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryManager;

import java.util.Arrays;
import java.util.List;

class NetworkInitialization {

    public static SimpleChannel getHandshakeChannel() {
        SimpleChannel handshakeChannel = NetworkRegistry.ChannelBuilder.
                named(FMLNetworkConstants.FML_HANDSHAKE_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> FMLNetworkConstants.NETVERSION).
                simpleChannel();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.C2SAcknowledge.class, 99, NetworkDirection.LOGIN_TO_SERVER).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.C2SAcknowledge::decode).
                encoder(FMLHandshakeMessages.C2SAcknowledge::encode).
                consumer(FMLHandshakeHandler.indexFirst(FMLHandshakeHandler::handleClientAck)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CModList.class, 1, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CModList::decode).
                encoder(FMLHandshakeMessages.S2CModList::encode).
                markAsLoginPacket().
                consumer(FMLHandshakeHandler.biConsumerFor(FMLHandshakeHandler::handleServerModListOnClient)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.C2SModListReply.class, 2, NetworkDirection.LOGIN_TO_SERVER).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.C2SModListReply::decode).
                encoder(FMLHandshakeMessages.C2SModListReply::encode).
                consumer(FMLHandshakeHandler.indexFirst(FMLHandshakeHandler::handleClientModListOnServer)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CRegistry.class, 3, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CRegistry::decode).
                encoder(FMLHandshakeMessages.S2CRegistry::encode).
                buildLoginPacketList(RegistryManager::generateRegistryPackets). //TODO: Make this non-static, and store a cache on the client.
                consumer(FMLHandshakeHandler.biConsumerFor(FMLHandshakeHandler::handleRegistryMessage)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CConfigData.class, 4, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CConfigData::decode).
                encoder(FMLHandshakeMessages.S2CConfigData::encode).
                buildLoginPacketList(ConfigTracker.INSTANCE::syncConfigs).
                consumer(FMLHandshakeHandler.biConsumerFor(FMLHandshakeHandler::handleConfigSync)).
                add();

        return handshakeChannel;
    }

    public static SimpleChannel getPlayChannel() {
         SimpleChannel playChannel = NetworkRegistry.ChannelBuilder.
                named(FMLNetworkConstants.FML_PLAY_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> FMLNetworkConstants.NETVERSION).
                simpleChannel();

        playChannel.messageBuilder(FMLPlayMessages.SpawnEntity.class, 0).
                decoder(FMLPlayMessages.SpawnEntity::decode).
                encoder(FMLPlayMessages.SpawnEntity::encode).
                consumer(FMLPlayMessages.SpawnEntity::handle).
                add();

        playChannel.messageBuilder(FMLPlayMessages.OpenContainer.class,1).
                decoder(FMLPlayMessages.OpenContainer::decode).
                encoder(FMLPlayMessages.OpenContainer::encode).
                consumer(FMLPlayMessages.OpenContainer::handle).
                add();

        //TODO Dimensions..
//        playChannel.messageBuilder(FMLPlayMessages.DimensionInfoMessage.class, 2)
//                .decoder(FMLPlayMessages.DimensionInfoMessage::decode)
//                .encoder(FMLPlayMessages.DimensionInfoMessage::encode)
//                .consumer(FMLPlayMessages.DimensionInfoMessage::handle)
//                .add();

        playChannel.messageBuilder(FMLPlayMessages.SyncCustomTagTypes.class, 3).
              decoder(FMLPlayMessages.SyncCustomTagTypes::decode).
              encoder(FMLPlayMessages.SyncCustomTagTypes::encode).
              consumer(FMLPlayMessages.SyncCustomTagTypes::handle).
              add();

        return playChannel;
    }

    public static List<EventNetworkChannel> buildMCRegistrationChannels() {
        final EventNetworkChannel mcRegChannel = NetworkRegistry.ChannelBuilder.
                named(FMLNetworkConstants.MC_REGISTER_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> FMLNetworkConstants.NETVERSION).
                eventNetworkChannel();
        mcRegChannel.addListener(FMLMCRegisterPacketHandler.INSTANCE::registerListener);
        final EventNetworkChannel mcUnregChannel = NetworkRegistry.ChannelBuilder.
                named(FMLNetworkConstants.MC_UNREGISTER_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> FMLNetworkConstants.NETVERSION).
                eventNetworkChannel();
        mcUnregChannel.addListener(FMLMCRegisterPacketHandler.INSTANCE::unregisterListener);
        return Arrays.asList(mcRegChannel, mcUnregChannel);
    }
}
