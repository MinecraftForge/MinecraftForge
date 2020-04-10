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

import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class FMLHandshakeCodec extends FMLIndexedMessageToMessageCodec<FMLHandshakeMessage> {
    public FMLHandshakeCodec()
    {
        addDiscriminator((byte)0, FMLHandshakeMessage.ServerHello.class);
        addDiscriminator((byte)1, FMLHandshakeMessage.ClientHello.class);
        addDiscriminator((byte)2, FMLHandshakeMessage.ModList.class);
        addDiscriminator((byte)3, FMLHandshakeMessage.RegistryData.class);
        addDiscriminator((byte)-1, FMLHandshakeMessage.HandshakeAck.class);
        addDiscriminator((byte)-2, FMLHandshakeMessage.HandshakeReset.class);
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, FMLHandshakeMessage msg, ByteBuf target) throws Exception
    {
        msg.toBytes(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, FMLHandshakeMessage msg)
    {
        msg.fromBytes(source);
    }
}
