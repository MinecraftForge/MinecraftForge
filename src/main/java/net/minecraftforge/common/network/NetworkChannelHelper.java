package net.minecraftforge.common.network;

import java.util.HashSet;
import java.util.Set;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod.EventBusSubscriber(modid = "forge")
public class NetworkChannelHelper
{
    private static Set<ChannelOutboundInvoker> markedChannels;
    
    static
    {
        markedChannels = new HashSet<ChannelOutboundInvoker>();
        Thread t = new Thread(NetworkChannelHelper::run, "NetworkChannelFlush");
        t.setDaemon(true);
        t.start();
    }
    
    
    
    public static ChannelFuture writeAndFlush(ChannelOutboundInvoker channel, Object msg, INetHandler packetListener)
    {
        if(packetListener==null)
        {
            return channel.writeAndFlush(msg);
        }
        else if(packetListener instanceof INetHandlerPlayClient || packetListener instanceof INetHandlerPlayServer)
        {
            return writeAndFlush(channel, msg);
        }
        else
        {
            return channel.writeAndFlush(msg);
        }
    }
    
    public static ChannelFuture writeAndFlush(ChannelOutboundInvoker channel, Object msg)
    {
        if(isInstantFlushEnabled())
        {
            return channel.writeAndFlush(msg);
        }
        else
        {
            markForFlush(channel);
            return channel.write(msg);
        }
    }
    
    public static ChannelFuture writeAndFlush(ChannelOutboundInvoker channel, Object msg, ChannelPromise promise)
    {
        if(isInstantFlushEnabled())
        {
            return channel.writeAndFlush(msg, promise);
        }
        else
        {
            markForFlush(channel);
            return channel.write(msg, promise);
        }
    }
    
    public static void markForFlush(ChannelOutboundInvoker channel)
    {
        synchronized (markedChannels)
        {
            markedChannels.add(channel);
        }
    }
    
    public static void flushAllChannels()
    {
        synchronized (markedChannels)
        {
            markedChannels.forEach(ChannelOutboundInvoker::flush);
            markedChannels.clear();
        }
    }
    
    private static boolean isInstantFlushEnabled()
    {
        return ForgeModContainer.instantNetworkFlushing;
    }
    
    public static void run()
    {
        try
        {
            while(true)
            {
                flushAllChannels();
                Thread.sleep(50);
            }
        }
        catch(InterruptedException e) {
            
        }
    }
}
