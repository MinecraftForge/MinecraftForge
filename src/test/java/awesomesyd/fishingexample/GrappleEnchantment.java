package awesomesyd.fishingexample;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class GrappleEnchantment extends Enchantment
{

    public GrappleEnchantment(Rarity rarity, EnchantmentType applicable, EquipmentSlotType... slots)
    {
        super(rarity, applicable, slots);
    }

    @Override
    public int getMinCost(int level)
    {
        return 15;
    }
}