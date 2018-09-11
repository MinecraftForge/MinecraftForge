/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

public class NetworkChannelHelper
{
    private static Set<ChannelOutboundInvoker> markedChannels;
    
    public static int flushDelay;
    
    static
    {
        markedChannels = new HashSet<ChannelOutboundInvoker>();
        Thread t = new Thread(NetworkChannelHelper::run, "NetworkChannelFlush");
        t.setDaemon(true);
        t.start();
        flushDelay = Integer.getInteger("fml.network.flushDelay", 50);
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
        return !ForgeModContainer.flushNetworkOnTick;
    }
    
    public static void run()
    {
        try
        {
            while(true)
            {
                flushAllChannels();
                Thread.sleep(flushDelay);
            }
        }
        catch(InterruptedException e) {
            
        }
    }
}
