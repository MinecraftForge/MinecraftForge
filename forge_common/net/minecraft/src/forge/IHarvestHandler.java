/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.ItemTool;

public interface IHarvestHandler {
    public boolean canHarvestBlock(ItemTool item, Block block);
}
