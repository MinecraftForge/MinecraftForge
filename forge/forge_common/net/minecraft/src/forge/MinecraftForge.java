package net.minecraft.src.forge;

import java.util.LinkedList;

import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class MinecraftForge {

	private static LinkedList<IBucketHandler> bucketHandlers = new LinkedList<IBucketHandler>();
	
	public static ItemStack fillCustomBucket (World w, int i, int j, int k) {
		for (IBucketHandler handler : bucketHandlers) {
			ItemStack stack = handler.fillCustomBucket(w, i, j, k);
			
			if (stack != null) {
				return stack;
			}
		}
		
		return null;
	}
	
	public static void registerCustomBucketHander (IBucketHandler handler) {
		bucketHandlers.add(handler);
	}
	
}
