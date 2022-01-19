/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.hunger;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.hunger.FoodValues;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Base class for all FoodEvent events.
 *
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class FoodEvent extends Event
{
    private final Player player;

    protected FoodEvent(Player player)
    {
        this.player = player;
    }

    /**
     * @return The player for which this FoodEvent pertains to. Might be null in some cases.
     * @see FoodEvent.GetFoodValues
     */
    public Player getPlayer()
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

        public GetFoodValues(FoodValues originalFoodValues, ItemStack itemstack, @Nullable Player player)
        {
            super(player);
            this.foodValues = originalFoodValues;
            this.originalFoodValues = originalFoodValues;
            this.itemstack = itemstack;
        }

        /**
         * @return The current food values for the given food.
         */
        public FoodValues getFoodValues()
        {
            return foodValues;
        }

        /**
         * @param foodValues The new food values for the given food.
         */
        public void setFoodValues(FoodValues foodValues)
        {
            this.foodValues = foodValues;
        }

        /**
         * @return The food values for the given food before any modifications (the base values).
         */
        public FoodValues getOriginalFoodValues()
        {
            return originalFoodValues;
        }

        /**
         * @return The food ItemStack itself.
         */
        public ItemStack getItemStack()
        {
            return itemstack;
        }
    }

    /**
     * Fired after {@link net.minecraft.world.food.FoodData#eat(Item, ItemStack)}, containing the effects and context for the food that was eaten.
     *
     * This event is not {@link Cancelable}.
     *
     * This event does not have a result. {@link HasResult}
     */
    public static class FoodEaten extends FoodEvent
    {
        private final FoodValues foodValues;
        private final int nutritionAdded;
        private final float saturationAdded;
        private final ItemStack food;

        public FoodEaten(FoodValues foodValues, int nutritionAdded, float saturationAdded, ItemStack food, Player player)
        {
            super(player);
            this.foodValues = foodValues;
            this.nutritionAdded = nutritionAdded;
            this.saturationAdded = saturationAdded;
            this.food = food;
        }

        /**
         * @return The food values of the food eaten.
         */
        public FoodValues getFoodValues()
        {
            return foodValues;
        }

        /**
         * @return The actual amount of hunger/nutrition added.
         */
        public int getNutritionAdded()
        {
            return nutritionAdded;
        }

        /**
         * @return The actual amount of saturation added.
         */
        public float getSaturationAdded()
        {
            return saturationAdded;
        }

        /**
         * @return The food ItemStack itself.
         */
        public ItemStack getItemStack() {
            return food;
        }
    }

    /**
     * Fired when nutrition/saturation is added to a player's FoodData.
     *
     * This event is {@link Cancelable}.
     * If this event is canceled, the nutrition and saturation of the FoodData will not change.
     *
     * This event does not have a result. {@link HasResult}
     */
    @Cancelable
    public static class FoodDataAddition extends FoodEvent
    {
        private final FoodValues foodValues;

        public FoodDataAddition(FoodValues foodValues, Player player)
        {
            super(player);
            this.foodValues = foodValues;
        }

        /**
         * @return The food values of the food being eaten.
         */
        public FoodValues getFoodValues()
        {
            return foodValues;
        }
    }
}
