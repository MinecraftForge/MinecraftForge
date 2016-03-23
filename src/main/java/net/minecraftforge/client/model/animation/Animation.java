package net.minecraftforge.client.model.animation;

import net.minecraft.world.World;

public enum Animation
{
    INSTANCE;

    private float clientPartialTickTime;

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
        return (world.getTotalWorldTime() + tickProgress) / 20;
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
