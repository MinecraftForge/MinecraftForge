/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingEatEvent is fire when a LivingEntity eats food (item, cake, etc.).<br>
 * Specifically it is fired for the method call {@link FoodData#eat()}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the food item is consumed but no changes will be
 * made to the hunger and saturation.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingEatEvent extends LivingEvent
{
    private int foodAmount;
    private float saturationAmount;
    private final ItemStack foodItem;

    private LivingEatEvent(@javax.annotation.Nullable LivingEntity entity, int foodAmount, float saturationAmount,
            ItemStack foodItem)
    {
        super(entity);
        this.foodAmount = foodAmount;
        this.saturationAmount = saturationAmount;
        this.foodItem = foodItem;
    }

    public LivingEatEvent(@Nullable LivingEntity entity, int foodAmount, float saturationAmount)
    {
        this(entity, foodAmount, saturationAmount, null);
    }

    public LivingEatEvent(@Nullable LivingEntity entity, ItemStack foodItem, FoodProperties prop)
    {
        this(entity, prop.getNutrition(), prop.getSaturationModifier(), foodItem);
    }

    /**
     * @return the amount of food gain to occur
     */
    public int getFoodAmount()
    {
        return foodAmount;
    }

    /**
     * sets the amount of food gain to occur
     * @param foodAmount
     */
    public void setFoodAmount(int foodAmount)
    {
        this.foodAmount = foodAmount;
    }

    /**
     * @return the amount of saturation gain to occur
     */
    public float getSaturationAmount()
    {
        return saturationAmount;
    }

    /**
     * sets the amount of saturation gain to occur
     * @param saturaionAmount
     */
    public void setSaturationAmount(float saturaionAmount)
    {
        this.saturationAmount = saturaionAmount;
    }

    /**
     * @return the {@link ItemStack} that the entity ate (if available)
     */
    public ItemStack getFoodItem()
    {
        return foodItem;
    }
}
