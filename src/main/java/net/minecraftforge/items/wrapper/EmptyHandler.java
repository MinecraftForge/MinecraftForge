package net.minecraftforge.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class EmptyHandler implements IItemHandler
{
    public static IItemHandler INSTANCE = new EmptyHandler();

    @Override
    public int getSlots()
    {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return null;
    }
}
