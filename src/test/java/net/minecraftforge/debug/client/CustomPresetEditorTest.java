/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext.DimensionsUpdater;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod(CustomPresetEditorTest.MODID)
public class CustomPresetEditorTest
{
    public static final String MODID = "custom_preset_editor_test";
    public static final ResourceKey<WorldPreset> WORLD_PRESET_KEY = ResourceKey.create(Registries.WORLD_PRESET, new ResourceLocation(MODID, MODID));

    @EventBusSubscriber(modid = MODID, bus = Bus.MOD)
    public static class CommonModEvents
    {
        @SubscribeEvent
        public static void onGatherData(GatherDataEvent event)
        {
            DataGenerator gen = event.getGenerator();
            PackOutput packOutput = gen.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            RegistrySetBuilder registrySetBuilder =  new RegistrySetBuilder()
                .add(Registries.WORLD_PRESET, context -> context.register(WORLD_PRESET_KEY, makeWorldPreset(context)));

            gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of(MODID))
            {
                @Override
                public String getName()
                {
                    return MODID + ":" + super.getName(); // dataproviders must have unique names
                }
            });
        }

        private static WorldPreset makeWorldPreset(BootstapContext<WorldPreset> context)
        {
            Holder<NoiseGeneratorSettings> overworldNoise = context.lookup(Registries.NOISE_SETTINGS)
                .getOrThrow(NoiseGeneratorSettings.OVERWORLD);
            Holder<Biome> plains = context.lookup(Registries.BIOME)
                .getOrThrow(Biomes.PLAINS);
            Holder<DimensionType> overworldDimensionType = context.lookup(Registries.DIMENSION_TYPE)
                .getOrThrow(BuiltinDimensionTypes.OVERWORLD);
            BiomeSource biomeSource = new FixedBiomeSource(plains);
            ChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(biomeSource, overworldNoise);
            LevelStem levelStem = new LevelStem(overworldDimensionType, chunkGenerator);
            return new WorldPreset(Map.of(LevelStem.OVERWORLD, levelStem));
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onRegisterPresetEditors(RegisterPresetEditorsEvent event)
        {
            event.register(WORLD_PRESET_KEY, SwampDesertScreen::new);
        }
    }

    public static class SwampDesertScreen extends Screen
    {
        private final CreateWorldScreen parent;

        public SwampDesertScreen(CreateWorldScreen parent, WorldCreationContext context)
        {
            super(Component.literal(MODID));
            this.parent = parent;
        }

        @Override
        protected void init()
        {
            // Examples of configuring the world preset via gui controls.
            // These buttons tell the parent create world screen to replace the overworld with a single-biome dimension when clicked.
            this.addRenderableWidget(
                Button.builder(Component.literal("Swamp"), this.onPressBiomeButton(Biomes.SWAMP))
                    .bounds(this.width/2 - 155, this.height - 28, 150, 20)
                    .build());
            this.addRenderableWidget(
                Button.builder(Component.literal("Desert"), this.onPressBiomeButton(Biomes.DESERT))
                    .bounds(this.width/2 + 5, this.height - 28, 150, 20)
                    .build());
        }

        private OnPress onPressBiomeButton(ResourceKey<Biome> biomeKey)
        {
            return button -> {
                this.parent.getUiState().updateDimensions(singleBiomeDimension(biomeKey));
                this.minecraft.setScreen(this.parent);
            };
        }

        private DimensionsUpdater singleBiomeDimension(ResourceKey<Biome> biomeKey)
        {
            // The original dimension list from the world preset json is provided to the DimensionsUpdater lambda here.
            // We can alter which dimensions are present by returning a different list of dimensions.
            return (registries, oldDimensions) -> {
                Holder<NoiseGeneratorSettings> overworldNoise = registries.registryOrThrow(Registries.NOISE_SETTINGS)
                    .getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
                Holder<Biome> biome = registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(biomeKey);
                return oldDimensions.replaceOverworldGenerator(registries, new NoiseBasedChunkGenerator(new FixedBiomeSource(biome), overworldNoise));
            };
        }
    }
}
