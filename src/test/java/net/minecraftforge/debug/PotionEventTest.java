/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("potion_event_test")
@Mod.EventBusSubscriber
public class PotionEventTest
{
    private static Logger LOGGER = LogManager.getLogger(PotionEventTest.class);

    public PotionEventTest()
    {
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
    }
    
    private void onSetup(FMLCommonSetupEvent event)
    {
    	BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.ICE), Ingredient.of(Items.LAVA_BUCKET), new ItemStack(Items.OBSIDIAN));
    }
    
    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
            LOGGER.info("{} has a new PotionEffect {}, the old one was {}", event.getEntityLiving(), event.getPotionEffect(), event.getOldPotionEffect());
    }

    @SubscribeEvent
    public static void isPotionApplicable(PotionEvent.PotionApplicableEvent event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
        {
            event.setResult(Result.ALLOW);
            LOGGER.info("Allowed Potion {} for Entity {}", event.getPotionEffect(), event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onPotionRemove(PotionEvent.PotionRemoveEvent event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
            LOGGER.info("Effect {} got Removed from {}", event.getPotionEffect(), event.getEntityLiving());
    }

    @SubscribeEvent
    public static void onPotionExpiry(PotionEvent.PotionExpiryEvent event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide)
            LOGGER.info("Effect {} expired from {}", event.getPotionEffect(), event.getEntityLiving());
    }
}
