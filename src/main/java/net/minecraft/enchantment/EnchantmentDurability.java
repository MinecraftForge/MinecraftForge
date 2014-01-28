package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class EnchantmentDurability extends Enchantment
{
    private static final String __OBFID = "CL_00000103";

    protected EnchantmentDurability(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.breakable);
        this.setName("durability");
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return 5 + (par1 - 1) * 8;
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 3;
    }

    public boolean canApply(ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemStackDamageable() ? true : super.canApply(par1ItemStack);
    }

    // JAVADOC METHOD $$ func_92097_a
    public static boolean negateDamage(ItemStack par0ItemStack, int par1, Random par2Random)
    {
        return par0ItemStack.getItem() instanceof ItemArmor && par2Random.nextFloat() < 0.6F ? false : par2Random.nextInt(par1 + 1) > 0;
    }
}