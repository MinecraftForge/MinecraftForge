/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.RegistryManager;

public record RegistryList(
    int token,
    List<ResourceLocation> normal,
    List<ResourceKey<? extends Registry<?>>> datapacks) {

    public static final StreamCodec<FriendlyByteBuf, RegistryList> STREAM_CODEC = StreamCodec.ofMember(RegistryList::encode, RegistryList::decode);

    public RegistryList(int token) {
        this(token, RegistryManager.getRegistryNamesForSyncToClient(), List.copyOf(DataPackRegistriesHooks.getSyncedCustomRegistries()));
    }

    public static RegistryList decode(FriendlyByteBuf buf) {
        var token = buf.readVarInt();
        var normal = buf.readList(FriendlyByteBuf::readResourceLocation);
        List<ResourceKey<? extends Registry<?>>> datapacks = buf.readList(b -> ResourceKey.createRegistryKey(buf.readResourceLocation()));
        return new RegistryList(token, normal, datapacks);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(token());
        buf.writeCollection(normal(), FriendlyByteBuf::writeResourceLocation);
        buf.writeCollection(datapacks(), FriendlyByteBuf::writeResourceKey);
    }
}