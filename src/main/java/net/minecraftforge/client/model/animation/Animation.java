/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.animation;

import net.minecraft.world.World;

import java.lang.ref.WeakReference;

public enum Animation
{
    INSTANCE;

    private float clientPartialTickTime;

    private static long epochTime;
    private static WeakReference<World> worldRef;

    /**
     * Get the global world time for the current tick, in seconds.
     */
    public static float getWorldTime(World world)
    {
        return getWorldTime(world, 0);
    }

    /**
     * Get the global world time for the current tick + partial tick progress, in seconds.
     */
    public static float getWorldTime(World world, float tickProgress)
    {
        if (worldRef == null || worldRef.get() != world)
        {
            epochTime = world.getGameTime();
            worldRef = new WeakReference<>(world);
        }
        return (world.getGameTime() - epochTime + tickProgress) / 20;
    }

    /**
     * Get current partialTickTime.
     */
    public static float getPartialTickTime()
    {
        return INSTANCE.clientPartialTickTime;
    }

    /**
     * Internal hook, do not use.
     */
    public static void setClientPartialTickTime(float clientPartialTickTime) {
        Animation.INSTANCE.clientPartialTickTime = clientPartialTickTime;
    }
}
