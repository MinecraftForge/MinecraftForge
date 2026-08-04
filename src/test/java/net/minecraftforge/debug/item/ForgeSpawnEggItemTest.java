/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(value = ForgeSpawnEggItemTest.MODID)
public class ForgeSpawnEggItemTest
{
    static final String MODID = "forge_spawnegg_test";
    static final boolean ENABLED = true;

    private static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);
    private static final RegistryObject<EntityType<PigEntity>> ENTITY = ENTITIES.register("test_entity", () ->
            EntityType.Builder.create(PigEntity::new, EntityClassification.CREATURE).size(1, 1).build("test_entity")
    );

    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<ForgeSpawnEggItem> EGG = ITEMS.register("test_spawn_egg", () ->
            new ForgeSpawnEggItem(ENTITY, 0x0000FF, 0xFF0000, new Item.Properties().group(ItemGroup.MISC))
    );

    public ForgeSpawnEggItemTest()
    {
        if (ENABLED)
        {
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
            ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

            FMLJavaModLoadingContext.get().getModEventBus().register(this);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientEvents
    {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event)
        {
            if (!ENABLED) { return; }

                EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
                manager.register(PigEntity.class, new PigRenderer(manager));
        }
    }
}