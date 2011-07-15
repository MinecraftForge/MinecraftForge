/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public interface IBucketHandler {

    public ItemStack fillCustomBucket(World w, int i, int j, int k);

}
