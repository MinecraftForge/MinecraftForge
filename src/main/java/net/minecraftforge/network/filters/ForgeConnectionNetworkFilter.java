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

package net.minecraftforge.network.filters;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import io.netty.channel.ChannelHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.Connection;

import com.google.common.collect.ImmutableMap;

/**
 * Network filter for forge-forge connections.
 */
@ChannelHandler.Sharable
public class ForgeConnectionNetworkFilter extends VanillaPacketFilter
{
    public ForgeConnectionNetworkFilter(@Nullable Connection manager)
    {
        super(buildHandlers(manager));
    }

    private static Map<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> buildHandlers(@Nullable Connection connection)
    {
        if (connection == null)
        {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> builder = ImmutableMap.builder();
        if (VanillaPacketSplitter.shouldBeUsed(connection))
        {
            VanillaPacketSplitter.registerHandlers(builder);
        }
        if (ExtendedMobEffectChannel.shouldBeUsed(connection))
        {
            ExtendedMobEffectChannel.registerHandlers(builder);
        }

        return builder.build();
    }

    @Override
    protected boolean isNecessary(Connection connection)
    {
        return ExtendedMobEffectChannel.shouldBeUsed(connection) || VanillaPacketSplitter.shouldBeUsed(connection);
    }

}
