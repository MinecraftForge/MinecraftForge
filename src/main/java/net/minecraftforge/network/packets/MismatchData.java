/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkContext.NetworkMismatchData;
import net.minecraftforge.network.NetworkContext.NetworkMismatchData.Version;

/**
 * Notifies the client of a channel mismatch on the server, so a {@link net.minecraftforge.client.gui.ModMismatchDisconnectedScreen} is used to notify the user of the disconnection.
 * This packet also sends the data of a channel mismatch (currently, the ids and versions of the mismatched channels) to the client for it to display the correct information in said screen.
 */
public record MismatchData(
    Map<ResourceLocation, Version> mismatched,
    Set<ResourceLocation> missing
) {
    public static final StreamCodec<FriendlyByteBuf, MismatchData> STREAM_CODEC = StreamCodec.ofMember(MismatchData::encode, MismatchData::decode);
    private static final int MAX_LENGTH = 0x100;

    public MismatchData(NetworkMismatchData data) {
        this(data.mismatched(), data.missing());
    }

    public static MismatchData decode(FriendlyByteBuf buf) {
        var mismatched = buf.readMap(
            i -> ResourceLocation.parse(i.readUtf(MAX_LENGTH)),
            i -> new Version(
                i.readUtf(MAX_LENGTH),
                i.readUtf(MAX_LENGTH)
            )
        );
        var missing = buf.readCollection(HashSet::new,
            i -> ResourceLocation.parse(i.readUtf(MAX_LENGTH))
        );
        return new MismatchData(mismatched, missing);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(mismatched,
            (o, k) -> o.writeUtf(k.toString(), MAX_LENGTH),
            (o, v) -> {
                o.writeUtf(v.received(), MAX_LENGTH);
                o.writeUtf(v.had(), MAX_LENGTH);
            }
        );
        buf.writeCollection(missing,
            (o, k) -> o.writeUtf(k.toString(), MAX_LENGTH)
        );
    }
}