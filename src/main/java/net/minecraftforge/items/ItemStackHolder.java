package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ItemStackHolder {
    final int size;
    ItemStack[] stacks;

    public ItemStackHolder(int size)
    {
        this.size = size;
        stacks = new ItemStack[size];
    }

    public int getSlots()
    {
        return size;
    }

    public ItemStack getStackInSlot(int slot)
    {
        return stacks[slot];
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack == null)
            return null;
        ItemStack existing = stacks[slot];

        int limit = getStackLimit(slot, stack);

        if (existing != null)
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.stackSize;
        }

        if (limit == 0)
            return stack;

        boolean reachedLimit = stack.stackSize > limit;

        if (!simulate)
        {
            if (existing == null)
            {
                stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : stack;
            }
            else
            {
                existing.stackSize += reachedLimit ? limit : stack.stackSize;
            }
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : null;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return null;

        ItemStack existing = stacks[slot];

        if (existing == null)
            return null;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.stackSize <= toExtract)
        {
            if (!simulate)
                stacks[slot] = null;
            return existing;
        }
        else
        {
            if (!simulate)
                stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract);

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);

        }
    }

    public int getStackLimit(int slot, ItemStack stack)
    {
        return stack.getMaxStackSize();
    }

    public void readFromNBT(NBTTagCompound tags)
    {
        stacks = new ItemStack[size];
        NBTTagList nbtTagList = tags.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtTagList.tagCount(); i++)
        {
            NBTTagCompound itemTags = nbtTagList.getCompoundTagAt(i);
            int j = size < 256 ? itemTags.getByte("Slot") & 255 : itemTags.getInteger("Slot");

            if (j > 0 && j < stacks.length)
            {
                stacks[j] = ItemStack.loadItemStackFromNBT(itemTags);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tags)
    {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < stacks.length; i++)
        {
            if (stacks[i] != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                if (size < 256)
                    itemTag.setByte("Slot", (byte) i);
                else
                    itemTag.setInteger("Slot", i);
                stacks[i].writeToNBT(itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }

        tags.setTag("Items", nbtTagList);
    }
}
