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
    private static LinkedList<IHarvestHandler> harvestHandlers = new LinkedList<IHarvestHandler>();

    /**
     * Registers a new custom bucket handler.
     */
    public static void registerCustomBucketHander(IBucketHandler handler) {
        bucketHandlers.add(handler);
    }
    
    /**
     * Registers a new harvest handler.
     */
    public static void registerHarvestHandler(IHarvestHandler handler) {
        harvestHandlers.add(handler);
    }

    /**
     * This is not supposed to be called outside of Minecraft internals.
     */
    public static ItemStack fillCustomBucket(World w, int i, int j, int k) {
        for (IBucketHandler handler : bucketHandlers) {
            ItemStack stack = handler.fillCustomBucket(w, i, j, k);

            if (stack != null) {
                return stack;
            }
        }

        return null;
    }

    /**
     * This is not supposed to be called outside of Minecraft internals.
     */
    public static boolean canHarvestBlock(ItemTool item, Block block) {
        for (IHarvestHandler handler : harvestHandlers) {
            if (handler.canHarvestBlock(item, block)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Add a block to the list of blocks to which the pickaxe is know to be
     * effective.
     */
    public static void addPickaxeBlockEffectiveAgainst (Block block) {
        ((ItemTool) Item.pickaxeWood).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeStone).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeSteel).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeGold).addBlockEffectiveAgainst(block);
        ((ItemTool) Item.pickaxeDiamond).addBlockEffectiveAgainst(block);
    }
}
