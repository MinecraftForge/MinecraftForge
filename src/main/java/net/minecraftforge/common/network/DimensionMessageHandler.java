/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

public class DimensionMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.DimensionRegisterMessage>{

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DimensionRegisterMessage msg) throws Exception
    {
        if (DimensionManager.getRegistry().getByValue(msg.id) == null)
        {
            DimensionManager.registerDimensionInternal(msg.id + 1, msg.name, msg.dim, msg.data, msg.skyLight);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        LOGGER.error("DimensionMessageHandler exception", cause);
        super.exceptionCaught(ctx, cause);
    }

}
