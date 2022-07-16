/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CustomColorResolverTest.MOD_ID)
public class CustomColorResolverTest
{
    static final String MOD_ID = "custom_color_resolver_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    private static final RegistryObject<Block> BLOCK = BLOCKS.register("test_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));

    public CustomColorResolverTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        BLOCKS.register(modBus);

        ITEMS.register("test_block", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid=MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientHandler
    {

        private static final ColorResolver COLOR_RESOLVER = (biome, x, z) -> biome.getFogColor();

        @SubscribeEvent
        static void registerColorResolver(RegisterColorHandlersEvent.ColorResolvers event)
        {
            event.register(COLOR_RESOLVER);
        }

        @SubscribeEvent
        static void registerBlockColor(RegisterColorHandlersEvent.Block event)
        {
            event.register(((state, btGetter, pos, tintIndex) -> btGetter == null || pos == null ? 0 : btGetter.getBlockTint(pos, COLOR_RESOLVER)), BLOCK.get());
        }
    }
}
