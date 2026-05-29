/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftsingularity.common.brewing.BrewingRecipeRegistry;
import net.minecraftsingularity.event.entity.living.MobEffectEvent;
import net.minecraftsingularity.eventbus.api.Event.Result;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("potion_event_test")
@Mod.EventBusSubscriber
public class PotionEventTest
{
    private static Logger LOGGER = LogManager.getLogger(PotionEventTest.class);

    public PotionEventTest() {}

    @SubscribeEvent
    private void onSetup(FMLCommonSetupEvent event) {
    	BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.ICE), Ingredient.of(Items.LAVA_BUCKET), new ItemStack(Items.OBSIDIAN));
    }

    @SubscribeEvent
    public static void onPotionAdded(MobEffectEvent.Added event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
            LOGGER.info("{} has a new PotionEffect {} from {}, the old one was {}", event.getEntity(), event.getEffectInstance(), event.getEffectSource(), event.getOldEffectInstance());
    }

    @SubscribeEvent
    public static void isPotionApplicable(MobEffectEvent.Applicable event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
        {
            event.setResult(Result.ALLOW);
            LOGGER.info("Allowed Potion {} for Entity {}", event.getEffectInstance(), event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPotionRemove(MobEffectEvent.Remove event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
            LOGGER.info("Effect {} got Removed from {}", event.getEffectInstance(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPotionExpiry(MobEffectEvent.Expired event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
            LOGGER.info("Effect {} expired from {}", event.getEffectInstance(), event.getEntity());
    }
}
