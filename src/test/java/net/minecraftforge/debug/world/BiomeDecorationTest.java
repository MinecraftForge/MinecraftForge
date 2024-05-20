package net.minecraftforge.debug.world;

/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BiomeDecorationTest.MODID)
public class BiomeDecorationTest
{
    public static final String MODID = "biome_decoration_test";
    private static final boolean ENABLED = true;

    /* Dynamic registry objects */
    public static final ResourceKey<ConfiguredFeature<?, ?>> TEST_FEATURE = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MODID, "test_feature"));
    private static final ResourceKey<PlacedFeature> TEST_GENERATION = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MODID, "test_generation"));
    public static final ResourceKey<BiomeModifier> TEST_BIOME = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation("test_biome"));

    static List<OreConfiguration.TargetBlockState> test = List.of(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
            Blocks.BLUE_ICE.defaultBlockState()));

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, context -> context.register(TEST_FEATURE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(test, 20))))
            .add(Registries.PLACED_FEATURE, context -> context.register(TEST_GENERATION,
                    new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(TEST_FEATURE),
                            List.of(CountPlacement.of(100), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(32)), BiomeFilter.biome()))))
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> context.register(TEST_BIOME, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                    context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                    HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(TEST_GENERATION)),
                    GenerationStep.Decoration.UNDERGROUND_ORES)));

    public BiomeDecorationTest()
    {
        if (!ENABLED)
            return;

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event)
    {
        event.getGenerator().addProvider(event.includeServer(), (DataProvider.Factory<BiomeDecoration>) output -> new BiomeDecoration(output, event.getLookupProvider()));
    }

    private static class BiomeDecoration extends DatapackBuiltinEntriesProvider
    {

        public BiomeDecoration(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries, BUILDER, Set.of(MODID));
        }

        @Override
        public String getName()
        {
            return "Biome Decoration Registries: " + MODID;
        }
    }
}

