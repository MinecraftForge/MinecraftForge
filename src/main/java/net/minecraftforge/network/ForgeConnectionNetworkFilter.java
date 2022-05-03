/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import io.netty.channel.ChannelHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;

import com.google.common.collect.ImmutableMap;

/**
 * Network filter for forge-forge connections.
 */
@ChannelHandler.Sharable
public class ForgeConnectionNetworkFilter extends VanillaPacketFilter
{

    @Deprecated
    public ForgeConnectionNetworkFilter()
    {
        this(null);
    }

    public ForgeConnectionNetworkFilter(@Nullable NetworkManager manager)
    {
        super(buildHandlers(manager));
    }

    private static Map<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> buildHandlers(@Nullable NetworkManager manager)
    {

        VanillaPacketSplitter.RemoteCompatibility compatibility = manager == null ? VanillaPacketSplitter.RemoteCompatibility.ABSENT : VanillaPacketSplitter.getRemoteCompatibility(manager);
        if (compatibility == VanillaPacketSplitter.RemoteCompatibility.ABSENT)
        {
            return ImmutableMap.of();
        }
        ImmutableMap.Builder<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> builder = ImmutableMap.<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>>builder()
                .put(SUpdateRecipesPacket.class, ForgeConnectionNetworkFilter::splitPacket)
                .put(STagsListPacket.class, ForgeConnectionNetworkFilter::splitPacket);

        if (compatibility == VanillaPacketSplitter.RemoteCompatibility.V11)
        {
            builder.put(SAdvancementInfoPacket.class, ForgeConnectionNetworkFilter::splitPacket);
        }
        return builder.build();
    }

    @Override
    protected boolean isNecessary(NetworkManager manager)
    {
        // not needed on local connections, because packets are not encoded to bytes there
        return !manager.isMemoryConnection() && VanillaPacketSplitter.isRemoteCompatible(manager);
    }

    private static void splitPacket(IPacket<?> packet, List<? super IPacket<?>> out)
    {
        VanillaPacketSplitter.appendPackets(
                ProtocolType.PLAY, PacketDirection.CLIENTBOUND, packet, out
        );
    }

}
