/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingEatEvent is fire when a LivingEntity eats food (item, cake, etc.).<br>
 * Specifically it is fired for the method call {@link FoodData#eat()}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the food item is consumed but no changes will be
 * made to the hunger and saturation and no effects will be applied.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingEatEvent extends LivingEvent
{
    private int foodAmount;
    private float saturationAmount;
    private final ItemStack foodItem;
    private final List<Pair<MobEffectInstance, Float>> effectPairs;

    public LivingEatEvent(@Nullable LivingEntity entity, ItemStack foodItem)
    {
        super(entity);
        this.foodItem = foodItem;
        if(foodItem != null)
        {
            FoodProperties prop = foodItem.getFoodProperties(entity);
            this.foodAmount = prop.getNutrition();
            this.saturationAmount = prop.getSaturationModifier();
            this.effectPairs = prop.getEffects();
        }
        else
            this.effectPairs = null;
    }

    public LivingEatEvent(@Nullable LivingEntity entity, int foodAmount, float saturationAmount)
    {
        this(entity, null);
        this.foodAmount = foodAmount;
        this.saturationAmount = saturationAmount;
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

    /**
     * can be modified to change the applied effects and probability
     * @return the effect-probability pair of {@link #foodItem}
     */
    public List<Pair<MobEffectInstance, Float>> getEffects()
    {
        return effectPairs;
    }
    
    /**
     * adds a new {@link MobEffectInstance} and probability to apply
     * @param effect {@link MobEffectInstance} to apply
     * @param probability probability of application
     */
    public void addEffect(MobEffectInstance effect, float probability)
    {
        this.effectPairs.add(new Pair<MobEffectInstance, Float>(effect, probability));
    }
}
