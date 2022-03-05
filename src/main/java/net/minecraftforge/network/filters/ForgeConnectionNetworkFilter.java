/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.filters;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import io.netty.channel.ChannelHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;

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
