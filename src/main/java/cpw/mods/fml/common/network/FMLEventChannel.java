package cpw.mods.fml.common.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.util.EnumMap;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

public class FMLEventChannel {
    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private EventBus eventBus;

    FMLEventChannel(String name)
    {
        this.channels = NetworkRegistry.INSTANCE.newChannel(name, new NetworkEventFiringHandler(this));
        this.eventBus = new EventBus();
    }

    public void register(Object object)
    {
        this.eventBus.register(object);
    }

    public void unregister(Object object)
    {
        this.eventBus.unregister(object);
    }

    void fireRead(FMLProxyPacket msg, ChannelHandlerContext ctx)
    {
        FMLNetworkEvent.CustomPacketEvent<?> event = null;
        if (msg.handler() instanceof NetHandlerPlayClient)
        {
            NetHandlerPlayClient client = (NetHandlerPlayClient) msg.handler();
            event = new FMLNetworkEvent.ClientCustomPacketEvent(client.func_147298_b(), msg);
        }
        else if (msg.handler() instanceof NetHandlerPlayServer)
        {
            NetHandlerPlayServer server = (NetHandlerPlayServer) msg.handler();
            event = new FMLNetworkEvent.ServerCustomPacketEvent(server.func_147362_b(), msg);
        }
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

    public void sendToAll(FMLProxyPacket pkt)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendTo(FMLProxyPacket pkt, EntityPlayerMP player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllAround(FMLProxyPacket pkt, NetworkRegistry.TargetPoint point)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToDimension(FMLProxyPacket pkt, int dimensionId)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        channels.get(Side.SERVER).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToServer(FMLProxyPacket pkt)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
