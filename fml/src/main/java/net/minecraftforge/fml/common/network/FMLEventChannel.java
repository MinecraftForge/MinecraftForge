package net.minecraftforge.fml.common.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.EnumMap;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

/**
 * An event driven network channel, using {@link FMLNetworkEvent.CustomPacketEvent} and {@link FMLNetworkEvent.CustomNetworkEvent}
 * to deliver messages to an event listener. There is one "bus" for each channel, due to the
 * impossibility of filtering a bus for specific events.
 *
 * This event driven system completely wraps the netty code. Mod code deals with FMLProxyPackets directly. It is not
 * possible to enhance the netty pipeline, and I would expect highly unexpected results if it were modified reflectively.
 * Use a real ChannelHandler if you want to use netty.
 *
 * @author cpw
 *
 */
public class FMLEventChannel {
    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private EventBus eventBus;

    /*
     * This is done this way so that the CLIENT specific code in the factory only loads on the client
     */
    private enum EventFactory
    {
        SERVER()
        {
            @Override
            FMLNetworkEvent.CustomPacketEvent<?> make(FMLProxyPacket msg)
            {
                FMLNetworkEvent.CustomPacketEvent<?> event = null;
                if (msg.handler() instanceof NetHandlerPlayServer)
                {
                    NetHandlerPlayServer server = (NetHandlerPlayServer) msg.handler();
                    event = new FMLNetworkEvent.ServerCustomPacketEvent(server.getNetworkManager(), msg);
                }
                return event;
            }
        },
        CLIENT()
        {
            @Override
            FMLNetworkEvent.CustomPacketEvent<?> make(FMLProxyPacket msg)
            {
                FMLNetworkEvent.CustomPacketEvent<?> event = null;
                if (msg.handler() instanceof NetHandlerPlayClient)
                {
                    NetHandlerPlayClient client = (NetHandlerPlayClient) msg.handler();
                    event = new FMLNetworkEvent.ClientCustomPacketEvent(client.getNetworkManager(), msg);
                }
                else if (msg.handler() instanceof NetHandlerPlayServer)
                {
                    NetHandlerPlayServer server = (NetHandlerPlayServer) msg.handler();
                    event = new FMLNetworkEvent.ServerCustomPacketEvent(server.getNetworkManager(), msg);
                }
                return event;
            }
        };
        abstract FMLNetworkEvent.CustomPacketEvent<?> make(FMLProxyPacket msg);
    }

    private static EventFactory factory = FMLCommonHandler.instance().getSide() == Side.CLIENT ? EventFactory.CLIENT : EventFactory.SERVER;
    FMLEventChannel(String name)
    {
        this.channels = NetworkRegistry.INSTANCE.newChannel(name, new NetworkEventFiringHandler(this));
        this.eventBus = new EventBus();
    }

    /**
     * Register an event listener with this channel and bus. See {@link SubscribeEvent}
     *
     * @param object
     */
    public void register(Object object)
    {
        this.eventBus.register(object);
    }

    /**
     * Unregister an event listener from the bus.
     * @param object
     */
    public void unregister(Object object)
    {
        this.eventBus.unregister(object);
    }

    void fireRead(FMLProxyPacket msg, ChannelHandlerContext ctx)
    {
        FMLNetworkEvent.CustomPacketEvent<?> event = factory.make(msg);
        if (event != null)
        {
            this.eventBus.post(event);
            if (event.reply != null)
            {
                ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.REPLY);
                ctx.writeAndFlush(event.reply).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        }
    }

    public void fireUserEvent(Object evt, ChannelHandlerContext ctx)
    {
        FMLNetworkEvent.CustomNetworkEvent event = new FMLNetworkEvent.CustomNetworkEvent(evt);
        this.eventBus.post(event);
    }

    /**
     * Send a packet to all on the server
     *
     * @param pkt
     */
    public void sendToAll(FMLProxyPacket pkt)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send to a specific player
     *
     * @param pkt
     * @param player
     */
    public void sendTo(FMLProxyPacket pkt, EntityPlayerMP player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send to all around a point
     * @param pkt
     * @param point
     */
    public void sendToAllAround(FMLProxyPacket pkt, NetworkRegistry.TargetPoint point)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send to all in a dimension
     * @param pkt
     * @param dimensionId
     */
    public void sendToDimension(FMLProxyPacket pkt, int dimensionId)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Send to the server
     * @param pkt
     */
    public void sendToServer(FMLProxyPacket pkt)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
