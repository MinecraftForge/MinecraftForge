/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.ItemTool;

public interface IHarvestHandler {
    
    /**
     * Return true if the tool in parameter is known to be able to harvest
     * the given block, false if this handler does not know of any particular
     * harvesting. Other rules may activate the harvesting outside of this
     * handler. This is typically used to add e.g. blocks only extracted by 
     * diamond pixaxe
     * 
     * @see MinecraftForge#registerHarvestHandler(IHarvestHandler)
     */
    public boolean canHarvestBlock(ItemTool item, Block block);
}
