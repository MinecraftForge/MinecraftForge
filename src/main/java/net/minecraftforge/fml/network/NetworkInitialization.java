/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.util.ThreeConsumer;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

class NetworkInitialization {

    public static SimpleChannel getHandshakeChannel() {
        SimpleChannel handshakeChannel = NetworkRegistry.ChannelBuilder.
                named(FMLNetworkConstants.FML_HANDSHAKE_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> FMLNetworkConstants.NETVERSION).
                simpleChannel();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.C2SAcknowledge.class, 99).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.C2SAcknowledge::decode).
                encoder(FMLHandshakeMessages.C2SAcknowledge::encode).
                consumer(indexFirst(FMLHandshakeHandler::handleClientAck)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CModList.class, 1).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CModList::decode).
                encoder(FMLHandshakeMessages.S2CModList::encode).
                markAsLoginPacket().
                consumer(biConsumerFor(FMLHandshakeHandler::handleServerModListOnClient)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.C2SModListReply.class, 2).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.C2SModListReply::decode).
                encoder(FMLHandshakeMessages.C2SModListReply::encode).
                consumer(indexFirst(FMLHandshakeHandler::handleClientModListOnServer)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CRegistry.class, 3).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CRegistry::decode).
                encoder(FMLHandshakeMessages.S2CRegistry::encode).
                buildLoginPacketList(RegistryManager::generateRegistryPackets). //TODO: Make this non-static, and store a cache on the client.
                consumer(biConsumerFor(FMLHandshakeHandler::handleRegistryMessage)).
                add();

        handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CConfigData.class, 4).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CConfigData::decode).
                encoder(FMLHandshakeMessages.S2CConfigData::encode).
                buildLoginPacketList(ConfigTracker.INSTANCE::syncConfigs).
                consumer(biConsumerFor(FMLHandshakeHandler::handleConfigSync)).
                add();

        return handshakeChannel;
    }

    public static SimpleChannel getPlayChannel() {
         SimpleChannel playChannel = NetworkRegistry.ChannelBuilder
                .named(FMLNetworkConstants.FML_PLAY_RESOURCE)
                .clientAcceptedVersions(a -> true)
                .serverAcceptedVersions(a -> true)
                .networkProtocolVersion(() -> FMLNetworkConstants.NETVERSION)
                .simpleChannel();

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

        return playChannel;
    }


    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} based on the {@link #getHandshake(Supplier)} function.
     *
     * @param consumer A two argument instance method reference
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    private static <MSG extends FMLHandshakeMessages.LoginIndexedMessage> BiConsumer<MSG, Supplier<NetworkEvent.Context>> biConsumerFor(ThreeConsumer<FMLHandshakeHandler, ? super MSG, ? super Supplier<NetworkEvent.Context>> consumer)
    {
        return (m, c) -> ThreeConsumer.bindArgs(consumer, m, c).accept(getHandshake(c));
    }

    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} {@link #biConsumerFor(ThreeConsumer)}, first calling the {@link #handleIndexedMessage(FMLHandshakeMessages.LoginIndexedMessage, Supplier)}
     * method to handle index tracking. Used for client to server replies.
     * @param next The method reference to call after index handling
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    private static <MSG extends FMLHandshakeMessages.LoginIndexedMessage> BiConsumer<MSG, Supplier<NetworkEvent.Context>> indexFirst(ThreeConsumer<FMLHandshakeHandler, MSG, Supplier<NetworkEvent.Context>> next)
    {
        final BiConsumer<MSG, Supplier<NetworkEvent.Context>> loginIndexedMessageSupplierBiConsumer = biConsumerFor(FMLHandshakeHandler::handleIndexedMessage);
        return loginIndexedMessageSupplierBiConsumer.andThen(biConsumerFor(next));
    }

    /**
     * Retrieve the handshake from the {@link NetworkEvent.Context}
     *
     * @param contextSupplier the {@link NetworkEvent.Context}
     * @return The handshake handler for the connection
     */
    private static FMLHandshakeHandler getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
        return contextSupplier.get().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).get();
    }
}
