/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(value = ForgeSpawnEggItemTest.MODID)
public class ForgeSpawnEggItemTest
{
    static final String MODID = "forge_spawnegg_test";
    static final boolean ENABLED = true;

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final RegistryObject<EntityType<Pig>> ENTITY = ENTITIES.register("test_entity", () ->
            EntityType.Builder.of(Pig::new, MobCategory.CREATURE).sized(1, 1).build("test_entity")
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<ForgeSpawnEggItem> EGG = ITEMS.register("test_spawn_egg", () ->
            new ForgeSpawnEggItem(ENTITY, 0x0000FF, 0xFF0000, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
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

    @SubscribeEvent
    public void onRegisterAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(ENTITY.get(), Pig.createAttributes().build());
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