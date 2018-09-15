package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FMLNetworking
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FMLHSMARKER = MarkerManager.getMarker("FMLHSMARKER");
    static final AttributeKey<String> FML_MARKER = AttributeKey.valueOf("fml:marker");
    private static final AttributeKey<FMLHandshake> FML_HANDSHAKE = AttributeKey.newInstance("fml:handshake");

    static void registerHandshake(NetworkManager manager, NetworkDirection direction) {
        manager.channel().attr(FML_HANDSHAKE).compareAndSet(null, new FMLHandshake(manager, direction));
    }

    private static FMLHandshake getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkManager networkManager = contextSupplier.get().getNetworkManager();
        return getHandshake(networkManager);
    }

    private static FMLHandshake getHandshake(NetworkManager manager)
    {
        return manager.channel().attr(FML_HANDSHAKE).get();
    }

    static boolean dispatch(NetworkManager networkManager)
    {
        return getHandshake(networkManager).tickServer();
    }

    public static class FMLHandshake {
        private static SimpleChannel channel;
        private static List<Supplier<FMLHandshakeMessage>> messages = Arrays.asList(FMLHandshakeMessage.S2CModList::new);
        private List<FMLHandshakeMessage> sentMessages = new ArrayList<>();
        static {
            channel = NetworkRegistry.ChannelBuilder.named(NetworkHooks.FMLHANDSHAKE).
                    clientAcceptedVersions(a -> true).
                    serverAcceptedVersions(a -> true).
                    networkProtocolVersion(() -> NetworkHooks.NETVERSION).
                    simpleChannel();
            channel.messageBuilder(FMLHandshakeMessage.S2CModList.class, 1).
                    decoder(FMLHandshakeMessage.S2CModList::decode).
                    encoder(FMLHandshakeMessage.S2CModList::encode).
                    loginIndex(FMLHandshakeMessage::getPacketIndexSequence, FMLHandshakeMessage::setPacketIndexSequence).
                    consumer((m,c)->getHandshake(c).handleServerModListOnClient(m, c)).
                    add();
            channel.messageBuilder(FMLHandshakeMessage.C2SModListReply.class, 2).
                    loginIndex(FMLHandshakeMessage::getPacketIndexSequence, FMLHandshakeMessage::setPacketIndexSequence).
                    decoder(FMLHandshakeMessage.C2SModListReply::decode).
                    encoder(FMLHandshakeMessage.C2SModListReply::encode).
                    consumer((m,c) -> getHandshake(c).handleClientModListOnServer(m,c)).
                    add();
        }

        private final NetworkDirection direction;

        private final NetworkManager manager;
        private int packetPosition;

        public FMLHandshake(NetworkManager networkManager, NetworkDirection side)
        {
            this.direction = side;
            this.manager = networkManager;
        }

        private void handleServerModListOnClient(FMLHandshakeMessage.S2CModList serverModList, Supplier<NetworkEvent.Context> c)
        {
            LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", serverModList.getModList());
            boolean accepted = NetworkRegistry.validateClientChannels(serverModList.getChannels());
            c.get().setPacketHandled(true);
            if (!accepted) {
                LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
                c.get().getNetworkManager().closeChannel(new TextComponentString("Connection closed - mismatched mod channel list"));
                return;
            }
            final FMLHandshakeMessage.C2SModListReply reply = new FMLHandshakeMessage.C2SModListReply();
            reply.setPacketIndexSequence(serverModList.getPacketIndexSequence());
            channel.reply(reply, c.get());
            LOGGER.debug(FMLHSMARKER, "Sent C2SModListReply packet with index {}", reply.getPacketIndexSequence());
        }

        private void handleClientModListOnServer(FMLHandshakeMessage.C2SModListReply clientModList, Supplier<NetworkEvent.Context> c)
        {
            LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]", clientModList.getModList());
            final FMLHandshakeMessage message = this.sentMessages.stream().filter(ob -> ob.getPacketIndexSequence() == clientModList.getPacketIndexSequence()).findFirst().orElseThrow(() -> new RuntimeException("Unexpected reply from client"));
            boolean removed = this.sentMessages.remove(message);
            boolean accepted = NetworkRegistry.validateServerChannels(clientModList.getChannels());
            c.get().setPacketHandled(true);
            if (!accepted) {
                LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
                c.get().getNetworkManager().closeChannel(new TextComponentString("Connection closed - mismatched mod channel list"));
                return;
            }
            LOGGER.debug(FMLHSMARKER, "Cleared original message {}", removed);
        }

        /**
         * Design of handshake.
         *
         * After {@link net.minecraft.server.network.NetHandlerLoginServer} enters the {@link net.minecraft.server.network.NetHandlerLoginServer.LoginState#NEGOTIATING}
         * state, this will be ticked once per server tick.
         *
         * FML will send packets, from Server to Client, from the messages queue until the queue is drained. Each message
         * will be indexed, and placed into the "pending acknowledgement" queue.
         *
         * The client should send an acknowledgement for every packet that has a positive index, containing
         * that index (and maybe other data as well).
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
            if (packetPosition < messages.size()) {
                final FMLHandshakeMessage message = messages.get(packetPosition).get();
                message.setPacketIndexSequence(packetPosition);
                LOGGER.debug(FMLHSMARKER, "Sending ticking packet {} index {}", message.getClass().getName(), message.getPacketIndexSequence());
                channel.sendTo(message, this.manager, this.direction);
                sentMessages.add(message);
                packetPosition++;
            }

            // we're done when sentMessages is empty
            if (sentMessages.isEmpty()) {
                // clear ourselves - we're done!
                this.manager.channel().attr(FML_HANDSHAKE).set(null);
                return true;
            }
            return false;
        }
    }


}
