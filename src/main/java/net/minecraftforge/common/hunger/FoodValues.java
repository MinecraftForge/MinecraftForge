/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.hunger;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FoodValues is a record used to retrieve and hold food values.
 *
 * To get food values for any given food, use any of the static {@link #get} methods.
 *
 * <pre>
 * {@code
 * FoodValues appleFoodValues = FoodValues.get(new ItemStack(Items.APPLE));
 * }
 * </pre>
 */
public record FoodValues(int nutrition, float saturationModifier)
{
    /**
     * @return The amount of saturation that the food values would provide, ignoring any limits.
     */
    public float getUnboundedSaturationIncrement()
    {
        return nutrition * saturationModifier * 2f;
    }

    /**
     * @return The bounded amount of saturation that the food values would provide to this player,
     * taking their max hunger level into account.
     */
    public float getSaturationIncrement(Player player)
    {
        return Math.min(ForgeEventFactory.getMaxHunger(player), getUnboundedSaturationIncrement());
    }

    /**
     * Get unmodified (vanilla) food values.
     *
     * @return The food values, or null if none were found.
     */
    public static FoodValues getUnmodified(@Nonnull ItemStack stack)
    {
        if (stack != ItemStack.EMPTY)
        {
            if (stack.isEdible())
            {
                FoodProperties food = stack.getItem().getFoodProperties();
                return new FoodValues(food.getNutrition(), food.getSaturationModifier());
            }
            else if (stack.getItem() instanceof IEdible edible)
            {
                return edible.getFoodValues(stack);
            }
            else if (stack.getItem() instanceof BlockItem blockItem)
            {
                if (blockItem.getBlock() instanceof IEdible edible)
                {
                    return edible.getFoodValues(stack);
                }
            }
        }

        return null;
    }

    /**
     * Get player-agnostic food values.
     *
     * @return The food values, or null if none were found.
     */
    public static FoodValues get(@Nonnull ItemStack stack)
    {
        return get(stack, null);
    }

    /**
     * Get player-specific food values.
     *
     * @return The food values, or null if none were found.
     */
    public static FoodValues get(@Nonnull ItemStack stack, @Nullable Player player)
    {
        FoodValues foodValues = getUnmodified(stack);
        if (foodValues != null)
        {
            return ForgeEventFactory.getFoodValues(foodValues, stack, player);
        }
        return null;
    }
}
