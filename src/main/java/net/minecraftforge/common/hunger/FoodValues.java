/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.hunger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;

/**
 * FoodValues is a utility class used to retrieve and hold food values.
 *
 * To get food values for any given food, use any of the static {@link #get} methods.
 *
 * <pre>
 * {@code
 * FoodValues appleFoodValues = FoodValues.get(new ItemStack(Items.APPLE));
 * }
 * </pre>
 */
public class FoodValues
{
    private final int hunger;
    private final float saturationModifier;

    public FoodValues(int hunger, float saturationModifier)
    {
        this.hunger = hunger;
        this.saturationModifier = saturationModifier;
    }

    public FoodValues(FoodValues other)
    {
        this(other.hunger, other.saturationModifier);
    }

    public int getHunger()
    {
        return hunger;
    }

    public float getSaturationModifier()
    {
        return saturationModifier;
    }

    /**
     * @return The amount of saturation that the food values would provide, ignoring any limits.
     */
    public float getUnboundedSaturationIncrement()
    {
        return hunger * saturationModifier * 2f;
    }

    /**
     * @return The bounded amount of saturation that the food values would provide to this player,
     * taking their max hunger level into account.
     */
    public float getSaturationIncrement(PlayerEntity player)
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
                Food food = stack.getItem().getFoodProperties();
                return new FoodValues(food.getNutrition(), food.getSaturationModifier());
            }
            else if (stack.getItem() instanceof IEdible)
            {
                return ((IEdible)stack.getItem()).getFoodValues(stack);
            }
            else if (stack.getItem() instanceof BlockItem)
            {
                BlockItem blockItem = (BlockItem)stack.getItem();
                if (blockItem.getBlock() instanceof IEdible)
                {
                    return ((IEdible)blockItem.getBlock()).getFoodValues(stack);
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
    public static FoodValues get(@Nonnull ItemStack stack, PlayerEntity player)
    {
        FoodValues foodValues = getUnmodified(stack);
        if (foodValues != null)
        {
            return ForgeEventFactory.getFoodValues(foodValues, stack, player);
        }
        return null;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + hunger;
        result = prime * result + Float.floatToIntBits(saturationModifier);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FoodValues other = (FoodValues) obj;
        if (hunger != other.hunger)
            return false;
        if (Float.floatToIntBits(saturationModifier) != Float.floatToIntBits(other.saturationModifier))
            return false;
        return true;
    }
}
