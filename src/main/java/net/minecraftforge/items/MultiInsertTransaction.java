package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class MultiInsertTransaction
{
    private final NonNullList<ItemStack> insertedStacks = NonNullList.create();

    private final NonNullList<ItemStack> leftOverStacks = NonNullList.create();

    public void addTransaction(InsertTransaction transaction)
    {
        insertedStacks.add(transaction.getInsertedStack());
        leftOverStacks.add(transaction.getLeftoverStack());

    }

    public void addLeftOverItemStack(ItemStack stack)
    {
        leftOverStacks.add(stack);
    }

    public void addInsertedItemStack(ItemStack stack)
    {
        insertedStacks.add(stack);
    }

    public NonNullList<ItemStack> getInsertedStacks()
    {
        return insertedStacks;
    }

    public NonNullList<ItemStack> getLeftOverStacks()
    {
        return leftOverStacks;
    }
}
