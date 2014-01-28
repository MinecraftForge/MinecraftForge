package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IInventory
{
    // JAVADOC METHOD $$ func_70302_i_
    int getSizeInventory();

    // JAVADOC METHOD $$ func_70301_a
    ItemStack getStackInSlot(int var1);

    // JAVADOC METHOD $$ func_70298_a
    ItemStack decrStackSize(int var1, int var2);

    // JAVADOC METHOD $$ func_70304_b
    ItemStack getStackInSlotOnClosing(int var1);

    // JAVADOC METHOD $$ func_70299_a
    void setInventorySlotContents(int var1, ItemStack var2);

    String func_145825_b();

    boolean func_145818_k_();

    // JAVADOC METHOD $$ func_70297_j_
    int getInventoryStackLimit();

    // JAVADOC METHOD $$ func_70296_d
    void onInventoryChanged();

    // JAVADOC METHOD $$ func_70300_a
    boolean isUseableByPlayer(EntityPlayer var1);

    void openChest();

    void closeChest();

    // JAVADOC METHOD $$ func_94041_b
    boolean isItemValidForSlot(int var1, ItemStack var2);
}