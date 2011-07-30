/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;

public interface IBiomePopulator {

    /** 
     * This is called for each chunk, after the rest of the generation, and
     * allow contributers to add additional blocks in the world.
     * 
     * @see MinecraftForge#registerBiomePopulate(IBiomePopulator)
     */
    public void populate(World world, BiomeGenBase biomegenbase, int x, int z);

}
