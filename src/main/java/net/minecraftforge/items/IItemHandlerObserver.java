package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemHandlerObserver
{
    void onStackInserted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot);

    void onStackExtracted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot);
}
