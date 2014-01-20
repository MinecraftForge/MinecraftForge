package cpw.mods.fml.common.network.simpleimpl;

import io.netty.channel.ChannelFutureListener;
import java.util.EnumMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

/**
 * This class is a simplified netty wrapper for those not wishing to deal with the full power of netty.
 * It provides a simple message driven system, based on a discriminator byte over the custom packet channel.
 *
 * Usage is simple:<ul>
 * <li>construct, and store, an instance of this class. It will automatically register and configure your underlying netty channel.
 *
 * <li>Then, call {@link #registerMessage(Class, Class, byte, Side)} for each message type you want to exchange
 * providing an {@link IMessageHandler} implementation class as well as an {@link IMessage} implementation class. The side parameter
 * to that method indicates which side (server or client) the <em>message processing</em> will occur on. The discriminator byte
 * should be unique for this channelName - it is used to discriminate between different types of message that might
 * occur on this channel (a simple form of message channel multiplexing, if you will).
 * <li>To get a packet suitable for presenting to the rest of minecraft, you can call {@link #getPacketFrom(IMessage)}. The return result
 * is suitable for returning from things like {@link TileEntity#func_145844_m()} for example.
 * <li>Finally, use the sendXXX to send unsolicited messages to various classes of recipients.
 * </ul>
 *
 *
 *
 * @author cpw
 *
 */
public class SimpleNetworkWrapper {
    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private SimpleIndexedCodec packetCodec;

    public SimpleNetworkWrapper(String channelName)
    {
        packetCodec = new SimpleIndexedCodec();
        channels = NetworkRegistry.INSTANCE.newChannel(channelName, packetCodec);
    }

    /**
     * Register a message and it's associated handler. The message will have the supplied discriminator byte. The message handler will
     * be registered on the supplied side (this is the side where you want the message to be processed and acted upon).
     *
     * @param messageHandler the message handler type
     * @param message the message type
     * @param discriminator a discriminator byte
     * @param side the side for the handler
     */
    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> message, int discriminator, Side side)
    {
        packetCodec.addDiscriminator(discriminator, message);
        FMLEmbeddedChannel channel = channels.get(side);
        String type = channel.findChannelHandlerNameForType(SimpleIndexedCodec.class);
        if (side == Side.SERVER)
        {
            addServerHandlerAfter(channel, type, messageHandler);
        }
        else
        {
            addClientHandlerAfter(channel, type, messageHandler);
        }
    }

    private <REQ extends IMessage, REPLY extends IMessage, NH extends INetHandler> void addServerHandlerAfter(FMLEmbeddedChannel channel, String type, Class<? extends IMessageHandler<REQ, REPLY>> messageHandler)
    {
        SimpleChannelHandlerWrapper<REQ, REPLY> handler = getHandlerWrapper(messageHandler, Side.SERVER);
        channel.pipeline().addAfter(type, messageHandler.getName(), handler);
    }

    private <REQ extends IMessage, REPLY extends IMessage, NH extends INetHandler> void addClientHandlerAfter(FMLEmbeddedChannel channel, String type, Class<? extends IMessageHandler<REQ, REPLY>> messageHandler)
    {
        SimpleChannelHandlerWrapper<REQ, REPLY> handler = getHandlerWrapper(messageHandler, Side.CLIENT);
        channel.pipeline().addAfter(type, messageHandler.getName(), handler);
    }

    private <REPLY extends IMessage, REQ extends IMessage> SimpleChannelHandlerWrapper<REQ, REPLY> getHandlerWrapper(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Side side)
    {
        return new SimpleChannelHandlerWrapper<REQ, REPLY>(messageHandler, side);
    }

    /**
     * Construct a minecraft packet from the supplied message. Can be used where minecraft packets are required, such as
     * {@link TileEntity#func_145844_m()}.
     *
     * @param message The message to translate into packet form
     * @return A minecraft {@link Packet} suitable for use in minecraft APIs
     */
    public Packet getPacketFrom(IMessage message)
    {
        return channels.get(Side.SERVER).generatePacketFrom(message);
    }

    /**
     * Send this message to everyone.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     */
    public void sendToAll(IMessage message)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send this message to the specified player.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param player The player to send it to
     */
    public void sendTo(IMessage message, EntityPlayerMP player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param point The {@link TargetPoint} around which to send
     */
    public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send this message to everyone within the supplied dimension.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param dimensionId The dimension id to target
     */
    public void sendToDimension(IMessage message, int dimensionId)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send this message to the server.
     * The {@link IMessageHandler} for this message type should be on the SERVER side.
     *
     * @param message The message to send
     */
    public void sendToServer(IMessage message)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
