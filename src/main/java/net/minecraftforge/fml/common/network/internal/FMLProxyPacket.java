/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import javax.annotation.Nullable;

public class FMLProxyPacket implements Packet<INetHandler> {
    final String channel;
    private Side target;
    private final PacketBuffer payload;
    private INetHandler netHandler;
    private NetworkDispatcher dispatcher;
    private static Multiset<String> badPackets = ConcurrentHashMultiset.create();
    private static int packetCountWarning = Integer.parseInt(System.getProperty("fml.badPacketCounter", "100"));

    public FMLProxyPacket(SPacketCustomPayload original)
    {
        this(original.getBufferData(), original.getChannelName());
        this.target = Side.CLIENT;
    }

    public FMLProxyPacket(CPacketCustomPayload original)
    {
        this(original.getBufferData(), original.getChannelName());
        this.target = Side.SERVER;
    }

    public FMLProxyPacket(PacketBuffer payload, String channel)
    {
        this.channel = channel;
        this.payload = payload;
    }
    @Override
    public void readPacketData(PacketBuffer packetbuffer) throws IOException
    {
        // NOOP - we are not built this way
    }

    @Override
    public void writePacketData(PacketBuffer packetbuffer) throws IOException
    {
        // NOOP - we are not built this way
    }

    @Override
    public void processPacket(INetHandler inethandler)
    {
        this.netHandler = inethandler;
        EmbeddedChannel internalChannel = NetworkRegistry.INSTANCE.getChannel(this.channel, this.target);
        if (internalChannel != null)
        {
            internalChannel.attr(NetworkRegistry.NET_HANDLER).set(this.netHandler);
            try
            {
                if (internalChannel.writeInbound(this))
                {
                    badPackets.add(this.channel);
                    if (badPackets.size() % packetCountWarning == 0)
                    {
                        FMLLog.log.fatal("Detected ongoing potential memory leak. {} packets have leaked. Top offenders", badPackets.size());
                        int i = 0;
                        for (Entry<String> s  : Multisets.copyHighestCountFirst(badPackets).entrySet())
                        {
                            if (i++ > 10) break;
                            FMLLog.log.fatal("\t {} : {}", s.getElement(), s.getCount());
                        }
                    }
                }
                internalChannel.inboundMessages().clear();
            }
            catch (FMLNetworkException ne)
            {
                FMLLog.log.error("There was a network exception handling a packet on channel {}", channel, ne);
                dispatcher.rejectHandshake(ne.getMessage());
            }
            catch (Throwable t)
            {
                FMLLog.log.error("There was a critical exception handling a packet on channel {}", channel, t);
                dispatcher.rejectHandshake("A fatal error has occurred, this connection is terminated");
            }
        }
    }

    public String channel()
    {
        return channel;
    }
    public ByteBuf payload()
    {
        return payload;
    }
    public INetHandler handler()
    {
        return netHandler;
    }
    public Packet<INetHandlerPlayServer> toC17Packet()
    {
        return new CPacketCustomPayload(channel, payload);
    }

    static final int PART_SIZE = 0x100000 - 0x50; // Make it a constant so that it gets inlined below.
    // FIXME int overflow
    public static final int MAX_LENGTH = PART_SIZE * 255;
    public List<Packet<INetHandlerPlayClient>> toS3FPackets() throws IOException
    {
        List<Packet<INetHandlerPlayClient>> ret = Lists.newArrayList();
        byte[] data = payload.array();

        if (data.length < PART_SIZE)
        {
            ret.add(new SPacketCustomPayload(channel, new PacketBuffer(payload.duplicate())));
        }
        else
        {
            int parts = (int)Math.ceil(data.length / (double)(PART_SIZE - 1)); //We add a byte header so -1
            if (parts > 255)
            {
                throw new IllegalArgumentException("Payload may not be larger than " + MAX_LENGTH + " bytes");
            }
            PacketBuffer preamble = new PacketBuffer(Unpooled.buffer());
            preamble.writeString(channel);
            preamble.writeByte(parts);
            preamble.writeInt(data.length);
            ret.add(new SPacketCustomPayload("FML|MP", preamble));

            int offset = 0;
            for (int x = 0; x < parts; x++)
            {
                int length = Math.min(PART_SIZE, data.length - offset + 1);
                byte[] tmp = new byte[length];
                tmp[0] = (byte)(x & 0xFF);
                System.arraycopy(data, offset, tmp, 1, tmp.length - 1);
                offset += tmp.length - 1;
                ret.add(new SPacketCustomPayload("FML|MP", new PacketBuffer(Unpooled.wrappedBuffer(tmp))));
            }
        }
        return ret;
    }

    public void setTarget(Side target)
    {
        this.target = target;
    }

    public void setDispatcher(NetworkDispatcher networkDispatcher)
    {
        this.dispatcher = networkDispatcher;
    }

    @Nullable
    public NetworkManager getOrigin()
    {
        return this.dispatcher != null ? this.dispatcher.manager : null;
    }

    public NetworkDispatcher getDispatcher()
    {
        return this.dispatcher;
    }

    public Side getTarget()
    {
        return target;
    }

    public FMLProxyPacket copy()
    {
        FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(payload.duplicate()), channel);
        pkt.dispatcher = dispatcher;
        pkt.netHandler = netHandler;
        pkt.target = target;
        return pkt;
    }
}
