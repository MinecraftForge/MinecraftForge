/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.registries.singularityRegistry.Snapshot;

public record RegistryData(int token, Identifier name, Snapshot data) {
    public static final StreamCodec<FriendlyByteBuf, RegistryData> STREAM_CODEC = StreamCodec.ofMember(RegistryData::encode, RegistryData::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(token);
        buf.writeIdentifier(name);
        buf.writeBytes(data.getPacketData());
    }

    public static RegistryData decode(FriendlyByteBuf buf) {
        return new RegistryData(buf.readVarInt(), buf.readIdentifier(), Snapshot.read(buf));
    }
}
