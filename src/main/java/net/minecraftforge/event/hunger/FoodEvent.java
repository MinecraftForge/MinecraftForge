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

package net.minecraftforge.event.hunger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.hunger.FoodValues;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all FoodEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class FoodEvent extends Event
{
    private final PlayerEntity player;

    protected FoodEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * Fired every time food values are retrieved to allow control over their values.
     * For this event, player can be null, which would indicate that the values should be player-independent.
     *
     * {@link #foodValues} contains the food values of the food associated with the {@link #itemstack}.
     * {@link #originalFoodValues} contains the food values of the food associated with the {@link #itemstack} before the GetFoodValues event was fired.
     * {@link #itemstack} contains the food in question.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class GetFoodValues extends FoodEvent
    {
        private FoodValues foodValues;
        private final FoodValues originalFoodValues;
        private final ItemStack itemstack;

        public GetFoodValues(FoodValues originalFoodValues, ItemStack itemstack, PlayerEntity player)
        {
            super(player);
            this.foodValues = originalFoodValues;
            this.originalFoodValues = originalFoodValues;
            this.itemstack = itemstack;
        }

        public FoodValues getFoodValues()
        {
            return foodValues;
        }

        public void setFoodValues(FoodValues foodValues)
        {
            this.foodValues = foodValues;
        }

        public FoodValues getOriginalFoodValues()
        {
            return originalFoodValues;
        }

        public ItemStack getItemStack()
        {
            return itemstack;
        }
    }

    /**
     * Fired after {@link net.minecraft.util.FoodStats#eat(Item, ItemStack)}, containing the effects and context for the food that was eaten.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class FoodEaten extends FoodEvent
    {
        private final FoodValues foodValues;
        private final int hungerAdded;
        private final float saturationAdded;
        private final ItemStack food;

        public FoodEaten(FoodValues foodValues, int hungerAdded, float saturationAdded, ItemStack food, PlayerEntity player)
        {
            super(player);
            this.foodValues = foodValues;
            this.hungerAdded = hungerAdded;
            this.saturationAdded = saturationAdded;
            this.food = food;
        }

        public FoodValues getFoodValues()
        {
            return foodValues;
        }

        public int getHungerAdded()
        {
            return hungerAdded;
        }

        public float getSaturationAdded()
        {
            return saturationAdded;
        }

        public ItemStack getItemStack() {
            return food;
        }
    }

    /**
     * Fired when hunger/saturation is added to a player's FoodStats.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, the hunger and saturation of the FoodStats will not change.
     *
     * This event does not have a result. {@link HasResult}
     */
    @Cancelable
    public static class FoodStatsAddition extends FoodEvent
    {
        private final FoodValues foodValues;

        public FoodStatsAddition(FoodValues foodValues, PlayerEntity player)
        {
            super(player);
            this.foodValues = foodValues;
        }

        public FoodValues getFoodValues()
        {
            return foodValues;
        }
    }
}
