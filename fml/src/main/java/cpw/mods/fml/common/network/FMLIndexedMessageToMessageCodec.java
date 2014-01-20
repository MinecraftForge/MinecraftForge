package cpw.mods.fml.common.network;

import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;
import java.util.List;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

@Sharable
public abstract class FMLIndexedMessageToMessageCodec<A> extends MessageToMessageCodec<FMLProxyPacket, A> {
    private TByteObjectHashMap<Class<? extends A>> discriminators = new TByteObjectHashMap<Class<? extends A>>();
    private TObjectByteHashMap<Class<? extends A>> types = new TObjectByteHashMap<Class<? extends A>>();

    /**
     * Make this accessible to subclasses
     */

    protected static final AttributeKey<ThreadLocal<FMLProxyPacket>> INBOUNDPACKETTRACKER = new AttributeKey<ThreadLocal<FMLProxyPacket>>("fml:inboundpacket");

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        super.handlerAdded(ctx);
        ctx.attr(INBOUNDPACKETTRACKER).set(new ThreadLocal<FMLProxyPacket>());
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
        ByteBuf buffer = Unpooled.buffer();
        @SuppressWarnings("unchecked") // Stupid unnecessary cast I can't seem to kill
        Class<? extends A> clazz = (Class<? extends A>) msg.getClass();
        byte discriminator = types.get(clazz);
        buffer.writeByte(discriminator);
        encodeInto(ctx, msg, buffer);
        FMLProxyPacket proxy = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
        FMLProxyPacket old = ctx.attr(INBOUNDPACKETTRACKER).get().get();
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
        ByteBuf payload = msg.payload();
        byte discriminator = payload.readByte();
        Class<? extends A> clazz = discriminators.get(discriminator);
        if(clazz == null)
        {
            throw new NullPointerException("Undefined message for discriminator " + discriminator + " in channel " + msg.channel());
        }
        A newMsg = clazz.newInstance();
        ctx.attr(INBOUNDPACKETTRACKER).get().set(msg);
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
        FMLLog.log(Level.ERROR, cause, "FMLIndexedMessageCodec exception caught");
        super.exceptionCaught(ctx, cause);
    }
}
