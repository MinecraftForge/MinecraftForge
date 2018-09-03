package net.minecraftforge.common.network;

import java.util.HashSet;
import java.util.Set;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = "forge")
public class NetworkChannelHelper
{
    private static Set<ChannelOutboundInvoker> markedChannels;
    
    static
    {
        markedChannels = new HashSet<ChannelOutboundInvoker>();
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
    
    @SubscribeEvent
    public static void tickServer(TickEvent.ServerTickEvent event)
    {
        synchronized (markedChannels)
        {
            markedChannels.forEach(ChannelOutboundInvoker::flush);
            markedChannels.clear();
        }
        
    }
    
    @SubscribeEvent
    public static void tickClient(TickEvent.ClientTickEvent event)
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
}
