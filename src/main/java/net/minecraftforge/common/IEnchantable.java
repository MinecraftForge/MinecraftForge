package net.minecraftforge.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

/**
*
* This allows for mods to create items that can accept 
* enchants they would not otherwise be able too
* e.g. a tool descended from ItemTool that can have
* Sharpness and Smite
*
*/
public interface IEnchantable 
{
    /**
     * Checks if the object can accept the enchant
     *
     * @param stack The ItemStack that is being used, Possible to be null
     * @param enchant the enchant in question
     * @return If this enchant can go on this item, true
     */
    public boolean canApply( ItemStack stack, Enchantment enchant);
}
