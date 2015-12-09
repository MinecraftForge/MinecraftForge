package net.minecraftforge.items.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class InvWrapper implements IItemHandler {
    IInventory inv;

    public InvWrapper(IInventory inv)
    {
        this.inv = inv;
    }

    public static int getSlots(IInventory inv, EnumFacing side)
    {
        return inv.getSizeInventory();
    }

    public static ItemStack getStackInSlot(IInventory inv, int slot, EnumFacing side)
    {
        return inv.getStackInSlot(slot);
    }

    public static ItemStack insertItem(IInventory inv, int slot, ItemStack insertStack, EnumFacing side, boolean simulate)
    {
        if (insertStack == null)
            return null;

        if (!inv.isItemValidForSlot(slot, insertStack))
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
                    inv.markDirty();
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
                    inv.markDirty();
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
                    inv.markDirty();
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
                {
                    inv.setInventorySlotContents(slot, insertStack);
                    inv.markDirty();
                }
                return null;
            }
        }

    }

    public static ItemStack extractItem(IInventory inv, int slot, int amount, EnumFacing side, boolean simulate)
    {
        if (amount == 0)
            return null;

        ItemStack stackInSlot = inv.getStackInSlot(slot);

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

            ItemStack decrStackSize = inv.decrStackSize(slot, m);
            inv.markDirty();
            return decrStackSize;
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
