package net.minecraftforge.common;

import net.minecraft.src.ItemStack;

public interface IPotionIngredient
{
    /**
     * Checks whether this ingredient can be brewed into the potion.
     * 
     * @param itemstack
     * @return True if this ingredient can be brewed into the passed ItemStack, false otherwise.
     */
    public boolean canAffectPotion(ItemStack itemstack);
    /**
     * Gets the result of brewing this ingredient into the potion.
     * 
     * @param itemstack
     * @return The ItemStack resulting from brewing this ingredient into the passed ItemStack.
     */
    public ItemStack getBrewingResult(ItemStack itemstack);
}
