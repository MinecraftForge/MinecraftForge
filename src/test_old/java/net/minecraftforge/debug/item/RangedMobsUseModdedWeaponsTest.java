/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(RangedMobsUseModdedWeaponsTest.MOD_ID)
public class RangedMobsUseModdedWeaponsTest {

    // Testing if the new alternative for ProjectileHelper.getWeaponHoldingHand works for vanilla mobs
    // as well as replacing their usages of LivingEntity#isHolding(Item) with LivingEntity#isHolding(Predicate<ItemStack>)
    // Skeletons and Illusioners should be able to use the modded bow.
    // Piglins and Pillagers should be able to use the modded crossbow.

    public static final boolean ENABLE = true;

    public static final String MOD_ID = "ranged_mobs_use_modded_weapons_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Item> MODDED_BOW = ITEMS.register("modded_bow", () ->
            new BowItem(new Item.Properties().defaultDurability(384))
    );
    private static final RegistryObject<Item> MODDED_CROSSBOW = ITEMS.register("modded_crossbow", () ->
            new CrossbowItem(new Item.Properties().defaultDurability(326))
    );

    public RangedMobsUseModdedWeaponsTest()
    {
        if(ENABLE){
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            ITEMS.register(modEventBus);
            modEventBus.addListener(this::onClientSetup);
            modEventBus.addListener(this::addCreative);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
        {
            event.accept(MODDED_BOW);
            event.accept(MODDED_CROSSBOW);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        RangedWeaponModeLProperties.initBowModelProperties();
        RangedWeaponModeLProperties.initCrossbowModelProperties();
    }

    private static class RangedWeaponModeLProperties
    {
        static void initBowModelProperties()
        {
            ItemProperties.register(MODDED_BOW.get(), new ResourceLocation("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
                if (livingEntity == null)
                {
                    return 0.0F;
                } else {
                    return livingEntity.getUseItem() != itemStack ? 0.0F : (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                }
            });
            ItemProperties.register(MODDED_BOW.get(), new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        }

        static void initCrossbowModelProperties()
        {
            ItemProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
                if (livingEntity == null)
                {
                    return 0.0F;
                } else {
                    return CrossbowItem.isCharged(itemStack) ? 0.0F : (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(itemStack);
                }
            });
            ItemProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F);
            ItemProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("charged"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F);
            ItemProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("firework"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);
        }
    }
}