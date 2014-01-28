package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem
{
    IBehaviorDispenseItem itemDispenseBehaviorProvider = new IBehaviorDispenseItem()
    {
        private static final String __OBFID = "CL_00001200";
        // JAVADOC METHOD $$ func_82482_a
        public ItemStack dispense(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
        {
            return par2ItemStack;
        }
    };

    // JAVADOC METHOD $$ func_82482_a
    ItemStack dispense(IBlockSource var1, ItemStack var2);
}