/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common.network.handshake;

import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class HandshakeMessageHandler<S extends Enum<S> & IHandshakeState<S>> extends SimpleChannelInboundHandler<FMLHandshakeMessage> {
    private static final AttributeKey<IHandshakeState<?>> STATE = AttributeKey.valueOf("fml:handshake-state");
    private final AttributeKey<S> fmlHandshakeState;
    private S initialState;
    private Class<S> stateType;

    @SuppressWarnings("unchecked")
    public HandshakeMessageHandler(Class<S> stateType)
    {
        fmlHandshakeState = (AttributeKey<S>) ((Object)STATE);
        initialState = Enum.valueOf(stateType, "START");
        this.stateType = stateType;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLHandshakeMessage msg) throws Exception
    {
        S state = ctx.attr(fmlHandshakeState).get();
        FMLLog.fine(stateType.getSimpleName() + ": " + msg.toString(stateType) + "->" + state.getClass().getName().substring(state.getClass().getName().lastIndexOf('.')+1)+":"+state);
        S newState = state.accept(ctx, msg);
        FMLLog.fine("  Next: " + newState.name());
        ctx.attr(fmlHandshakeState).set(newState);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.attr(fmlHandshakeState).set(initialState);
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        S state = ctx.attr(fmlHandshakeState).get();
        FMLLog.fine(stateType.getSimpleName() + ": null->" + state.getClass().getName().substring(state.getClass().getName().lastIndexOf('.')+1)+":"+state);
        S newState = state.accept(ctx, null);
        FMLLog.fine("  Next: " + newState.name());
        ctx.attr(fmlHandshakeState).set(newState);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "HandshakeMessageHandler exception");
        super.exceptionCaught(ctx, cause);
    }
}
