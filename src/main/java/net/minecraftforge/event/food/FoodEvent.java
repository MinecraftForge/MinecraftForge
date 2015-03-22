package net.minecraftforge.event.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.food.FoodValues;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for all FoodEvent events.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class FoodEvent extends Event
{
    /**
     * Fired every time food values are retrieved to allow player-independent control over their values.
     *
     * This event is fired in {@link FoodStats#addStats(ItemFood, ItemStack)}.<br>
     * <br>
     * {@link #foodValues} contains the values of the {@link #food}.<br>
     * {@link #unmodifiedFoodValues} contains the food values of the {@link #food} before the GetFoodValues event was fired.<br>
     * {@link #food} contains the food in question.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class GetFoodValues extends FoodEvent
    {
        public FoodValues foodValues;
        public final FoodValues unmodifiedFoodValues;
        public final ItemStack food;

        public GetFoodValues(ItemStack itemStack, FoodValues foodValues)
        {
            this.food = itemStack;
            this.foodValues = foodValues;
            this.unmodifiedFoodValues = foodValues;
        }
    }

    /**
     * Fired every time food values are retrieved to allow player-dependent control over their values.
     * This event will always be preceded by {@link GetFoodValues} being fired.
     *
     * This event is fired in {@link FoodStats#addStats(ItemFood, ItemStack)}.<br>
     * <br>
     * {@link #player} contains the player.<br>
     * {@link #foodValues} contains the values of the {@link #food}.<br>
     * {@link #unmodifiedFoodValues} contains the food values of the {@link #food} before the GetFoodValues event was fired.<br>
     * {@link #food} contains the food in question.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class GetPlayerFoodValues extends FoodEvent
    {
        public FoodValues foodValues;
        public final FoodValues unmodifiedFoodValues;
        public final ItemStack food;
        public final EntityPlayer player;

        public GetPlayerFoodValues(EntityPlayer player, ItemStack itemStack, FoodValues foodValues)
        {
            this.player = player;
            this.food = itemStack;
            this.foodValues = foodValues;
            this.unmodifiedFoodValues = foodValues;
        }
    }

    /**
     * Fired after {@link FoodStats#addStats}, containing the effects and context for the food that was eaten.
     *
     * This event is fired in {@link FoodStats#addStats(ItemFood, ItemStack)}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class FoodEaten extends FoodEvent
    {
        public final FoodValues foodValues;
        public final int hungerAdded;
        public final float saturationAdded;
        public final ItemStack food;
        public final EntityPlayer player;

        public FoodEaten(EntityPlayer player, ItemStack itemStack, FoodValues foodValues, int hungerAdded, float saturationAdded)
        {
            this.player = player;
            this.food = itemStack;
            this.foodValues = foodValues;
            this.hungerAdded = hungerAdded;
            this.saturationAdded = saturationAdded;
        }
    }

    /**
     * Fired when hunger/saturation is added to a player's FoodStats.
     *
     * This event is fired in {@link FoodStats#addStats(int, float)}.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the hunger and saturation of the FoodStats will not change.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    @Cancelable
    public static class FoodStatsAddition extends FoodEvent
    {
        public final FoodValues foodValuesToBeAdded;
        public final EntityPlayer player;

        public FoodStatsAddition(EntityPlayer player, FoodValues foodValuesToBeAdded)
        {
            this.player = player;
            this.foodValuesToBeAdded = foodValuesToBeAdded;
        }
    }
}
