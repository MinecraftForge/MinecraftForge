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
        return i == -1 ? ItemStack.field_190927_a : inv.getStackInSlot(i);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.func_190926_b())
            return ItemStack.field_190927_a;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return stack;

        if (!inv.isItemValidForSlot(slot1, stack) || !inv.canInsertItem(slot1, stack, side))
            return stack;

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        int m;
        if (!stackInSlot.func_190926_b())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit()) - stackInSlot.func_190916_E();

            if (stack.func_190916_E() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.func_190917_f(stackInSlot.func_190916_E());
                    inv.setInventorySlotContents(slot1, copy);
                }

                return ItemStack.field_190927_a;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.splitStack(m);
                    copy.func_190917_f(stackInSlot.func_190916_E());
                    inv.setInventorySlotContents(slot1, copy);
                    return stack;
                }
                else
                {
                    stack.func_190918_g(m);
                    return stack;
                }
            }
        }
        else
        {
            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
            if (m < stack.func_190916_E())
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
                    stack.func_190918_g(m);
                    return stack;
                }
            }
            else
            {
                if (!simulate)
                    inv.setInventorySlotContents(slot1, stack);
                return ItemStack.field_190927_a;
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
            return ItemStack.field_190927_a;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return ItemStack.field_190927_a;

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        if (stackInSlot.func_190926_b())
            return ItemStack.field_190927_a;

        if (!inv.canExtractItem(slot1, stackInSlot, side))
            return ItemStack.field_190927_a;

        if (simulate)
        {
            if (stackInSlot.func_190916_E() < amount)
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.func_190920_e(amount);
                return copy;
            }
        }
        else
        {
            int m = Math.min(stackInSlot.func_190916_E(), amount);
            return inv.decrStackSize(slot1, m);
        }
    }
}
