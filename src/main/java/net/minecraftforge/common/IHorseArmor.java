package net.minecraftforge.common;

import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.ItemStack;

/**
 * When implemented by an item, it will be allowed to function as a piece of
 * horse armor.
 */
public interface IHorseArmor 
{
    /**
     * Provides the HorseArmorType to use for this armor. Used to change the
     * protection value provided.
     * 
     * @param stack The ItemStack instance of the armor.
     * @return HorseArmorType The HorseArmorType to use for this armor.
     */
    HorseArmorType getArmorType(ItemStack stack);
}
