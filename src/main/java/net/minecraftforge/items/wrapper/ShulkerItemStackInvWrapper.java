package net.minecraftforge.items.wrapper;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ShulkerItemStackInvWrapper implements IItemHandler
{
    private final int size;
    private final ItemStack stack;
    private final Predicate<ItemStack> canHold;

    public ShulkerItemStackInvWrapper(ItemStack stack, int size, Predicate<ItemStack> canHold)
    {
        this.size = size;
        this.stack = stack;
        this.canHold = canHold;
    }

    @Override
    public int getSlots()
    {
        return this.size;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        if (slot < getSlots()) {
            return getItemList().get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = itemStacks.get(slot);

        int m;
        if (!stackInSlot.isEmpty())
        {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
                return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!this.isItemValid(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    itemStacks.set(slot, copy);
                    this.setItemList(itemStacks);
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    itemStacks.set(slot, copy);
                    this.setItemList(itemStacks);
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
            if (!this.isItemValid(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount())
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    itemStacks.set(slot, stack.split(m));
                    this.setItemList(itemStacks);
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
                {
                    itemStacks.set(slot, stack);
                    this.setItemList(itemStacks);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = itemStacks.get(slot);

        if (stackInSlot.isEmpty())
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

            ItemStack decrStackSize = ContainerHelper.removeItem(itemStacks, slot, m);
            this.setItemList(itemStacks);
            return decrStackSize;
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return canHold.test(stack);
    }

    public NonNullList<ItemStack> getItemList()
    {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
        CompoundTag rootTag = this.stack.getTag();
        if (rootTag != null && rootTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(rootTag, itemStacks);
        }
        return itemStacks;
    }

    public void setItemList(NonNullList<ItemStack> itemStacks)
    {
        ContainerHelper.saveAllItems(this.stack.getOrCreateTag(), itemStacks);
    }
}
