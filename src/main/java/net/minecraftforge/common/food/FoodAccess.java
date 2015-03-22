package net.minecraftforge.common.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.food.ExhaustionEvent;
import net.minecraftforge.event.food.FoodEvent;
import net.minecraftforge.event.food.HealthRegenEvent;
import net.minecraftforge.event.food.StarvationEvent;

public class FoodAccess {


    public static IEdible getEdible(ItemStack food)
    {
        if(food.getItem() instanceof IEdible)
            return (IEdible)food.getItem();
        else if(food.getItem() instanceof ItemBlock && ((ItemBlock)food.getItem()).block instanceof IEdible)
            return (IEdible)((ItemBlock)food.getItem()).block;
        else
            return null;
    }

    public static boolean isFood(ItemStack food)
    {
        return getEdible(food) != null;
    }

    public static boolean isEdible(ItemStack food)
    {
        IEdible edible = getEdible(food);
        return edible != null && edible.isEdible(food);
    }

    public static FoodValues getFoodValues(ItemStack food)
    {
        FoodValues foodValues = getUnmodifiedFoodValues(food);
        if (foodValues != null)
        {
            FoodEvent.GetFoodValues event = new FoodEvent.GetFoodValues(food, foodValues);
            MinecraftForge.EVENT_BUS.post(event);
            return event.foodValues;
        }
        return null;
    }

    public static FoodValues getFoodValuesForPlayer(ItemStack food, EntityPlayer player)
    {
        FoodValues foodValues = getFoodValues(food);
        if (foodValues != null)
        {
            FoodEvent.GetPlayerFoodValues event = new FoodEvent.GetPlayerFoodValues(player, food, foodValues);
            MinecraftForge.EVENT_BUS.post(event);
            return event.foodValues;
        }
        return null;
    }

    public static FoodValues getUnmodifiedFoodValues(ItemStack food)
    {
        if (isEdible(food))
            return getEdible(food).getFoodValues(food);

        return null;
    }

    public static float getExhaustion(EntityPlayer player)
    {
        return player.getFoodStats().getExhaustionLevel();
    }

    public static float getMaxExhaustion(EntityPlayer player)
    {
        ExhaustionEvent.GetMaxExhaustion event = new ExhaustionEvent.GetMaxExhaustion(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.maxExhaustionLevel;
    }

    public static int getHealthRegenTickPeriod(EntityPlayer player)
    {
        HealthRegenEvent.GetRegenTickPeriod event = new HealthRegenEvent.GetRegenTickPeriod(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.regenTickPeriod;
    }

    public static int getStarveDamageTickPeriod(EntityPlayer player)
    {
        StarvationEvent.GetStarveTickPeriod event = new StarvationEvent.GetStarveTickPeriod(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.starveTickPeriod;
    }
}
