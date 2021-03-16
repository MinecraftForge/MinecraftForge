/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.network;

import java.util.List;

import io.netty.channel.ChannelHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraftforge.fml.network.NetworkHooks;

import com.google.common.collect.ImmutableMap;

/**
 * Network filter for forge-forge connections.
 */
@ChannelHandler.Sharable
public class ForgeConnectionNetworkFilter extends VanillaPacketFilter
{

    public ForgeConnectionNetworkFilter()
    {
        super(
                ImmutableMap.of(
                        SUpdateRecipesPacket.class, ForgeConnectionNetworkFilter::splitPacket,
                        STagsListPacket.class, ForgeConnectionNetworkFilter::splitPacket
                )
        );
    }

    @Override
    protected boolean isNecessary(NetworkManager manager)
    {
        // not needed on local connections, because packets are not encoded to bytes there
        return !manager.isMemoryConnection() && !VanillaPacketSplitter.isRemoteCompatible(manager);
    }

    private static void splitPacket(IPacket<?> packet, List<? super IPacket<?>> out)
    {
        VanillaPacketSplitter.appendPackets(
                ProtocolType.PLAY, PacketDirection.CLIENTBOUND, packet, out
        );
    }

}
