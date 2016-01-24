package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemHandlerHelper
{
    public static ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate)
    {
        if (dest == null || stack == null)
            return stack;

        for (int i = 0; i < dest.getSlots(); i++)
        {
            stack = dest.insertItem(i, stack, simulate);
            if (stack == null || stack.stackSize <= 0)
            {
                return null;
            }
        }

        return stack;
    }

    public static boolean canItemStacksStack(ItemStack a, ItemStack b)
    {
        if (a == null || !a.isItemEqual(b))
            return false;

        final NBTTagCompound aTag = a.getTagCompound();
        final NBTTagCompound bTag = b.getTagCompound();
        return (aTag != null || bTag == null) && (aTag == null || aTag.equals(bTag));
    }

    public static ItemStack copyStackWithSize(ItemStack itemStack, int size)
    {
        if (size == 0)
            return null;
        ItemStack copy = ItemStack.copyItemStack(itemStack);
        copy.stackSize = size;
        return copy;
    }
}
