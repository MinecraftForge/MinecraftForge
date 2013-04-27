package net.minecraftforge.common;

import net.minecraft.inventory.IInventory;

/**
 * This enables transport mods to access the full inventory
 * of a multiblock chest that doesn't extend TileEntityChest.
 *
 * Allowing for chests of custom shapes and sizes without loosing
 * automation support, or requiring a connection to every TileEntity.
 */
public interface IMultiBlockInventory extends IInventory
{
    /**
     * Returns the IInventory representing this blocks storage.
     * 
     * If it's currently not a multiblock chest just return the blocks TileEntity,
     * Otherwise return an IInventory instance that is aware of all TileEntities needed.
     * 
     * See TileEntityChest for a usage example.
     */
    IInventory getMultiBlockInventory();
}