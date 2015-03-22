package net.minecraftforge.common.food;

import net.minecraft.item.ItemStack;

/**
 * An interface for edible objects that should add hunger and saturation to the player.
 */
public interface IEdible
{
    boolean isEdible(ItemStack stack);

    FoodValues getFoodValues(ItemStack stack);
}
