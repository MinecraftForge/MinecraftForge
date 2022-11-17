/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingFoodEffectEvent is fire when an LivingEntity eats food item
 * regardless of if any effects are applied.<br>
 * Specifically it is fired for the method call
 * {@link LivingEntity#addEatEffect()}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, no effect will be applied.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingFoodEffectEvent extends LivingEvent
{

    private final ItemStack foodItem;
    private final List<Pair<MobEffectInstance, Float>> effectPairs;

    public LivingFoodEffectEvent(LivingEntity entity, ItemStack foodItem)
    {
        super(entity);
        this.foodItem = foodItem;
        effectPairs = foodItem.getFoodProperties(entity).getEffects();
    }

    /**
     * @return the {@link ItemStack} that the entity ate
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
