package net.minecraftforge.items;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.IntPredicate;

public record SidedInventoryItemHandler(IItemHandlerModifiable handler, IntPredicate extract, BiPredicate<Integer, ItemStack> insert) implements IItemHandlerModifiable
{
    @Override
    public int getSlots()
    {
        return handler.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        return handler.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return insert.test(slot, stack) ? handler.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return extract.test(slot) ? handler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return insert.test(slot, stack) && handler.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack)
    {
        handler.setStackInSlot(slot, stack);
    }
}
