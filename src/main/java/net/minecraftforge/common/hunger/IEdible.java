/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.hunger;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Interface to be applied to non-standard food items or blocks that are edible, in order to obtain their food values.
 * For example, Forge implements this interface on {@link net.minecraft.world.level.block.CakeBlock}.
 * It is recommended to add a {@link net.minecraft.world.food.FoodProperties} object to your item and not use this interface.
 */
public interface IEdible
{
    /**
     * Obtain the FoodValues for this object
     * @param stack The ItemStack containing this edible object
     * @return The FoodValues for the object.
     */
    FoodValues getFoodValues(@Nonnull ItemStack stack);

    /**
     * @return true if this edible object can be eaten even when the player's hunger is full
     * @see net.minecraft.world.food.FoodProperties#canAlwaysEat()
     */
    boolean canAlwaysEat();
}
