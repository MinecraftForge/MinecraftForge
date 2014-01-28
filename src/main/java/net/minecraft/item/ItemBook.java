package net.minecraft.item;

public class ItemBook extends Item
{
    private static final String __OBFID = "CL_00001775";

    // JAVADOC METHOD $$ func_77616_k
    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return par1ItemStack.stackSize == 1;
    }

    // JAVADOC METHOD $$ func_77619_b
    public int getItemEnchantability()
    {
        return 1;
    }
}