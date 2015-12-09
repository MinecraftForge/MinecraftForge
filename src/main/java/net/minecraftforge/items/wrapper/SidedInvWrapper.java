package net.minecraftforge.items.wrapper;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SidedInvWrapper implements IItemHandler {
    ISidedInventory inv;

    public SidedInvWrapper(ISidedInventory inv)
    {
        this.inv = inv;
    }

    public static int getSlots(ISidedInventory inv, EnumFacing side)
    {
        return inv.getSlotsForFace(side).length;
    }

    public static int getSlot(ISidedInventory inv, int slot, EnumFacing side)
    {
        int[] slots = inv.getSlotsForFace(side);
        if (slot < slots.length)
            return slots[slot];
        return -1;
    }

    public static ItemStack getStackInSlot(ISidedInventory inv, int slot, EnumFacing side)
    {
        int i = getSlot(inv, slot, side);
        return i == -1 ? null : inv.getStackInSlot(i);
    }

    public static ItemStack insertItem(ISidedInventory inv, int s, ItemStack insertStack, EnumFacing side, boolean simulate)
    {

        if (insertStack == null)
            return null;

        int slot = getSlot(inv, s, side);

        if (slot == -1)
            return insertStack;

        if (!inv.isItemValidForSlot(slot, insertStack) || !inv.canInsertItem(slot, insertStack, side))
            return insertStack;

        ItemStack stackInSlot = inv.getStackInSlot(slot);

        int m;
        if (stackInSlot != null)
        {
            if (!ItemHandlerHelper.canItemStacksStack(insertStack, stackInSlot))
                return insertStack;

            m = Math.min(insertStack.getMaxStackSize(), inv.getInventoryStackLimit()) - stackInSlot.stackSize;

            if (insertStack.stackSize <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = insertStack.copy();
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                }

                return null;
            }
            else
            {
                if (!simulate)
                {
                    ItemStack copy = insertStack.splitStack(m);
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                    return insertStack;
                }
                else
                {
                    insertStack.stackSize -= m;
                    return insertStack;
                }
            }
        }
        else
        {
            m = Math.min(insertStack.getMaxStackSize(), inv.getInventoryStackLimit());
            if (m < insertStack.stackSize)
            {
                if (!simulate)
                {
                    inv.setInventorySlotContents(slot, insertStack.splitStack(m));
                    return insertStack;
                }
                else
                {
                    insertStack.stackSize -= m;
                    return insertStack;
                }
            }
            else
            {
                if (!simulate)
                    inv.setInventorySlotContents(slot, insertStack);
                return null;
            }
        }

    }

    public static ItemStack extractItem(ISidedInventory inv, int s, int amount, EnumFacing side, boolean simulate)
    {
        if (amount == 0)
            return null;

        int slot = getSlot(inv, s, side);

        if (slot == -1)
            return null;

        ItemStack stackInSlot = inv.getStackInSlot(slot);

        if (stackInSlot == null)
            return null;

        if (!inv.canExtractItem(slot, stackInSlot, side))
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
            return inv.decrStackSize(slot, m);
        }
    }

    @Override
    public int getSlots(EnumFacing side)
    {
        return getSlots(inv, side);
    }

    @Override
    public ItemStack getStackInSlot(int slot, EnumFacing side)
    {
        return getStackInSlot(inv, slot, side);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, EnumFacing side, boolean simulate)
    {
        return insertItem(inv, slot, stack, side, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, EnumFacing side, boolean simulate)
    {
        return extractItem(inv, slot, amount, side, simulate);
    }
}
