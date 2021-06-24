package awesomesyd.fishingexample;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class YankEnchantment extends Enchantment
{

    public YankEnchantment(Rarity rarity, EnchantmentType applicable, EquipmentSlotType... slots)
    {
        super(rarity, applicable, slots);
    }

    @Override
    public int getMinCost(int level)
    {
        return 1;
    }

    @Override
    public int getMaxCost(int level)
    {
        return 30;
    }
}