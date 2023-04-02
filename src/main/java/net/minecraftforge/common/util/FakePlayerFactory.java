/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerLevel;

//To be expanded for generic Mod fake players?
public class FakePlayerFactory
{
    private static final GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
    // Map of all active fake player usernames to their entities
    private static final Map<FakePlayerKey, FakePlayer> fakePlayers = Maps.newHashMap();
    private record FakePlayerKey(ServerLevel level, GameProfile username) { }

    public static FakePlayer getMinecraft(ServerLevel level)
    {
        return get(level, MINECRAFT);
    }

    /**
     * Get a fake player with a given username,
     * Mods should either hold weak references to the return value, or listen for a
     * WorldEvent.Unload and kill all references to prevent worlds staying in memory,
     * or call this function every time and let Forge take care of the cleanup.
     */
    public static FakePlayer get(ServerLevel level, GameProfile username)
    {
        FakePlayerKey key = new FakePlayerKey(level, username);
        return fakePlayers.computeIfAbsent(key, k -> new FakePlayer(k.level(), k.username()));
    }

    public static void unloadLevel(ServerLevel level)
    {
        fakePlayers.entrySet().removeIf(entry -> entry.getValue().level() == level);
    }
}
