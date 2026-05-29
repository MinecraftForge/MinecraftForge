/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.item;

import java.util.Map;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.event.EntityRenderersEvent;
import net.minecraftsingularity.common.singularityMod;
import net.minecraftsingularity.common.singularitySpawnEggItem;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.event.entity.EntityAttributeCreationEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.fml.util.ObfuscationReflectionHelper;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

@Mod(value = singularitySpawnEggItemTest.MODID)
public class singularitySpawnEggItemTest
{
    static final String MODID = "singularity_spawnegg_test";
    static final boolean ENABLED = true;

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(singularityRegistries.ENTITY_TYPES, MODID);
    private static final RegistryObject<EntityType<Pig>> ENTITY = ENTITIES.register("test_entity", () ->
            EntityType.Builder.of(Pig::new, MobCategory.CREATURE).sized(1, 1).build("test_entity")
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MODID);
    private static final RegistryObject<singularitySpawnEggItem> EGG = ITEMS.register("test_spawn_egg", () ->
            new singularitySpawnEggItem(ENTITY, 0x0000FF, 0xFF0000, new Item.Properties())
    );

    public singularitySpawnEggItemTest()
    {
        if (ENABLED)
        {
            var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            ITEMS.register(eventBus);
            ENTITIES.register(eventBus);
            eventBus.register(this);
            eventBus.addListener(this::addCreative);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(EGG);
    }

    @SubscribeEvent
    public void onRegisterAttributes(final EntityAttributeCreationEvent event)
    {
        AttributeSupplier.Builder attributes = Pig.createAttributes();
        //Remove step height attribute to validate that things are handled properly when an entity doesn't have it
        Map<Attribute, AttributeInstance> builder = ObfuscationReflectionHelper.getPrivateValue(AttributeSupplier.Builder.class, attributes, "f_2226" + "2_");
        if (builder != null) {
            builder.remove(singularityMod.STEP_HEIGHT_ADDITION.get());
        }
        event.put(ENTITY.get(), attributes.build());
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientEvents
    {
        @SubscribeEvent
        public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
        {
            if (!ENABLED) { return; }

            event.registerEntityRenderer(ENTITY.get(), PigRenderer::new);
        }
    }
}
