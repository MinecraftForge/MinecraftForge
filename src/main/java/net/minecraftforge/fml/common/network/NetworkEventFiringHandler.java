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

package net.minecraftforge.fml.common.network;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import org.apache.logging.log4j.Level;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Use this handler as the only thing in your channel, to receive network events
 * whenever your channel receives a message.
 * Note: it will not forward on to other handlers.
 *
 * @author cpw
 *
 */
@Sharable
public class NetworkEventFiringHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {
    private FMLEventChannel eventChannel;

    NetworkEventFiringHandler(FMLEventChannel fmlEventChannel)
    {
        this.eventChannel = fmlEventChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception
    {
        eventChannel.fireRead(msg,ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        eventChannel.fireUserEvent(evt,ctx);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log.error("NetworkEventFiringHandler exception", cause);
        super.exceptionCaught(ctx, cause);
    }
}
