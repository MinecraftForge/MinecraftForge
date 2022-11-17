/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEatEvent;
import net.minecraftforge.event.entity.living.LivingFoodEffectEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_food_event_test")
public class LivingFoodEventTest
{
    public static final Logger LOGGER = LogManager.getLogger();

    public LivingFoodEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingEat);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingFoodEffect);
    }

    // Tests the LivingEatEvent
    // Logs the items a LivingEntity is about to eat
    // Cancels the event if it is Steak
    // Changes to 1 nutrition and 0 saturation for anything else
    public void onLivingEat(LivingEatEvent event)
    {
        LOGGER.info("{} is about to eat {} with {} nutrition and {} saturation", event.getEntity().getType(),
                event.getFoodItem().getItem(), event.getFoodAmount(), event.getSaturationAmount());
        if (event.getFoodItem().getItem() == Items.COOKED_BEEF)
        {
            event.setCanceled(true);
            LOGGER.info("Canceled eating Steak");
        }
        else
        {
            event.setFoodAmount(1);
            event.setSaturationAmount(0);
            LOGGER.info("Changed to 1 nutrition and 0 saturation");
        }
    }

    // Tests the LivingFoodEffectEvent
    // Entirely removes potion effects from eating a golden apple
    // Add a regeneration 4 effect to pufferfishes
    public void onLivingFoodEffect(LivingFoodEffectEvent event)
    {
        Item item = event.getFoodItem().getItem();
        if (item == Items.GOLDEN_APPLE)
        {
            event.getEffects().clear();
            LOGGER.info("Removed effects from Golden Apple");
        }
        else if (item == Items.PUFFERFISH)
        {
            event.getEffects().add(
                    new Pair<MobEffectInstance, Float>(new MobEffectInstance(MobEffects.REGENERATION, 100, 3), 1f));
            LOGGER.info("Added Regeneration to PufferFish");
        }
    }
}
