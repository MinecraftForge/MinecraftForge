/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

public record ChannelVersions(Map<ResourceLocation, Integer> channels) {
    public ChannelVersions() {
        this(NetworkRegistry.buildChannelVersions());
    }

    public static ChannelVersions decode(FriendlyByteBuf buf) {
        Map<ResourceLocation, Integer> channels = new HashMap<>();
        int len = buf.readVarInt();
        for (int x = 0; x < len; x++)
            channels.put(buf.readResourceLocation(), buf.readVarInt());

        return new ChannelVersions(channels);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(channels.size());
        channels.forEach((k, v) -> {
            buf.writeResourceLocation(k);
            buf.writeVarInt(v);
        });
    }
}