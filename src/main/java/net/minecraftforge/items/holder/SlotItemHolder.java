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

package net.minecraftforge.items.holder;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SlotItemHolder extends Slot
{
    private final IItemHolder itemHolder;

    public SlotItemHolder(IItemHolder itemHolder, int index, int xPosition, int yPosition)
    {
        super(ItemHandlerHelper.emptyInventory, index, xPosition, yPosition);
        this.itemHolder = itemHolder;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return itemHolder.isStackValidForSlot(stack, getSlotIndex());
    }

    @Nonnull
    @Override
    public ItemStack getStack()
    {
        return itemHolder.getStack(getSlotIndex());
    }

    @Override
    public void putStack(@Nonnull ItemStack stack)
    {
        itemHolder.putStack(getSlotIndex(), stack, false);
    }

    @Override
    public int getSlotStackLimit()
    {
        return super.getSlotStackLimit();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount)
    {
        return itemHolder.decreaseStack(getSlotIndex(), amount, false);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn)
    {
        return false;
    }

    public IItemHolder getItemHolder()
    {
        return itemHolder;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return itemHolder.canExtractStackFromSlot(getStack(), getSlotIndex());
    }
}
