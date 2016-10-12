/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotItemHandler extends Slot
{
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IItemHandler itemHandler;
    private final IItemHandlerContainer itemHandlerContainer;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.itemHandlerContainer = (itemHandler instanceof IItemHandlerContainer) ? (IItemHandlerContainer)itemHandler : null;
        this.index = index;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack == null)
            return false;
        IItemHandlerContainer ihc = getItemHandlerContainer();
        if (ihc != null)
            return ihc.isItemValidForSlot(slotNumber, stack);
        ItemStack remainder = itemHandler.insertItem(index, stack, true);
        return remainder == null || remainder.stackSize < stack.stackSize;
    }

    @Override
    public ItemStack getStack()
    {
        return this.getItemHandler().getStackInSlot(index);
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void putStack(ItemStack stack)
    {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {

    }

    @Override
    public int getSlotStackLimit()
    {
        IItemHandlerContainer ihc = getItemHandlerContainer();
        if (ihc != null)
            return ihc.getInventoryStackLimit(slotNumber, null);
        return 64;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        IItemHandlerContainer ihc = getItemHandlerContainer();
        if (ihc != null)
            return ihc.getInventoryStackLimit(slotNumber, stack);

        ItemStack maxAdd = stack.copy();
        maxAdd.stackSize = maxAdd.getMaxStackSize();

        IItemHandler itemHandler = this.getItemHandler();
        ItemStack currentStack = itemHandler.getStackInSlot(index);
        ItemStack remainder = itemHandler.insertItem(index, maxAdd, true);

        int current = currentStack == null ? 0 : currentStack.stackSize;
        int added = maxAdd.stackSize - (remainder != null ? remainder.stackSize : 0);
        return current + added;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return this.getItemHandler().extractItem(index, 1, true) != null;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        return this.getItemHandler().extractItem(index, amount, false);
    }

    public IItemHandler getItemHandler()
    {
        return itemHandler;
    }

    public IItemHandlerContainer getItemHandlerContainer()
    {
        return itemHandlerContainer;
    }

    @Override
    public boolean isSameInventory(Slot other)
    {
        return other instanceof SlotItemHandler && ((SlotItemHandler) other).getItemHandler() == this.itemHandler;
    }
}