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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class InvWrapper implements IItemHandlerModifiable
{
    private final IInventory inv;

    public InvWrapper(IInventory inv)
    {
        this.inv = inv;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InvWrapper that = (InvWrapper) o;

        return getInv().equals(that.getInv());

    }

    @Override
    public int hashCode()
    {
        return getInv().hashCode();
    }

    @Override
    public int getSlots()
    {
        return getInv().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getInv().getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack == null)
            return null;

        if (!getInv().isItemValidForSlot(slot, stack))
            return stack;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        int m;
        if (stackInSlot != null)
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getInv().getInventoryStackLimit()) - stackInSlot.stackSize;

            if (stack.stackSize <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.stackSize += stackInSlot.stackSize;
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                }

                return null;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.splitStack(m);
                    copy.stackSize += stackInSlot.stackSize;
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                    return stack;
                }
                else
                {
                    stack.stackSize -= m;
                    return stack;
                }
            }
        }
        else
        {
            m = Math.min(stack.getMaxStackSize(), getInv().getInventoryStackLimit());
            if (m < stack.stackSize)
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    getInv().setInventorySlotContents(slot, stack.splitStack(m));
                    getInv().markDirty();
                    return stack;
                }
                else
                {
                    stack.stackSize -= m;
                    return stack;
                }
            }
            else
            {
                if (!simulate)
                {
                    getInv().setInventorySlotContents(slot, stack);
                    getInv().markDirty();
                }
                return null;
            }
        }

    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return null;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        if (stackInSlot == null)
            return null;

        if (simulate)
        {
            if (stackInSlot.stackSize < amount)
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.stackSize = amount;
                return copy;
            }
        }
        else
        {
            int m = Math.min(stackInSlot.stackSize, amount);

            ItemStack decrStackSize = getInv().decrStackSize(slot, m);
            getInv().markDirty();
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        getInv().setInventorySlotContents(slot, stack);
    }

    public IInventory getInv()
    {
        return inv;
    }
}
