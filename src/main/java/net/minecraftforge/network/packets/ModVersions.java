/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

/**
 * Prefixes S2CModList by sending additional data about the mods installed on the server to the client
 * The mod data is stored as follows: [modId -> [modName, modVersion]]
 */
public record ModVersions(Map<String, Info> mods) {
    public static final StreamCodec<FriendlyByteBuf, ModVersions> STREAM_CODEC = StreamCodec.ofMember(ModVersions::encode, ModVersions::decode);

    private static final int MAX_LENGTH = 0x100;

    public static ModVersions create() {
        return new ModVersions(ModList.get().getMods().stream().collect(Collectors.toMap(
            IModInfo::getModId,
            mod -> new Info(mod.getDisplayName(), mod.getVersion().toString())
        )));
    }

    public static ModVersions decode(FriendlyByteBuf buf) {
        return new ModVersions(buf.readMap(
            o -> o.readUtf(MAX_LENGTH),
            o -> new Info(
                o.readUtf(MAX_LENGTH),
                o.readUtf(MAX_LENGTH)
            )
        ));
    }

    public void encode(FriendlyByteBuf output) {
        output.writeMap(mods,
            (o, s) -> o.writeUtf(s, MAX_LENGTH),
            (o, p) -> {
                o.writeUtf(p.name(), MAX_LENGTH);
                o.writeUtf(p.version(), MAX_LENGTH);
            }
        );
    }

    public record Info(String name, String version) { }
}