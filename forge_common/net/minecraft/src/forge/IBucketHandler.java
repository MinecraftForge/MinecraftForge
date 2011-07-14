package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public interface IBucketHandler {

	public ItemStack fillCustomBucket (World w, int i, int j, int k);
	
}
