package net.minecraftforge.common;

import net.minecraft.src.*;
import net.minecraftforge.common.ForgeHooks.GrassEntry;
import net.minecraftforge.common.ForgeHooks.SeedEntry;
import net.minecraftforge.event.EventBus;

public class MinecraftForge
{
    /**
     * The core Forge EventBus, all events for Forge will be fired on this, 
     * you should use this to register all your listeners.
     * This replaces every register*Handler() function in the old version of Forge.
     */
    public static final EventBus EVENT_BUS = new EventBus();
    
    
    /** Register a new plant to be planted when bonemeal is used on grass.
     * @param block The block to place.
     * @param metadata The metadata to set for the block when being placed.
     * @param weight The weight of the plant, where red flowers are
     *               10 and yellow flowers are 20.
     */
    public static void addGrassPlant(Block block, int metadata, int weight)
    {
        ForgeHooks.grassList.add(new GrassEntry(block, metadata, weight));
    }

    /** 
     * Register a new seed to be dropped when breaking tall grass.
     * 
     * @param seed The item to drop as a seed.
     * @param weight The relative probability of the seeds, 
     *               where wheat seeds are 10.
     */
    public static void addGrassSeed(ItemStack seed, int weight)
    {
        ForgeHooks.seedList.add(new SeedEntry(seed, weight));
    }
}
