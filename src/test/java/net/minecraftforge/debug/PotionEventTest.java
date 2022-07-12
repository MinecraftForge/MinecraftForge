/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.living.MobEffectEvent;
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
