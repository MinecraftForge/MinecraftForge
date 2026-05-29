/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.packets;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.network.NetworkRegistry;

public record ChannelVersions(Map<Identifier, @NotNull Integer> channels) {
    public static StreamCodec<FriendlyByteBuf, ChannelVersions> STREAM_CODEC = StreamCodec.ofMember(ChannelVersions::encode, ChannelVersions::decode);

    public ChannelVersions() {
        this(NetworkRegistry.buildChannelVersions());
    }

    public static ChannelVersions decode(FriendlyByteBuf buf) {
        return new ChannelVersions(buf.readMap(Object2IntOpenHashMap::new, FriendlyByteBuf::readIdentifier, FriendlyByteBuf::readVarInt));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(channels, FriendlyByteBuf::writeIdentifier, FriendlyByteBuf::writeVarInt);
    }
}
