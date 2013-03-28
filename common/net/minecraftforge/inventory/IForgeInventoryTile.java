package net.minecraftforge.inventory;

import net.minecraftforge.common.ForgeDirection;

/**
 * This interface is implemented by tile entities that use the Forge inventory system.
 * 
 */
public interface IForgeInventoryTile {
    IForgeInventory getSideInventory(ForgeDirection side);
}
