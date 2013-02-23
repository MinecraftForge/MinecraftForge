/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */
 
package net.minecraftforge.common;
 
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
 
/**
 * ItemStack input- and output-per-slot information. This class is implemented
 * by TileEntities that have specific slots for specific items or outputs.
 */
public interface ISpecialSlotInventory extends IInventory
{
    /**
     * <p>Queries the specified slot for the maximum amount of the passed ItemStack
     * that it can accept (NOT the slot size).</p>
     *
     * <p>This will return zero if the slot is full or output-only.</p>
     *
     * @param slot The slot index to query the stack against
     * @param item The ItemStack to be checked
     * @return The remaining stack size that this slot can accept
     */
    public int getSlotAcceptedStackSize(int slot, ItemStack item);
 
    /**
     * <p>Queries the specified slot for the size of ItemStack that can be extracted.</p>
     *
     * <p>This will return zero if the slot is empty or input-only.</p>
     *
     * @param slot The slot index to query for extraction
     * @return The stack size available to be taken from this slot
     */
    public int getSlotAvailableStackSize(int slot);
}