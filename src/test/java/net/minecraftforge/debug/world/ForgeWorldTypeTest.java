/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeWorldTypeScreens;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod("forge_world_type_test")
public class ForgeWorldTypeTest
{
    @ObjectHolder("forge_world_type_test:test_world_type")
    public static ForgeWorldType testWorldType;

    public ForgeWorldTypeTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ForgeWorldType.class, this::registerWorldTypes);
    }

    private void registerWorldTypes(RegistryEvent.Register<ForgeWorldType> event)
    {
        event.getRegistry().registerAll(
                new ForgeWorldType(WorldGenSettings::makeDefaultOverworld).setRegistryName("test_world_type")
        );
        event.getRegistry().registerAll(
                new ForgeWorldType(this::createChunkGenerator).setRegistryName("test_world_type2")
        );
    }

    private ChunkGenerator createChunkGenerator(Registry<Biome> biomes, Registry<NoiseGeneratorSettings> dimensionSettings, long seed, String settings)
    {
        return WorldGenSettings.makeDefaultOverworld(biomes, dimensionSettings, seed);
    }

    @Mod.EventBusSubscriber(modid="forge_world_type_test", value=Dist.CLIENT, bus=Bus.MOD)
    public static class ForgeWorldTypeTestClientModEvents
    {
        @SubscribeEvent
        public static void registerWorldTypeScreenFactories(FMLClientSetupEvent event)
        {
            ForgeWorldTypeScreens.registerFactory(testWorldType, (returnTo, dimensionGeneratorSettings) -> new Screen(testWorldType.getDisplayName())
            {
                @Override
                protected void init()
                {
                    super.init();

                    addRenderableWidget(new Button(0, 0, 120, 20, new TextComponent("close"), btn -> {
                        Minecraft.getInstance().setScreen(returnTo);
                    }));
                }
            });
        }
    }
}
