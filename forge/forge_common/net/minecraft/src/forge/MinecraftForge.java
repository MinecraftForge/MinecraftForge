/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import java.util.LinkedList;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.World;

public class MinecraftForge {

    private static LinkedList<IBucketHandler> bucketHandlers = new LinkedList<IBucketHandler>();
    private static LinkedList<IBiomePopulator> biomePopulators = new LinkedList<IBiomePopulator>();
    private static LinkedList<IHarvestHandler> harvestHandlers = new LinkedList<IHarvestHandler>();

    public static void registerCustomBucketHander(IBucketHandler handler) {
        bucketHandlers.add(handler);
    }

    public static void registerBiomePopulate(IBiomePopulator populator) {
        biomePopulators.add(populator);
    }
    
    public static void registerHarvestHandler(IHarvestHandler handler) {
        harvestHandlers.add(handler);
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

    public static boolean canHarvestBlock(ItemTool item, Block block) {
        for (IHarvestHandler handler : harvestHandlers) {
            if (handler.canHarvestBlock(item, block)) {
                return true;
            }
        }
        
        return false;
    }

    public void addPixaxeBlockEffectiveAgainst (Block block) {
        ((ItemTool) Item.pickaxeWood).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeStone).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeSteel).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeGold).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeDiamond).addBlockEffectiveAgainst(block);
    }
}
