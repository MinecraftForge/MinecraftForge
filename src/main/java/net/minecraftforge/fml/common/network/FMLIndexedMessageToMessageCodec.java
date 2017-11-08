/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common.network;

import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

@Sharable
public abstract class FMLIndexedMessageToMessageCodec<A> extends MessageToMessageCodec<FMLProxyPacket, A> {
    private TByteObjectHashMap<Class<? extends A>> discriminators = new TByteObjectHashMap<Class<? extends A>>();
    private TObjectByteHashMap<Class<? extends A>> types = new TObjectByteHashMap<Class<? extends A>>();

    /**
     * Make this accessible to subclasses
     */

    public static final AttributeKey<ThreadLocal<WeakReference<FMLProxyPacket>>> INBOUNDPACKETTRACKER = AttributeKey.valueOf("fml:inboundpacket");

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        super.handlerAdded(ctx);
        ctx.channel().attr(INBOUNDPACKETTRACKER).set(new ThreadLocal<WeakReference<FMLProxyPacket>>());
    }

    public FMLIndexedMessageToMessageCodec<A> addDiscriminator(int discriminator, Class<? extends A> type)
    {
        discriminators.put((byte)discriminator, type);
        types.put(type, (byte)discriminator);
        return this;
    }

    public abstract void encodeInto(ChannelHandlerContext ctx, A msg, ByteBuf target) throws Exception;

    @Override
    protected final void encode(ChannelHandlerContext ctx, A msg, List<Object> out) throws Exception
    {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        byte discriminator = types.get(msg.getClass());
        buffer.writeByte(discriminator);
        encodeInto(ctx, msg, buffer);
        FMLProxyPacket proxy = new FMLProxyPacket(buffer/*.copy()*/, ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
        WeakReference<FMLProxyPacket> ref = ctx.channel().attr(INBOUNDPACKETTRACKER).get().get();
        FMLProxyPacket old = ref == null ? null : ref.get();
        if (old != null)
        {
            proxy.setDispatcher(old.getDispatcher());
        }
        out.add(proxy);
    }

    public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf source, A msg);

    @Override
    protected final void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception
    {
        testMessageValidity(msg);
        ByteBuf payload = msg.payload().duplicate();
        if (payload.readableBytes() < 1)
        {
            FMLLog.log.error("The FMLIndexedCodec has received an empty buffer on channel {}, likely a result of a LAN server issue. Pipeline parts : {}", ctx.channel().attr(NetworkRegistry.FML_CHANNEL), ctx.pipeline().toString());
        }
        byte discriminator = payload.readByte();
        Class<? extends A> clazz = discriminators.get(discriminator);
        if(clazz == null)
        {
            throw new NullPointerException("Undefined message for discriminator " + discriminator + " in channel " + msg.channel());
        }
        A newMsg = clazz.newInstance();
        ctx.channel().attr(INBOUNDPACKETTRACKER).get().set(new WeakReference<FMLProxyPacket>(msg));
        decodeInto(ctx, payload.slice(), newMsg);
        out.add(newMsg);
    }

    /**
     * Called to verify the message received. This can be used to hard disconnect in case of an unexpected packet,
     * say due to a weird protocol mismatch. Use with caution.
     * @param msg
     */
    protected void testMessageValidity(FMLProxyPacket msg)
    {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log.error("FMLIndexedMessageCodec exception caught", cause);
        super.exceptionCaught(ctx, cause);
    }
}
