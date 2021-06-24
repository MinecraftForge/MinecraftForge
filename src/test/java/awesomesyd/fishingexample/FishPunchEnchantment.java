package awesomesyd.fishingexample;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class FishPunchEnchantment extends Enchantment
{
    public FishPunchEnchantment(Rarity rarity, EnchantmentType applicable, EquipmentSlotType... slots) {
        super(rarity, applicable, slots);
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
    
    @Override
    public int getMinCost(int level) {
        return 8*level;
    }
}