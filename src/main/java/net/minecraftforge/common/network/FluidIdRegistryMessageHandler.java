/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.FMLLog;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FluidIdRegistryMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.FluidIdMapMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ForgeMessage.FluidIdMapMessage msg) throws Exception
    {
        FluidRegistry.initFluidIDs(msg.fluidIds, msg.defaultFluids);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log.error("FluidIdRegistryMessageHandler exception", cause);
        super.exceptionCaught(ctx, cause);
    }

}
