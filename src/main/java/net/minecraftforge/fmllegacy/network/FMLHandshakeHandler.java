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

package net.minecraftforge.fmllegacy.network;

import com.google.common.collect.Multimap;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.minecraftforge.fmllegacy.util.ThreeConsumer;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static net.minecraftforge.registries.ForgeRegistry.REGISTRIES;

/**
 * Instance responsible for handling the overall FML network handshake.
 *
 * <p>An instance is created during {@link net.minecraft.network.handshake.client.CHandshakePacket} handling, and attached
 * to the {@link NetworkManager#channel} via {@link FMLNetworkConstants#FML_HANDSHAKE_HANDLER}.
 *
 * <p>The {@link FMLNetworkConstants#handshakeChannel} is a {@link SimpleChannel} with standard messages flowing in both directions.
 *
 * <p>The {@link #loginWrapper} transforms these messages into {@link net.minecraft.network.login.client.CCustomPayloadLoginPacket}
 * and {@link net.minecraft.network.login.server.SCustomPayloadLoginPacket} compatible messages, by means of wrapping.
 *
 * <p>The handshake is ticked {@link #tickLogin(NetworkManager)} from the {@link ServerLoginNetHandler#update()} method,
 * utilizing the {@link ServerLoginNetHandler.State#NEGOTIATING} state, which is otherwise unused in vanilla code.
 *
 * <p>During client to server initiation, on the <em>server</em>, the {@link NetworkEvent.GatherLoginPayloadsEvent} is fired,
 * which solicits all registered channels at the {@link NetworkRegistry} for any
 * {@link NetworkRegistry.LoginPayload} they wish to supply.
 *
 * <p>The collected {@link NetworkRegistry.LoginPayload} are sent, one per tick, via
 * the {@link FMLLoginWrapper#wrapPacket(ResourceLocation, PacketBuffer)} mechanism to the incoming client connection. Each
 * packet is indexed via {@link net.minecraft.network.login.client.CCustomPayloadLoginPacket#transaction}, which is
 * the only mechanism available for tracking request/response pairs.
 *
 * <p>Each packet sent from the server should be replied by the client, though not necessarily in sent order. The reply
 * should contain the index of the server's packet it is replying to. The {@link FMLLoginWrapper} class handles indexing
 * replies correctly automatically.
 *
 * <p>Once all packets have been dispatched, we wait for all replies to be received. Once all replies are received, the
 * final login phase will commence.
 */
public class FMLHandshakeHandler {
    static final Marker FMLHSMARKER = MarkerManager.getMarker("FMLHANDSHAKE").setParents(FMLNetworkConstants.NETWORK);
    private static final Logger LOGGER = LogManager.getLogger();

    private static final FMLLoginWrapper loginWrapper = new FMLLoginWrapper();

    static {
    }

    /**
     * Create a new handshake instance. Called when connection is first created during the {@link net.minecraft.network.handshake.client.CHandshakePacket}
     * handling.
     *
     * @param manager The network manager for this connection
     * @param direction The {@link NetworkDirection} for this connection: {@link NetworkDirection#LOGIN_TO_SERVER} or {@link NetworkDirection#LOGIN_TO_CLIENT}
     */
    static void registerHandshake(Connection manager, NetworkDirection direction) {
        manager.channel().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).compareAndSet(null, new FMLHandshakeHandler(manager, direction));
    }

    static boolean tickLogin(Connection networkManager)
    {
        return networkManager.channel().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).get().tickServer();
    }

    private List<NetworkRegistry.LoginPayload> messageList;

    private List<Integer> sentMessages = new ArrayList<>();

    private final NetworkDirection direction;
    private final Connection manager;
    private int packetPosition;
    private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;
    private Set<ResourceLocation> registriesToReceive;
    private Map<ResourceLocation, String> registryHashes;

    private FMLHandshakeHandler(Connection networkManager, NetworkDirection side)
    {
        this.direction = side;
        this.manager = networkManager;
        if (networkManager.isMemoryConnection()) {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, true);
            LOGGER.debug(FMLHSMARKER, "Starting local connection.");
        } else if (NetworkHooks.getConnectionType(()->this.manager)==ConnectionType.VANILLA) {
            this.messageList = Collections.emptyList();
            LOGGER.debug(FMLHSMARKER, "Starting new vanilla network connection.");
        } else {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, false);
            LOGGER.debug(FMLHSMARKER, "Starting new modded network connection. Found {} messages to dispatch.", this.messageList.size());
        }
    }

    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} based on the {@link #getHandshake(Supplier)} function.
     *
     * This should only be used for login message types.
     *
     * @param consumer A two argument instance method reference
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> biConsumerFor(ThreeConsumer<FMLHandshakeHandler, ? super MSG, ? super Supplier<NetworkEvent.Context>> consumer)
    {
        return (m, c) -> ThreeConsumer.bindArgs(consumer, m, c).accept(getHandshake(c));
    }

    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} {@link #biConsumerFor(ThreeConsumer)}, first calling the {@link #handleIndexedMessage(FMLHandshakeMessages.LoginIndexedMessage, Supplier)}
     * method to handle index tracking. Used for client to server replies.
     *
     * This should only be used for login messages.
     *
     * @param next The method reference to call after index handling
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> indexFirst(ThreeConsumer<FMLHandshakeHandler, MSG, Supplier<NetworkEvent.Context>> next)
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

    void handleServerModListOnClient(FMLHandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", String.join(", ", serverModList.getModList()));
        boolean accepted = NetworkRegistry.validateClientChannels(serverModList.getChannels());
        c.get().setPacketHandled(true);
        if (!accepted) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
            c.get().getNetworkManager().disconnect(new TextComponent("Connection closed - mismatched mod channel list"));
            return;
        }
        FMLNetworkConstants.handshakeChannel.reply(new FMLHandshakeMessages.C2SModListReply(), c.get());

        LOGGER.debug(FMLHSMARKER, "Accepted server connection");
        // Set the modded marker on the channel so we know we got packets
        c.get().getNetworkManager().channel().attr(FMLNetworkConstants.FML_NETVERSION).set(FMLNetworkConstants.NETVERSION);
        c.get().getNetworkManager().channel().attr(FMLNetworkConstants.FML_CONNECTION_DATA)
                .set(new FMLConnectionData(serverModList.getModList(), serverModList.getChannels()));

        this.registriesToReceive = new HashSet<>(serverModList.getRegistries());
        this.registrySnapshots = Maps.newHashMap();
        LOGGER.debug(REGISTRIES, "Expecting {} registries: {}", ()->this.registriesToReceive.size(), ()->this.registriesToReceive);
    }

    <MSG extends IntSupplier> void handleIndexedMessage(MSG message, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client indexed reply {} of type {}", message.getAsInt(), message.getClass().getName());
        boolean removed = this.sentMessages.removeIf(i-> i == message.getAsInt());
        if (!removed) {
            LOGGER.error(FMLHSMARKER, "Recieved unexpected index {} in client reply", message.getAsInt());
        }
    }

    void handleClientModListOnServer(FMLHandshakeMessages.C2SModListReply clientModList, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]",  String.join(", ", clientModList.getModList()));
        boolean accepted = NetworkRegistry.validateServerChannels(clientModList.getChannels());
        c.get().getNetworkManager().channel().attr(FMLNetworkConstants.FML_CONNECTION_DATA)
                .set(new FMLConnectionData(clientModList.getModList(), clientModList.getChannels()));
        c.get().setPacketHandled(true);
        if (!accepted) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
            c.get().getNetworkManager().disconnect(new TextComponent("Connection closed - mismatched mod channel list"));
            return;
        }
        LOGGER.debug(FMLHSMARKER, "Accepted client connection mod list");
    }

    void handleRegistryMessage(final FMLHandshakeMessages.S2CRegistry registryPacket, final Supplier<NetworkEvent.Context> contextSupplier){
        LOGGER.debug(FMLHSMARKER,"Received registry packet for {}", registryPacket.getRegistryName());
        this.registriesToReceive.remove(registryPacket.getRegistryName());
        this.registrySnapshots.put(registryPacket.getRegistryName(), registryPacket.getSnapshot());

        boolean continueHandshake = true;
        if (this.registriesToReceive.isEmpty()) {
            continueHandshake = handleRegistryLoading(contextSupplier);
        }
        // The handshake reply isn't sent until we have processed the message
        contextSupplier.get().setPacketHandled(true);
        if (!continueHandshake) {
            LOGGER.error(FMLHSMARKER, "Connection closed, not continuing handshake");
        } else {
            FMLNetworkConstants.handshakeChannel.reply(new FMLHandshakeMessages.C2SAcknowledge(), contextSupplier.get());
        }
    }

    private boolean handleRegistryLoading(final Supplier<NetworkEvent.Context> contextSupplier) {
        // We use a countdown latch to suspend the network thread pending the client thread processing the registry data
        AtomicBoolean successfulConnection = new AtomicBoolean(false);
        CountDownLatch block = new CountDownLatch(1);
        contextSupplier.get().enqueueWork(() -> {
            LOGGER.debug(FMLHSMARKER, "Injecting registry snapshot from server.");
            final Multimap<ResourceLocation, ResourceLocation> missingData = GameData.injectSnapshot(registrySnapshots, false, false);
            LOGGER.debug(FMLHSMARKER, "Snapshot injected.");
            if (!missingData.isEmpty()) {
                LOGGER.error(FMLHSMARKER, "Missing registry data for network connection:\n{}", LogMessageAdapter.adapt(sb->
                        missingData.forEach((reg, entry)-> sb.append("\t").append(reg).append(": ").append(entry).append('\n'))));
            }
            successfulConnection.set(missingData.isEmpty());
            block.countDown();
        });
        LOGGER.debug(FMLHSMARKER, "Waiting for registries to load.");
        try {
            block.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        if (successfulConnection.get()) {
            LOGGER.debug(FMLHSMARKER, "Registry load complete, continuing handshake.");
        } else {
            LOGGER.error(FMLHSMARKER, "Failed to load registry, closing connection.");
            this.manager.disconnect(new TextComponent("Failed to synchronize registry data from server, closing connection"));
        }
        return successfulConnection.get();
    }

    void handleClientAck(final FMLHandshakeMessages.C2SAcknowledge msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received acknowledgement from client");
        contextSupplier.get().setPacketHandled(true);
    }

    void handleConfigSync(final FMLHandshakeMessages.S2CConfigData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received config sync from server");
        ConfigSync.INSTANCE.receiveSyncedConfig(msg, contextSupplier);
        contextSupplier.get().setPacketHandled(true);
        FMLNetworkConstants.handshakeChannel.reply(new FMLHandshakeMessages.C2SAcknowledge(), contextSupplier.get());
    }
    /**
     * FML will send packets, from Server to Client, from the messages queue until the queue is drained. Each message
     * will be indexed, and placed into the "pending acknowledgement" queue.
     *
     * As indexed packets are received at the server, they will be removed from the "pending acknowledgement" queue.
     *
     * Once the pending queue is drained, this method returns true - indicating that login processing can proceed to
     * the next step.
     *
     * @return true if there is no more need to tick this login connection.
     */
    public boolean tickServer()
    {
        if (packetPosition < messageList.size()) {
            NetworkRegistry.LoginPayload message = messageList.get(packetPosition);

            LOGGER.debug(FMLHSMARKER, "Sending ticking packet info '{}' to '{}' sequence {}", message.getMessageContext(), message.getChannelName(), packetPosition);
            sentMessages.add(packetPosition);
            loginWrapper.sendServerToClientLoginPacket(message.getChannelName(), message.getData(), packetPosition, this.manager);
            packetPosition++;
        }

        // we're done when sentMessages is empty
        if (sentMessages.isEmpty() && packetPosition >= messageList.size()-1) {
            // clear ourselves - we're done!
            this.manager.channel().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).set(null);
            LOGGER.debug(FMLHSMARKER, "Handshake complete!");
            return true;
        }
        return false;
    }
}
