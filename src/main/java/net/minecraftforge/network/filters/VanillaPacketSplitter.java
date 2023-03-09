/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;
import net.minecraftforge.network.event.EventNetworkChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A custom payload channel that allows sending vanilla server-to-client packets, even if they would normally
 * be too large for the vanilla protocol. This is achieved by splitting them into multiple custom payload packets.
 */
public class VanillaPacketSplitter
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ResourceLocation CHANNEL = new ResourceLocation("forge", "split");
    private static final String VERSION = "1.1";

    private static final int PROTOCOL_MAX = CompressionDecoder.MAXIMUM_UNCOMPRESSED_LENGTH;

    private static final int PAYLOAD_TO_CLIENT_MAX = 1048576;
    // 1 byte for state, 5 byte for VarInt PacketID
    private static final int PART_SIZE = PAYLOAD_TO_CLIENT_MAX - 1 - 5;

    private static final byte STATE_FIRST = 1;
    private static final byte STATE_LAST = 2;

    public static void register()
    {
        Predicate<String> versionCheck = NetworkRegistry.acceptMissingOr(VERSION);
        EventNetworkChannel channel = NetworkRegistry.newEventChannel(CHANNEL, () -> VERSION, versionCheck, versionCheck);
        channel.addListener(VanillaPacketSplitter::onClientPacket);
    }

    /**
     * Append the given packet to the given list. If the packet needs to be split, multiple packets will be appened.
     * Otherwise only the packet itself.
     */
    public static void appendPackets(ConnectionProtocol protocol, PacketFlow direction, Packet<?> packet, List<? super Packet<?>> out)
    {
        if (heuristicIsDefinitelySmallEnough(packet))
        {
            out.add(packet);
        }
        else
        {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.write(buf);
            if (buf.readableBytes() <= PROTOCOL_MAX)
            {
                buf.release();
                out.add(packet);
            }
            else
            {
                int parts = (int)Math.ceil(((double)buf.readableBytes()) / PART_SIZE);
                if (parts == 1)
                {
                    buf.release();
                    out.add(packet);
                }
                else
                {
                    for (int part = 0; part < parts; part++)
                    {
                        ByteBuf partPrefix;
                        if (part == 0)
                        {
                            partPrefix = Unpooled.buffer(5);
                            partPrefix.writeByte(STATE_FIRST);
                            new FriendlyByteBuf(partPrefix).writeVarInt(protocol.getPacketId(direction, packet));
                        }
                        else
                        {
                            partPrefix = Unpooled.buffer(1);
                            partPrefix.writeByte(part == parts - 1 ? STATE_LAST : 0);
                        }
                        int partSize = Math.min(PART_SIZE, buf.readableBytes());
                        ByteBuf partBuf = Unpooled.wrappedBuffer(
                                partPrefix,
                                buf.retainedSlice(buf.readerIndex(), partSize)
                        );
                        buf.skipBytes(partSize);
                        out.add(new ClientboundCustomPayloadPacket(CHANNEL, new FriendlyByteBuf(partBuf)));
                    }
                    // we retained all the slices, so we do not need this one anymore
                    buf.release();
                }
            }
        }
    }

    private static boolean heuristicIsDefinitelySmallEnough(Packet<?> packet)
    {
        return false;
    }

    private static final List<FriendlyByteBuf> receivedBuffers = new ArrayList<>();

    private static void onClientPacket(NetworkEvent.ServerCustomPayloadEvent event)
    {
        NetworkEvent.Context ctx = event.getSource().get();
        PacketFlow direction = ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND;
        ConnectionProtocol protocol = ConnectionProtocol.PLAY;

        ctx.setPacketHandled(true);

        FriendlyByteBuf buf = event.getPayload();

        byte state = buf.readByte();
        if (state == STATE_FIRST)
        {
            if (!receivedBuffers.isEmpty())
            {
                LOGGER.warn("forge:split received out of order - inbound buffer not empty when receiving first");
                receivedBuffers.clear();
            }
        }
        buf.retain(); // retain the buffer, it is released after this handler otherwise
        receivedBuffers.add(buf);
        if (state == STATE_LAST)
        {
            FriendlyByteBuf full = new FriendlyByteBuf(Unpooled.wrappedBuffer(receivedBuffers.toArray(new FriendlyByteBuf[0])));
            int packetId = full.readVarInt();
            Packet<?> packet = protocol.createPacket(direction, packetId, full);
            if (packet == null)
            {
                LOGGER.error("Received invalid packet ID {} in forge:split", packetId);
            }
            else
            {
                receivedBuffers.clear();
                full.release();
                ctx.enqueueWork(() -> genericsFtw(packet, event.getSource().get().getNetworkManager().getPacketListener()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void genericsFtw(Packet<T> pkt, Object listener)
    {
        pkt.handle((T) listener);
    }

    public enum RemoteCompatibility
    {
        ABSENT,
        PRESENT
    }

    public static RemoteCompatibility getRemoteCompatibility(Connection manager)
    {
        ConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        if (connectionData != null && connectionData.getChannels().containsKey(CHANNEL))
        {
            return RemoteCompatibility.PRESENT;
        }
        else
        {
            return RemoteCompatibility.ABSENT;
        }
    }

    public static boolean isRemoteCompatible(Connection manager)
    {
        return getRemoteCompatibility(manager) != RemoteCompatibility.ABSENT;
    }
}
