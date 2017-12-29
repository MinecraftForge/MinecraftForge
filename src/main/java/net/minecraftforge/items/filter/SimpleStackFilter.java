package net.minecraftforge.items.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

public class SimpleStackFilter implements IStackFilter
{
    private final ItemStack toTest;

    public SimpleStackFilter(ItemStack toTest)
    {
        this.toTest = toTest;
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return ItemHandlerHelper.canItemStacksStack(toTest, stack);
    }

    @Override
    public NonNullList<ItemStack> getExamples()
    {
        return NonNullList.from(ItemStack.EMPTY, toTest);
    }
}
