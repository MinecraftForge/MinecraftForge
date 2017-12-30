package net.minecraftforge.items.itemhandlerobserver;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public interface IItemHandlerObserver
{
    void onStackInserted(IItemHandler handler, int slot, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack);

    void onStackExtracted(IItemHandler handler, int slot, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack);

    void onInvalidated();

    boolean isValid();
}
