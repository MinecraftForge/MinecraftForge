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

package net.minecraftforge.items.wrapper;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SidedInvWrapper implements IItemHandlerModifiable
{
    protected final ISidedInventory inv;
    protected final EnumFacing side;

    public SidedInvWrapper(ISidedInventory inv, EnumFacing side)
    {
        this.inv = inv;
        this.side = side;
    }

    public static int getSlot(ISidedInventory inv, int slot, EnumFacing side)
    {
        int[] slots = inv.getSlotsForFace(side);
        if (slot < slots.length)
            return slots[slot];
        return -1;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SidedInvWrapper that = (SidedInvWrapper) o;

        return inv.equals(that.inv) && side == that.side;
    }

    @Override
    public int hashCode()
    {
        int result = inv.hashCode();
        result = 31 * result + side.hashCode();
        return result;
    }

    @Override
    public int getSlots()
    {
        return inv.getSlotsForFace(side).length;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        int i = getSlot(inv, slot, side);
        return i == -1 ? ItemStack.EMPTY : inv.getStackInSlot(i);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return stack;

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        int m;
        if (!stackInSlot.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!inv.isItemValidForSlot(slot1, stack) || !inv.canInsertItem(slot1, stack, side))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    inv.setInventorySlotContents(slot1, copy);
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.splitStack(m);
                    copy.grow(stackInSlot.getCount());
                    inv.setInventorySlotContents(slot1, copy);
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
        }
        else
        {
            if (!inv.isItemValidForSlot(slot1, stack) || !inv.canInsertItem(slot1, stack, side))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount())
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    inv.setInventorySlotContents(slot1, stack.splitStack(m));
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
            else
            {
                if (!simulate)
                    inv.setInventorySlotContents(slot1, stack);
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        int slot1 = getSlot(inv, slot, side);

        if (slot1 != -1)
            inv.setInventorySlotContents(slot1, stack);
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (!inv.canExtractItem(slot1, stackInSlot, side))
            return ItemStack.EMPTY;

        if (simulate)
        {
            if (stackInSlot.getCount() < amount)
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        }
        else
        {
            int m = Math.min(stackInSlot.getCount(), amount);
            return inv.decrStackSize(slot1, m);
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return inv.getInventoryStackLimit();
    }
}
