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

package net.minecraftforge.network;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.*;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLConnectionData;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
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
    // TODO 1.17: bump this to version 1.1 and remove dummy channel
    // Version 1.1 removed client-side whitelist
    // we don't bump actual channel version because that would prevent old clients from connecting
    // but we still need to detect new clients, so we add a dummy channel just for that.
    private static final ResourceLocation V11_DUMMY_CHANNEL = new ResourceLocation("forge", "split_11");
    private static final String VERSION = "1.0";
    private static final String VERSION_11 = "1.1";

    private static final int PROTOCOL_MAX = 2097152;

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

        Predicate<String> version11Check = NetworkRegistry.acceptMissingOr(VERSION_11);
        NetworkRegistry.newEventChannel(V11_DUMMY_CHANNEL, () -> VERSION_11, version11Check, version11Check);
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
                packet.write(buf);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
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
                            new PacketBuffer(partPrefix).writeVarInt(protocol.getPacketId(direction, packet));
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
                        out.add(new SCustomPayloadPlayPacket(CHANNEL, new PacketBuffer(partBuf)));
                    }
                    // we retained all the slices, so we do not need this one anymore
                    buf.release();
                }
            }
        }
    }

    private static boolean heuristicIsDefinitelySmallEnough(IPacket<?> packet)
    {
        return false;
    }

    private static final List<PacketBuffer> receivedBuffers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private static void onClientPacket(NetworkEvent.ServerCustomPayloadEvent event)
    {
        NetworkEvent.Context ctx = event.getSource().get();
        PacketDirection direction = ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND;
        ProtocolType protocol = ProtocolType.PLAY;

        ctx.setPacketHandled(true);

        PacketBuffer buf = event.getPayload();

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
            PacketBuffer full = new PacketBuffer(Unpooled.wrappedBuffer(receivedBuffers.toArray(new PacketBuffer[0])));
            int packetId = full.readVarInt();
            IPacket<?> packet = protocol.createPacket(direction, packetId);
            if (packet == null)
            {
                LOGGER.error("Received invalid packet ID {} in forge:split", packetId);
            }
            else
            {
                try
                {
                    packet.read(full);
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                } finally {
                    receivedBuffers.clear();
                    full.release();
                }
                ctx.enqueueWork(() -> ((IPacket<ClientPlayNetHandler>)packet).handle(Minecraft.getInstance().getConnection()));
            }
        }
    }

    public enum RemoteCompatibility
    {
        ABSENT,
        V10_LEGACY,
        V11
    }

    public static RemoteCompatibility getRemoteCompatibility(NetworkManager manager)
    {
        FMLConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        if (connectionData == null)
        {
            return RemoteCompatibility.ABSENT;
        }
        else if (connectionData.getChannels().containsKey(V11_DUMMY_CHANNEL))
        {
            return RemoteCompatibility.V11;
        }
        else if (connectionData.getChannels().containsKey(CHANNEL))
        {
            return RemoteCompatibility.V10_LEGACY;
        }
        else
        {
            return RemoteCompatibility.ABSENT;
        }
    }

    public static boolean isRemoteCompatible(NetworkManager manager)
    {
        return getRemoteCompatibility(manager) != RemoteCompatibility.ABSENT;
    }
}
