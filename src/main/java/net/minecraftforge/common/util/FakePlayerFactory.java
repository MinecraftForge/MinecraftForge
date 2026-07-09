/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.world.server.ServerWorld;

//To be expanded for generic Mod fake players?
public class FakePlayerFactory
{
    private static GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
    // Map of all active fake player usernames to their entities
    private static Map<GameProfile, FakePlayer> fakePlayers = Maps.newHashMap();
    private static WeakReference<FakePlayer> MINECRAFT_PLAYER = null;

    public static FakePlayer getMinecraft(ServerWorld world)
    {
        FakePlayer ret = MINECRAFT_PLAYER != null ? MINECRAFT_PLAYER.get() : null;
        if (ret == null)
        {
            ret = FakePlayerFactory.get(world,  MINECRAFT);
            MINECRAFT_PLAYER = new WeakReference<FakePlayer>(ret);
        }
        return ret;
    }

    /**
     * Get a fake player with a given username,
     * Mods should either hold weak references to the return value, or listen for a
     * WorldEvent.Unload and kill all references to prevent worlds staying in memory.
     */
    public static FakePlayer get(ServerWorld world, GameProfile username)
    {
        if (!fakePlayers.containsKey(username))
        {
            FakePlayer fakePlayer = new FakePlayer(world, username);
            fakePlayers.put(username, fakePlayer);
        }

        return fakePlayers.get(username);
    }

    public static void unloadWorld(ServerWorld world)
    {
        fakePlayers.entrySet().removeIf(entry -> entry.getValue().world == world);
        if (MINECRAFT_PLAYER != null && MINECRAFT_PLAYER.get() != null && MINECRAFT_PLAYER.get().world == world) // This shouldn't be strictly necessary, but lets be aggressive.
        {
            FakePlayer mc = MINECRAFT_PLAYER.get();
            if (mc != null && mc.world == world)
            {
                MINECRAFT_PLAYER = null;
            }
        }
    }
}
