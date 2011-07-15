/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;

public interface IBiomePopulator {

    /** 
     * This is called for 
     */
    public void populate(World world, BiomeGenBase biomegenbase, int x, int z);

}
