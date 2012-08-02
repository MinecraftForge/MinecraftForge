/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.IInventory;

/** Inventory ranges mapped by side.  This class is implemented by TileEntities
 * that provide different inventory slot ranges to different sides.
 */
public interface ISidedInventory extends IInventory
{

    /**
     * Get the start of the side inventory.
     * @param side The global side to get the start of range.
     *      0: -Y (bottom side)
     *      1: +Y (top side)
     *      2: -Z
     *      3: +Z
     *      4: -X
     *      5: +x
     */
    int getStartInventorySide(int side);

    /**
     * Get the size of the side inventory.
     * @param side The global side.
     */
    int getSizeInventorySide(int side);
}

