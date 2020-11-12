package net.minecraftforge.network;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSet;

/**
 * A custom payload channel that allows sending vanilla server-to-client packets, even if they would normally
 * be too large for the vanilla protocol. This is achieved by splitting them into multiple custom payload packets.
 */
public class VanillaPacketSplitter
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ResourceLocation CHANNEL = new ResourceLocation("forge", "split");
    private static final String VERSION = "1.0";

    private static final Set<Class<? extends IPacket<?>>> ALLOWED_PACKETS = ImmutableSet.of(
            SUpdateRecipesPacket.class,
            STagsListPacket.class
    );

    private static final int PROTOCOL_MAX = 2097152;

    private static final int PAYLOAD_TO_CLIENT_MAX = 1048576;
    // 1 byte for state, 5 byte for VarInt PacketID
    private static final int PART_SIZE = PAYLOAD_TO_CLIENT_MAX - 1 - 5;

    private static final byte STATE_FIRST = 1;
    private static final byte STATE_LAST = 2;

    public static void register()
    {
        EventNetworkChannel channel = NetworkRegistry.newEventChannel(CHANNEL, () -> VERSION, VERSION::equals, VERSION::equals);
        channel.addListener(VanillaPacketSplitter::onClientPacket);
    }

    /**
     * Append the given packet to the given list. If the packet needs to be split, multiple packets will be appened.
     * Otherwise only the packet itself.
     */
    public static void appendPackets(ProtocolType protocol, PacketDirection direction, IPacket<?> packet, List<? super IPacket<?>> out)
    {
        if (heuristicIsDefinitelySmallEnough(packet))
        {
            out.add(packet);
        }
        else
        {
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            try
            {
                packet.writePacketData(buf);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
            if (buf.readableBytes() <= PROTOCOL_MAX)
            {
                out.add(packet);
            }
            else
            {
                int parts = (int)Math.ceil(((double)buf.readableBytes()) / PART_SIZE);
                if (parts == 1)
                {
                    out.add(packet);
                }
                else
                {
                    for (int part = 0; part < parts; part++)
                    {
                        PacketBuffer partBuf = new PacketBuffer(Unpooled.buffer());
                        if (part == 0)
                        {
                            partBuf.writeByte(STATE_FIRST);
                            partBuf.writeVarInt(protocol.getPacketId(direction, packet));
                        }
                        else
                        {
                            partBuf.writeByte(part == parts - 1 ? STATE_LAST : 0);
                        }
                        partBuf.writeBytes(buf, Math.min(PART_SIZE, buf.readableBytes()));
                        out.add(new SCustomPayloadPlayPacket(CHANNEL, partBuf));
                    }
                }
            }
        }
    }

    private static boolean heuristicIsDefinitelySmallEnough(IPacket<?> packet)
    {
        // todo: improve this
        return false;
    }

    private static final List<PacketBuffer> receivedBuffers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private static void onClientPacket(NetworkEvent.ClientCustomPayloadEvent event)
    {
        NetworkEvent.Context ctx = event.getSource().get();
        PacketDirection direction = ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND;
        ProtocolType protocol = ProtocolType.PLAY;

        PacketBuffer buf = event.getPayload();

        byte state = buf.readByte();
        if (state == STATE_FIRST)
        {
            receivedBuffers.clear();
        }
        receivedBuffers.add(buf);
        if (state == STATE_LAST)
        {
            PacketBuffer full = new PacketBuffer(Unpooled.wrappedBuffer(receivedBuffers.toArray(new PacketBuffer[0])));
            int packetId = full.readVarInt();
            IPacket<?> packet = protocol.getPacket(direction, packetId);
            if (packet == null)
            {
                LOGGER.error("Received invalid packet ID {} in forge:split", packetId);
            }
            else if (!ALLOWED_PACKETS.contains(packet.getClass()))
            {
                LOGGER.error("Received not allowed packet type {} in forge:split", packet);
            }
            else
            {
                try
                {
                    packet.readPacketData(full);
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
                ctx.enqueueWork(() -> ((IPacket<ClientPlayNetHandler>)packet).processPacket(Minecraft.getInstance().getConnection()));
            }
        }
    }

}
