package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        private static List<Supplier<HandshakeMessage>> messages = Arrays.asList(HandshakeMessage.S2CModList::new);
        private List<HandshakeMessage> sentMessages = new ArrayList<>();
        static {
            channel = NetworkRegistry.ChannelBuilder.named(NetworkHooks.FMLHANDSHAKE).
                    clientAcceptedVersions(a -> true).
                    serverAcceptedVersions(a -> true).
                    networkProtocolVersion(() -> NetworkHooks.NETVERSION).
                    simpleChannel();
            channel.messageBuilder(HandshakeMessage.S2CModList.class, 1).
                    decoder(HandshakeMessage.S2CModList::decode).
                    encoder(HandshakeMessage.S2CModList::encode).
                    loginIndex(HandshakeMessage.S2CModList::setPacketIndexSequence).
                    consumer((m,c)->getHandshake(c).handleServerModListOnClient(m, c)).
                    add();
            channel.messageBuilder(HandshakeMessage.C2SModListReply.class, 2).
                    loginIndex(HandshakeMessage::setPacketIndexSequence).
                    decoder(HandshakeMessage.C2SModListReply::decode).
                    encoder(HandshakeMessage.C2SModListReply::encode).
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

        public void handleServerModListOnClient(HandshakeMessage.S2CModList serverModList, Supplier<NetworkEvent.Context> c)
        {
            LOGGER.debug(FMLHSMARKER, "Received S2CModList packet with index {}", serverModList.getPacketIndexSequence());
            c.get().setPacketHandled(true);
            final HandshakeMessage.C2SModListReply reply = new HandshakeMessage.C2SModListReply();
            channel.sendLogin(reply, c.get().getNetworkManager(), c.get().getDirection().reply(), reply.getPacketIndexSequence());
            LOGGER.debug(FMLHSMARKER, "Sent C2SModListReply packet with index {}", reply.getPacketIndexSequence());
        }

        private void handleClientModListOnServer(HandshakeMessage.C2SModListReply m, Supplier<NetworkEvent.Context> c)
        {
            LOGGER.debug(FMLHSMARKER, "Received C2SModListReply with index {}", m.getPacketIndexSequence());
            final HandshakeMessage message = this.sentMessages.stream().filter(ob -> ob.getPacketIndexSequence() == m.getPacketIndexSequence()).findFirst().orElseThrow(() -> new RuntimeException("Unexpected reply from client"));
            boolean removed = this.sentMessages.remove(message);
            c.get().setPacketHandled(true);
            LOGGER.debug(FMLHSMARKER, "Cleared original message {}", removed);
        }

        public boolean tickServer()
        {
            if (packetPosition < messages.size()) {
                final HandshakeMessage message = messages.get(packetPosition).get();
                LOGGER.debug(FMLHSMARKER, "Sending ticking packet {} index {}", message.getClass().getName(), message.getPacketIndexSequence());
                channel.sendLogin(message, this.manager, this.direction, packetPosition);
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


    static class HandshakeMessage
    {
        private int index;
        public void setPacketIndexSequence(int i)
        {
            this.index = i;
        }

        public int getPacketIndexSequence()
        {
            return index;
        }

        static class S2CModList extends HandshakeMessage
        {
            private List<String> modList;

            S2CModList() {
                this.modList = ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.toList());
            }

            S2CModList(NBTTagCompound nbtTagCompound)
            {
                this.modList = nbtTagCompound.getTagList("list", 8).stream().map(INBTBase::getString).collect(Collectors.toList());
            }

            public static S2CModList decode(PacketBuffer packetBuffer)
            {
                final NBTTagCompound nbtTagCompound = packetBuffer.readCompoundTag();
                return new S2CModList(nbtTagCompound);
            }

            public void encode(PacketBuffer packetBuffer)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setTag("list",modList.stream().map(NBTTagString::new).collect(Collectors.toCollection(NBTTagList::new)));
                packetBuffer.writeCompoundTag(tag);
            }
        }

        static class C2SModListReply extends HandshakeMessage
        {
            public static C2SModListReply decode(PacketBuffer buffer)
            {
                return new C2SModListReply();
            }

            public void encode(PacketBuffer buffer)
            {

            }
        }
    }
}
