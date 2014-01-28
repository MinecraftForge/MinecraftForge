package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType
{
    all,
    armor,
    armor_feet,
    armor_legs,
    armor_torso,
    armor_head,
    weapon,
    digger,
    fishing_rod,
    breakable,
    bow;

    private static final String __OBFID = "CL_00000106";

    // JAVADOC METHOD $$ func_77557_a
    public boolean canEnchantItem(Item par1Item)
    {
        if (this == all)
        {
            return true;
        }
        else if (this == breakable && par1Item.isDamageable())
        {
            return true;
        }
        else if (par1Item instanceof ItemArmor)
        {
            if (this == armor)
            {
                return true;
            }
            else
            {
                ItemArmor itemarmor = (ItemArmor)par1Item;
                return itemarmor.armorType == 0 ? this == armor_head : (itemarmor.armorType == 2 ? this == armor_legs : (itemarmor.armorType == 1 ? this == armor_torso : (itemarmor.armorType == 3 ? this == armor_feet : false)));
            }
        }
        else
        {
            return par1Item instanceof ItemSword ? this == weapon : (par1Item instanceof ItemTool ? this == digger : (par1Item instanceof ItemBow ? this == bow : (par1Item instanceof ItemFishingRod ? this == fishing_rod : false)));
        }
    }
}