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

package net.minecraftforge.fml.common.network.handshake;

import net.minecraftforge.fml.common.FMLLog;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class HandshakeMessageHandler<S extends Enum<S> & IHandshakeState<S>> extends SimpleChannelInboundHandler<FMLHandshakeMessage> {
    private static final AttributeKey<IHandshakeState<?>> STATE = AttributeKey.valueOf("fml:handshake-state");
    private final AttributeKey<S> fmlHandshakeState;
    private final S initialState;
    private final S errorState;
    private final Class<S> stateType;

    @SuppressWarnings("unchecked")
    public HandshakeMessageHandler(Class<S> stateType)
    {
        fmlHandshakeState = (AttributeKey<S>) ((Object)STATE);
        initialState = Enum.valueOf(stateType, "START");
        errorState = Enum.valueOf(stateType, "ERROR");
        this.stateType = stateType;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLHandshakeMessage msg) throws Exception
    {
        S state = ctx.channel().attr(fmlHandshakeState).get();
        FMLLog.log.debug("{}: {}->{}:{}", stateType.getSimpleName(), msg.toString(stateType), state.getClass().getName().substring(state.getClass().getName().lastIndexOf('.')+1), state);
        state.accept(ctx, msg, s ->
        {
            FMLLog.log.debug("  Next: {}", s.name());
            ctx.channel().attr(fmlHandshakeState).set(s);
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.channel().attr(fmlHandshakeState).set(initialState);
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        S state = ctx.channel().attr(fmlHandshakeState).get();
        FMLLog.log.debug("{}: null->{}:{}", stateType.getSimpleName(), state.getClass().getName().substring(state.getClass().getName().lastIndexOf('.')+1), state);
        state.accept(ctx, null, s ->
        {
            FMLLog.log.debug("  Next: {}", s.name());
            ctx.channel().attr(fmlHandshakeState).set(s);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log.error("HandshakeMessageHandler exception", cause);
        ctx.channel().attr(fmlHandshakeState).set(errorState);
        super.exceptionCaught(ctx, cause);
    }
}
