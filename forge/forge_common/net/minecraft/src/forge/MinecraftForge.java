package net.minecraft.src.forge;

import java.util.LinkedList;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class MinecraftForge {

    private static LinkedList<IBucketHandler> bucketHandlers = new LinkedList<IBucketHandler>();
    private static LinkedList<IBiomePopulator> biomePopulators = new LinkedList<IBiomePopulator>();

    public static void registerCustomBucketHander(IBucketHandler handler) {
        bucketHandlers.add(handler);
    }

    public static void registerBiomePopulate(IBiomePopulator populator) {
        biomePopulators.add(populator);
    }

    public static ItemStack fillCustomBucket(World w, int i, int j, int k) {
        for (IBucketHandler handler : bucketHandlers) {
            ItemStack stack = handler.fillCustomBucket(w, i, j, k);

            if (stack != null) {
                return stack;
            }
        }

        return null;
    }

    public static void populateBiome(World world, BiomeGenBase biomegenbase,
            int x, int z) {
        for (IBiomePopulator populator : biomePopulators) {
            populator.populate(world, biomegenbase, x, z);
        }
    }

}
