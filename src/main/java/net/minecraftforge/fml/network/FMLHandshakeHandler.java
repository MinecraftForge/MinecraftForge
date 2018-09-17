package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.util.ThreeConsumer;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Instance responsible for handling the overall FML network handshake.
 *
 * <p>An instance is created during {@link net.minecraft.network.handshake.client.CPacketHandshake} handling, and attached
 * to the {@link NetworkManager#channel} via {@link #FML_HANDSHAKE_HANDLER_ATTRIBUTE_KEY}.
 *
 * <p>The {@link #channel} is a {@link SimpleChannel} with standard messages flowing in both directions.
 *
 * <p>The {@link #loginWrapper} transforms these messages into {@link net.minecraft.network.login.client.CPacketCustomPayloadLogin}
 * and {@link net.minecraft.network.login.server.SPacketCustomPayloadLogin} compatible messages, by means of wrapping.
 *
 * <p>The handshake is ticked {@link #tickLogin(NetworkManager)} from the {@link NetHandlerLoginServer#update()} method,
 * utilizing the {@link NetHandlerLoginServer.LoginState#NEGOTIATING} state, which is otherwise unused in vanilla code.
 *
 * <p>During client to server initiation, on the <em>server</em>, the {@link NetworkEvent.GatherLoginPayloadsEvent} is fired,
 * which solicits all registered channels at the {@link NetworkRegistry} for any
 * {@link net.minecraftforge.fml.network.NetworkRegistry.LoginPayload} they wish to supply.
 *
 * <p>The collected {@link net.minecraftforge.fml.network.NetworkRegistry.LoginPayload} are sent, one per tick, via
 * the {@link FMLLoginWrapper#wrapPacket(ResourceLocation, PacketBuffer)} mechanism to the incoming client connection. Each
 * packet is indexed via {@link net.minecraft.network.login.client.CPacketCustomPayloadLogin#field_209922_a}, which is
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
    static final Marker FMLHSMARKER = MarkerManager.getMarker("FMLHANDSHAKE").setParents(FMLNetworking.NETWORK);
    private static final Logger LOGGER = LogManager.getLogger();
    static final ResourceLocation FML_HANDSHAKE_RESOURCE = new ResourceLocation("fml:handshake");
    private static final AttributeKey<FMLHandshakeHandler> FML_HANDSHAKE_HANDLER_ATTRIBUTE_KEY = AttributeKey.newInstance("fml:handshake");

    private static final FMLLoginWrapper loginWrapper = new FMLLoginWrapper();
    private static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(FML_HANDSHAKE_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkHooks.NETVERSION).
                simpleChannel();
        channel.messageBuilder(FMLHandshakeMessages.C2SAcknowledge.class, 99).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.C2SAcknowledge::decode).
                encoder(FMLHandshakeMessages.C2SAcknowledge::encode).
                consumer(indexFirst(FMLHandshakeHandler::handleClientAck)).
                add();
        channel.messageBuilder(FMLHandshakeMessages.S2CModList.class, 1).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CModList::decode).
                encoder(FMLHandshakeMessages.S2CModList::encode).
                markAsLoginPacket().
                consumer(biConsumerFor(FMLHandshakeHandler::handleServerModListOnClient)).
                add();
        channel.messageBuilder(FMLHandshakeMessages.C2SModListReply.class, 2).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.C2SModListReply::decode).
                encoder(FMLHandshakeMessages.C2SModListReply::encode).
                consumer(indexFirst(FMLHandshakeHandler::handleClientModListOnServer)).
                add();
        channel.messageBuilder(FMLHandshakeMessages.S2CRegistry.class, 3).
                loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).
                decoder(FMLHandshakeMessages.S2CRegistry::decode).
                encoder(FMLHandshakeMessages.S2CRegistry::encode).
                buildLoginPacketList(RegistryManager::generateRegistryPackets).
                consumer(biConsumerFor(FMLHandshakeHandler::handleRegistryMessage)).
                add();
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
     * Create a new handshake instance. Called when connection is first created during the {@link net.minecraft.network.handshake.client.CPacketHandshake}
     * handling.
     *
     * @param manager The network manager for this connection
     * @param direction The {@link NetworkDirection} for this connection: {@link NetworkDirection#LOGIN_TO_SERVER} or {@link NetworkDirection#LOGIN_TO_CLIENT}
     */
    static void registerHandshake(NetworkManager manager, NetworkDirection direction) {
        manager.channel().attr(FML_HANDSHAKE_HANDLER_ATTRIBUTE_KEY).compareAndSet(null, new FMLHandshakeHandler(manager, direction));
    }

    /**
     * Retrieve the handshake from the {@link NetworkEvent.Context}
     *
     * @param contextSupplier the {@link NetworkEvent.Context}
     * @return The handshake handler for the connection
     */
    private static FMLHandshakeHandler getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
        return contextSupplier.get().attr(FML_HANDSHAKE_HANDLER_ATTRIBUTE_KEY).get();
    }

    static boolean tickLogin(NetworkManager networkManager)
    {
        return networkManager.channel().attr(FML_HANDSHAKE_HANDLER_ATTRIBUTE_KEY).get().tickServer();
    }

    private List<NetworkRegistry.LoginPayload> messageList;

    private List<Integer> sentMessages = new ArrayList<>();

    private final NetworkDirection direction;
    private final NetworkManager manager;
    private int packetPosition;
    private FMLHandshakeHandler(NetworkManager networkManager, NetworkDirection side)
    {
        this.direction = side;
        this.manager = networkManager;
        this.messageList = NetworkRegistry.gatherLoginPayloads();
        LOGGER.debug(FMLHSMARKER, "Starting new modded network connection. Found {} messages to dispatch.", this.messageList.size());
    }

    private void handleServerModListOnClient(FMLHandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", serverModList.getModList());
        boolean accepted = NetworkRegistry.validateClientChannels(serverModList.getChannels());
        c.get().setPacketHandled(true);
        if (!accepted) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
            c.get().getNetworkManager().closeChannel(new TextComponentString("Connection closed - mismatched mod channel list"));
            return;
        }
        final FMLHandshakeMessages.C2SModListReply reply = new FMLHandshakeMessages.C2SModListReply();
        channel.reply(reply, c.get());
        LOGGER.debug(FMLHSMARKER, "Accepted server connection");
    }

    private <MSG extends FMLHandshakeMessages.LoginIndexedMessage> void handleIndexedMessage(MSG message, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client indexed reply {} of type {}", message.getLoginIndex(), message.getClass().getName());
        boolean removed = this.sentMessages.removeIf(i->i==message.getLoginIndex());
        if (!removed) {
            LOGGER.error(FMLHSMARKER, "Recieved unexpected index {} in client reply", message.getLoginIndex());
        }
    }

    private void handleClientModListOnServer(FMLHandshakeMessages.C2SModListReply clientModList, Supplier<NetworkEvent.Context> c)
    {
        LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]", clientModList.getModList());
        boolean accepted = NetworkRegistry.validateServerChannels(clientModList.getChannels());
        c.get().setPacketHandled(true);
        if (!accepted) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
            c.get().getNetworkManager().closeChannel(new TextComponentString("Connection closed - mismatched mod channel list"));
            return;
        }
        LOGGER.debug(FMLHSMARKER, "Accepted client connection mod list");
    }
    private void handleRegistryMessage(final FMLHandshakeMessages.S2CRegistry registryPacket, final Supplier<NetworkEvent.Context> contextSupplier) {
        RegistryManager.acceptRegistry(registryPacket, contextSupplier);
        contextSupplier.get().setPacketHandled(true);
        final FMLHandshakeMessages.C2SAcknowledge reply = new FMLHandshakeMessages.C2SAcknowledge();
        channel.reply(reply, contextSupplier.get());
    }

    private void handleClientAck(final FMLHandshakeMessages.C2SAcknowledge msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received acknowledgement from client");
        contextSupplier.get().setPacketHandled(true);
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
            loginWrapper.sendServerToClientLoginPacket(message.getChannelName(), message.getData(), packetPosition, this.manager);
            sentMessages.add(packetPosition);
            packetPosition++;
        }

        // we're done when sentMessages is empty
        if (sentMessages.isEmpty() && packetPosition >= messageList.size()-1) {
            // clear ourselves - we're done!
            this.manager.channel().attr(FML_HANDSHAKE_HANDLER_ATTRIBUTE_KEY).set(null);
            LOGGER.debug(FMLHSMARKER, "Handshake complete!");
            return true;
        }
        return false;
    }
}
