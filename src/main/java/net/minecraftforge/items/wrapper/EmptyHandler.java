package net.minecraftforge.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public class EmptyHandler implements IItemHandler {
    public static IItemHandler instance = new EmptyHandler();

    @Override
    public int getSlots(EnumFacing side)
    {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot, EnumFacing side)
    {
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, EnumFacing side, boolean simulate)
    {
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, EnumFacing side, boolean simulate)
    {
        return null;
    }
}
