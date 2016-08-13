package net.minecraftforge.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    /**
     * Provides the texture to use for the armor. The resulting string must
     * follow standard resource location format.
     * 
     * @param wearer The entity that is wearing the armor.
     * @param stack The ItemStack instance of the armor.
     * @return A string which points to the texture to use for this armor. Must
     *         follow the standard resource location format. Example:
     *         modid:textures/entity/horse/texture_name.png
     */
    @SideOnly(Side.CLIENT)
    String getArmorTexture(EntityLivingBase wearer, ItemStack stack);
}
