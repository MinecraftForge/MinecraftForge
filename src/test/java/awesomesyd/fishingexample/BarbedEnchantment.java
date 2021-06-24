package awesomesyd.fishingexample;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class BarbedEnchantment extends Enchantment
{
    public BarbedEnchantment(Rarity rarity, EnchantmentType type, EquipmentSlotType... slots)
    {
        super(rarity, type, slots);
    }

    @Override
    public int getMaxLevel()
    {
        return 4;
    }

    @Override
    public int getMinCost(int level)
    {
        return 1 + 8 * (level - 1);
    }

    @Override
    public int getMaxCost(int level)
    {
        return getMinCost(level + 1);
    }
}