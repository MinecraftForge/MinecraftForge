package net.minecraftforge.items.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class InvWrapper implements IItemHandlerModifiable
{
    public final IInventory inv;

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

        return inv.equals(that.inv);

    }

    @Override
    public int hashCode()
    {
        return inv.hashCode();
    }

    @Override
    public int getSlots()
    {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack == null)
            return null;

        if (!inv.isItemValidForSlot(slot, stack))
            return stack;

        ItemStack stackInSlot = inv.getStackInSlot(slot);

        int m;
        if (stackInSlot != null)
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit()) - stackInSlot.stackSize;

            if (stack.stackSize <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                    inv.markDirty();
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
                    inv.setInventorySlotContents(slot, copy);
                    inv.markDirty();
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
            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
            if (m < stack.stackSize)
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    inv.setInventorySlotContents(slot, stack.splitStack(m));
                    inv.markDirty();
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
                    inv.setInventorySlotContents(slot, stack);
                    inv.markDirty();
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
    public void setStackInSlot(int slot, ItemStack stack)
    {
        inv.setInventorySlotContents(slot, stack);
    }
}
