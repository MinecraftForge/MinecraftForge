/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
