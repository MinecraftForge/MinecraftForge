/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.ModList;

/**
 * Prefixes S2CModList by sending additional data about the mods installed on the server to the client
 * The mod data is stored as follows: [modId -> [modName, modVersion]]
 */
public record ModVersions(Map<String, Info> mods) {
    private static final int MAX_LENGTH = 0x100;

    public static ModVersions create() {
        Map<String, ModVersions.Info> mods = new HashMap<>();
        ModList.get().getMods().stream().forEach(mod ->
            mods.put(
                mod.getModId(),
                new Info(mod.getDisplayName(), mod.getVersion().toString())
            )
        );
        return new ModVersions(mods);
    }

    public static ModVersions decode(FriendlyByteBuf buf) {
        var mods = buf.<String, Info>readMap(
            o -> o.readUtf(MAX_LENGTH),
            o -> new Info(
                o.readUtf(MAX_LENGTH),
                o.readUtf(MAX_LENGTH)
            )
        );
        return new ModVersions(mods);
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

    public record Info(String name, String version) {}
}