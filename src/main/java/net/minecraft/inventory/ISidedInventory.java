package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

public interface ISidedInventory extends IInventory
{
    // JAVADOC METHOD $$ func_94128_d
    int[] getAccessibleSlotsFromSide(int var1);

    // JAVADOC METHOD $$ func_102007_a
    boolean canInsertItem(int var1, ItemStack var2, int var3);

    // JAVADOC METHOD $$ func_102008_b
    boolean canExtractItem(int var1, ItemStack var2, int var3);
}