/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.network.ConnectionData.ModMismatchData;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Instance responsible for handling the overall FML impl handshake.
 *
 * <p>An instance is created during {@link ClientIntentionPacket} handling, and attached
 * to the {@link Connection#channel()} via {@link NetworkConstants#FML_HANDSHAKE_HANDLER}.
 *
 * <p>The {@link NetworkConstants#handshakeChannel} is a {@link SimpleChannel} with standard messages flowing in both directions.
 *
 * <p>The {@link #loginWrapper} transforms these messages into {@link ServerboundCustomQueryPacket}
 * and {@link ClientboundCustomQueryPacket} compatible messages, by means of wrapping.
 *
 * <p>The handshake is ticked {@code #tickLogin(NetworkManager)} from the {@link ServerLoginPacketListenerImpl#tick()} method,
 * utilizing the {@code ServerLoginPacketListenerImpl.State#NEGOTIATING} state, which is otherwise unused in vanilla code.
 *
 * <p>During client to server initiation, on the <em>server</em>, the {@link NetworkEvent.GatherLoginPayloadsEvent} is fired,
 * which solicits all registered channels at the {@link NetworkRegistry} for any
 * {@link NetworkRegistry.LoginPayload} they wish to supply.
 *
 * <p>The collected {@link NetworkRegistry.LoginPayload} are sent, one per tick, via
 * the {@code FMLLoginWrapper#wrapPacket(ResourceLocation, net.minecraft.impl.FriendlyByteBuf)} mechanism to the incoming client connection. Each
 * packet is indexed via {@link ServerboundCustomQueryPacket#getTransactionId()}, which is
 * the only mechanism available for tracking request/response pairs.
 *
 * <p>Each packet sent from the server should be replied by the client, though not necessarily in sent order. The reply
 * should contain the index of the server's packet it is replying to. The {@link LoginWrapper} class handles indexing
 * replies correctly automatically.
 *
 * <p>Once all packets have been dispatched, we wait for all replies to be received. Once all replies are received, the
 * final login phase will commence.
 */
public class HandshakeHandler
{
    static final Marker FMLHSMARKER = MarkerManager.getMarker("FMLHANDSHAKE").setParents(NetworkConstants.NETWORK);
    private static final Logger LOGGER = LogManager.getLogger();

    private static final LoginWrapper loginWrapper = new LoginWrapper();

    static {
    }

    /**
     * Create a new handshake instance. Called when connection is first created during the {@link ClientIntentionPacket}
     * handling.
     *
     * @param manager The impl manager for this connection
     * @param direction The {@link NetworkDirection} for this connection: {@link NetworkDirection#LOGIN_TO_SERVER} or {@link NetworkDirection#LOGIN_TO_CLIENT}
     */
    static void registerHandshake(Connection manager, NetworkDirection direction) {
        manager.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).compareAndSet(null, new HandshakeHandler(manager, direction));
    }

    static boolean tickLogin(Connection networkManager)
    {
        return networkManager.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get().tickServer();
    }

    private List<NetworkRegistry.LoginPayload> messageList;

    private List<Integer> sentMessages = new ArrayList<>();

    private final NetworkDirection direction;
    private final Connection manager;
    private int packetPosition;
    private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;
    private Set<ResourceLocation> registriesToReceive;
    private Map<ResourceLocation, String> registryHashes;
    private boolean negotiationStarted = false;
    private final List<Future<Void>> pendingFutures = new ArrayList<>();

    private HandshakeHandler(Connection networkManager, NetworkDirection side)
    {
        this.direction = side;
        this.manager = networkManager;
        if (networkManager.isMemoryConnection()) {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, true);
            LOGGER.debug(FMLHSMARKER, "Starting local connection.");
        } else if (NetworkHooks.getConnectionType(()->this.manager)== ConnectionType.VANILLA) {
            this.messageList = Collections.emptyList();
            LOGGER.debug(FMLHSMARKER, "Starting new vanilla impl connection.");
        } else {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, false);
            LOGGER.debug(FMLHSMARKER, "Starting new modded impl connection. Found {} messages to dispatch.", this.messageList.size());
        }
    }

    @FunctionalInterface
    public interface HandshakeConsumer<MSG extends IntSupplier>
    {
        void accept(HandshakeHandler handler, MSG msg, Supplier<NetworkEvent.Context> context);
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
    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> biConsumerFor(HandshakeConsumer<MSG> consumer)
    {
        return (m, c) -> consumer.accept(getHandshake(c), m, c);
    }

    /**
     * Transforms a two-argument instance method reference into a {@link BiConsumer} {@link #biConsumerFor(HandshakeConsumer)}, first calling the {@link #handleIndexedMessage(IntSupplier, Supplier)}
     * method to handle index tracking. Used for client to server replies.
     *
     * This should only be used for login messages.
     *
     * @param next The method reference to call after index handling
     * @param <MSG> message type
     * @return A {@link BiConsumer} for use in message handling
     */
    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> indexFirst(HandshakeConsumer<MSG> next)
    {
        final BiConsumer<MSG, Supplier<NetworkEvent.Context>> loginIndexedMessageSupplierBiConsumer = biConsumerFor(HandshakeHandler::handleIndexedMessage);
        return loginIndexedMessageSupplierBiConsumer.andThen(biConsumerFor(next));
    }

    /**
     * Retrieve the handshake from the {@link NetworkEvent.Context}
     *
     * @param contextSupplier the {@link NetworkEvent.Context}
     * @return The handshake handler for the connection
     */
    private static HandshakeHandler getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
        return contextSupplier.get().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get();
    }

    void handleServerModListOnClient(HandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", String.join(", ", serverModList.getModList()));
        Map<ResourceLocation, String> mismatchedChannels = NetworkRegistry.validateClientChannels(serverModList.getChannels());
        c.get().setPacketHandled(true);
        //The connection data needs to be modified before a new ModMismatchData instance could be constructed
        NetworkHooks.appendConnectionData(c.get().getNetworkManager(), serverModList.getModList().stream().collect(Collectors.toMap(Function.identity(), s -> Pair.of("", ""))), serverModList.getChannels());
        if (!mismatchedChannels.isEmpty()) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
            //Populate the mod mismatch attribute with a new mismatch data instance to indicate that the disconnect happened due to a mod mismatch
            c.get().getNetworkManager().channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).set(ModMismatchData.channel(mismatchedChannels, NetworkHooks.getConnectionData(c.get().getNetworkManager()), true));
            c.get().getNetworkManager().disconnect(Component.literal("Connection closed - mismatched mod channel list"));
            return;
        }
        // Validate synced custom datapack registries, client cannot be missing any present on the server.
        List<String> missingDataPackRegistries = new ArrayList<>();
        Set<ResourceKey<? extends Registry<?>>> clientDataPackRegistries = DataPackRegistriesHooks.getSyncedCustomRegistries();
        for (ResourceKey<? extends Registry<?>> key : serverModList.getCustomDataPackRegistries())
        {
            if (!clientDataPackRegistries.contains(key))
            {
                ResourceLocation location = key.location();
                LOGGER.error(FMLHSMARKER, "Missing required datapack registry: {}", location);
                missingDataPackRegistries.add(key.location().toString());
            }
        }
        if (!missingDataPackRegistries.isEmpty())
        {
            c.get().getNetworkManager().disconnect(Component.translatable("fml.menu.multiplayer.missingdatapackregistries", String.join(", ", missingDataPackRegistries)));
            return;
        }
        NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SModListReply(), c.get());

        LOGGER.debug(FMLHSMARKER, "Accepted server connection");
        // Set the modded marker on the channel so we know we got packets
        c.get().getNetworkManager().channel().attr(NetworkConstants.FML_NETVERSION).set(NetworkConstants.NETVERSION);

        this.registriesToReceive = new HashSet<>(serverModList.getRegistries());
        this.registrySnapshots = Maps.newHashMap();
        LOGGER.debug(ForgeRegistry.REGISTRIES, "Expecting {} registries: {}", ()->this.registriesToReceive.size(), ()->this.registriesToReceive);
    }

    void handleModData(HandshakeMessages.S2CModData serverModData, Supplier<NetworkEvent.Context> c)
    {
        c.get().getNetworkManager().channel().attr(NetworkConstants.FML_CONNECTION_DATA).set(new ConnectionData(serverModData.getMods(), new HashMap<>()));
        c.get().setPacketHandled(true);
    }

    <MSG extends IntSupplier> void handleIndexedMessage(MSG message, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client indexed reply {} of type {}", message.getAsInt(), message.getClass().getName());
        boolean removed = this.sentMessages.removeIf(i-> i == message.getAsInt());
        if (!removed) {
            LOGGER.error(FMLHSMARKER, "Recieved unexpected index {} in client reply", message.getAsInt());
        }
    }

    void handleClientModListOnServer(HandshakeMessages.C2SModListReply clientModList, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]",  String.join(", ", clientModList.getModList()));
        Map<ResourceLocation, String> mismatchedChannels = NetworkRegistry.validateServerChannels(clientModList.getChannels());
        c.get().getNetworkManager().channel().attr(NetworkConstants.FML_CONNECTION_DATA)
                .set(new ConnectionData(clientModList.getModList().stream().collect(Collectors.toMap(Function.identity(), s -> Pair.of("", ""))), clientModList.getChannels()));
        c.get().setPacketHandled(true);
        if (!mismatchedChannels.isEmpty()) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
            NetworkConstants.handshakeChannel.reply(new HandshakeMessages.S2CChannelMismatchData(mismatchedChannels), c.get());
            c.get().getNetworkManager().disconnect(Component.literal("Connection closed - mismatched mod channel list"));
            return;
        }
        LOGGER.debug(FMLHSMARKER, "Accepted client connection mod list");
    }

    void handleModMismatchData(HandshakeMessages.S2CChannelMismatchData modMismatchData, Supplier<NetworkEvent.Context> c)
    {
        if (!modMismatchData.getMismatchedChannelData().isEmpty())
        {
            LOGGER.error(FMLHSMARKER, "Channels [{}] rejected their client side version number",
                    modMismatchData.getMismatchedChannelData().keySet().stream().map(Object::toString).collect(Collectors.joining(",")));
            LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
            c.get().setPacketHandled(true);
            //Populate the mod mismatch attribute with a new mismatch data instance to indicate that the disconnect happened due to a mod mismatch
            c.get().getNetworkManager().channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).set(ModMismatchData.channel(modMismatchData.getMismatchedChannelData(), NetworkHooks.getConnectionData(c.get().getNetworkManager()), false));
            c.get().getNetworkManager().disconnect(Component.literal("Connection closed - mismatched mod channel list"));
        }
    }

    void handleRegistryMessage(final HandshakeMessages.S2CRegistry registryPacket, final Supplier<NetworkEvent.Context> contextSupplier){
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
            NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), contextSupplier.get());
        }
    }

    private boolean handleRegistryLoading(final Supplier<NetworkEvent.Context> contextSupplier) {
        // We use a countdown latch to suspend the impl thread pending the client thread processing the registry data
        AtomicBoolean successfulConnection = new AtomicBoolean(false);
        AtomicReference<Multimap<ResourceLocation, ResourceLocation>> registryMismatches = new AtomicReference<>();
        CountDownLatch block = new CountDownLatch(1);
        contextSupplier.get().enqueueWork(() -> {
            LOGGER.debug(FMLHSMARKER, "Injecting registry snapshot from server.");
            final Multimap<ResourceLocation, ResourceLocation> missingData = GameData.injectSnapshot(registrySnapshots, false, false);
            LOGGER.debug(FMLHSMARKER, "Snapshot injected.");
            if (!missingData.isEmpty()) {
                LOGGER.error(FMLHSMARKER, "Missing registry data for impl connection:\n{}", LogMessageAdapter.adapt(sb->
                        missingData.forEach((reg, entry)-> sb.append("\t").append(reg).append(": ").append(entry).append('\n'))));
            }
            successfulConnection.set(missingData.isEmpty());
            registryMismatches.set(missingData);
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
            //Populate the mod mismatch attribute with a new mismatch data instance to indicate that the disconnect happened due to a mod mismatch
            this.manager.channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).set(ModMismatchData.registry(registryMismatches.get(), NetworkHooks.getConnectionData(contextSupplier.get().getNetworkManager())));
            this.manager.disconnect(Component.literal("Failed to synchronize registry data from server, closing connection"));
        }
        return successfulConnection.get();
    }

    void handleClientAck(final HandshakeMessages.C2SAcknowledge msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received acknowledgement from client");
        contextSupplier.get().setPacketHandled(true);
    }

    void handleConfigSync(final HandshakeMessages.S2CConfigData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received config sync from server");
        ConfigSync.INSTANCE.receiveSyncedConfig(msg, contextSupplier);
        contextSupplier.get().setPacketHandled(true);
        NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), contextSupplier.get());
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
        if (!negotiationStarted) {
            GameProfile profile = ((ServerLoginPacketListenerImpl) manager.getPacketListener()).gameProfile;
            PlayerNegotiationEvent event = new PlayerNegotiationEvent(manager, profile, pendingFutures);
            MinecraftForge.EVENT_BUS.post(event);
            negotiationStarted = true;
        }

        if (packetPosition < messageList.size()) {
            NetworkRegistry.LoginPayload message = messageList.get(packetPosition);

            LOGGER.debug(FMLHSMARKER, "Sending ticking packet info '{}' to '{}' sequence {}", message.getMessageContext(), message.getChannelName(), packetPosition);
            if (message.needsResponse())
                sentMessages.add(packetPosition);
            loginWrapper.sendServerToClientLoginPacket(message.getChannelName(), message.getData(), packetPosition, this.manager);
            packetPosition++;
        }

        pendingFutures.removeIf(future -> {
            if (!future.isDone()) {
                return false;
            }

            try {
                future.get();
            } catch (ExecutionException ex) {
                LOGGER.error("Error during negotiation", ex.getCause());
            } catch (CancellationException | InterruptedException ex) {
                // no-op
            }

            return true;
        });

        // we're done when sentMessages is empty
        if (sentMessages.isEmpty() && packetPosition >= messageList.size()-1 && pendingFutures.isEmpty()) {
            // clear ourselves - we're done!
            this.manager.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).set(null);
            LOGGER.debug(FMLHSMARKER, "Handshake complete!");
            return true;
        }
        return false;
    }

    /**
     * Helper method to determine if the S2C packet at the given packet position needs a response in form of a packet handled in {@link HandshakeHandler#handleIndexedMessage} for the handshake to progress.
     * @param mgr The impl manager for this connection
     * @param packetPosition The packet position of the packet that the status is queried of
     * @return true if the packet at the given packet position needs a response and thus may stop the handshake from progressing
     */
    public static boolean packetNeedsResponse(Connection mgr, int packetPosition)
    {
        HandshakeHandler handler = mgr.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get();
        if (handler != null)
        {
            return handler.sentMessages.contains(packetPosition);
        }
        return false;
    }
}
