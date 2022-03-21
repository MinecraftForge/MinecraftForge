/*
 * Copyright (c) Forge Development LLC and contributors
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

    private static Map<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> buildHandlers(@Nullable Connection manager)
    {

        VanillaPacketSplitter.RemoteCompatibility compatibility = manager == null ? VanillaPacketSplitter.RemoteCompatibility.ABSENT : VanillaPacketSplitter.getRemoteCompatibility(manager);
        if (compatibility == VanillaPacketSplitter.RemoteCompatibility.ABSENT)
        {
            return ImmutableMap.of();
        }
        ImmutableMap.Builder<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> builder = ImmutableMap.<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>>builder()
                .put(ClientboundUpdateRecipesPacket.class, ForgeConnectionNetworkFilter::splitPacket)
                .put(ClientboundUpdateTagsPacket.class, ForgeConnectionNetworkFilter::splitPacket)
                .put(ClientboundUpdateAdvancementsPacket.class, ForgeConnectionNetworkFilter::splitPacket);

        return builder.build();
    }

    @Override
    protected boolean isNecessary(Connection manager)
    {
        // not needed on local connections, because packets are not encoded to bytes there
        return !manager.isMemoryConnection() && VanillaPacketSplitter.isRemoteCompatible(manager);
    }

    private static void splitPacket(Packet<?> packet, List<? super Packet<?>> out)
    {
        VanillaPacketSplitter.appendPackets(
                ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND, packet, out
        );
    }

}
