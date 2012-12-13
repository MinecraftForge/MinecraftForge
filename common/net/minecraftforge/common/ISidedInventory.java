/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import net.minecraft.inventory.IInventory;

/** Inventory ranges mapped by side.  This class is implemented by TileEntities
 * that provide different inventory slot ranges to different sides.
 */
public interface ISidedInventory extends IInventory
{

    /**
     * Get the start of the side inventory.
     * @param side The global side to get the start of range.
     */
    int getStartInventorySide(ForgeDirection side);

    /**
     * Get the size of the side inventory.
     * @param side The global side.
     */
    int getSizeInventorySide(ForgeDirection side);
}

