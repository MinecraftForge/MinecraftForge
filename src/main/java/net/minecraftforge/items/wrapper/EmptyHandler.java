package net.minecraftforge.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class EmptyHandler implements IItemHandlerModifiable
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

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        // nothing to do here
    }
}
