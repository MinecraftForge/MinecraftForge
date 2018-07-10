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

package net.minecraftforge.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

import javax.annotation.Nullable;

public interface IHandshakeState<S> {
    /**
     * Accepts FML handshake message for this state, and if needed - switches to another handshake state
     * using the provided consumer.
     *
     * The consumer allows to set new state before sending any messages to avoid race conditions.
     */
    void accept(ChannelHandlerContext ctx, @Nullable FMLHandshakeMessage msg, Consumer<? super S> cons);
}
