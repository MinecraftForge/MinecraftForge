/*
 * Minecraft Forge
 * Copyright (c) 2017.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.items.customslots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * To be used in Containers.
 */
public class SlotExtension extends Slot
{
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IExtensionSlot slot;

    public SlotExtension(IExtensionSlot slot, int x, int y)
    {
        super(emptyInventory, 0, x, y);
        this.slot = slot;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack.isEmpty())
            return false;

        return slot.canEquip(stack);
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    @Override
    public ItemStack getStack()
    {
        return slot.getContents();
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    /**
     * Helper method to put a stack in the slot.
     */
    @Override
    public void putStack(ItemStack stack)
    {
        slot.setContents(stack);
        this.onSlotChanged();
    }

    /**
     * if par2 has more items than par1, onCrafting(item,countIncrease) is called
     */
    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {

    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return 1;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return slot.canUnequip(slot.getContents());
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int amount)
    {
        ItemStack itemstack = slot.getContents();

        int available = Math.min(itemstack.getCount(), amount);
        int remaining = itemstack.getCount() - available;

        ItemStack split = itemstack.copy();
        split.setCount(available);
        itemstack.setCount(remaining);

        if (remaining <= 0)
            slot.setContents(ItemStack.EMPTY);

        this.onSlotChanged();

        return split;
    }

    public IExtensionSlot getExtensionSlot()
    {
        return slot;
    }

    @Override
    public boolean isSameInventory(Slot other)
    {
        return other instanceof SlotExtension && ((SlotExtension) other).getExtensionSlot() == this.slot;
    }
}
