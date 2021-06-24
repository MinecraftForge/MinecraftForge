package awesomesyd.fishingexample;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class YoinkEnchantment extends Enchantment {

    public YoinkEnchantment(Rarity rarity, EnchantmentType applicable, EquipmentSlotType... slots) {
		super(rarity, applicable, slots);
	}

	@Override
	public int getMinCost(int level) {
		return 8*level-5;
	}

    @Override
    public int getMaxLevel(){
        return 4;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level+1);
    }
}