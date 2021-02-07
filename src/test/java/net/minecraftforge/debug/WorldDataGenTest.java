/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.SphereReplaceConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.data.worldgen.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static net.minecraft.block.Blocks.*;

@Mod(WorldDataGenTest.MODID)
@Mod.EventBusSubscriber(modid = WorldDataGenTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WorldDataGenTest
{
    public static final String MODID = "world_data_gen";

    @SubscribeEvent
    public static void gatherDataEvent(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new Carvers(event));
        generator.addProvider(new Procs(event));
        generator.addProvider(new Features(event));
        generator.addProvider(new Structs(event));
        generator.addProvider(new Surface(event));
        generator.addProvider(new Jigsaws(event));
        generator.addProvider(new Noise(event));
        generator.addProvider(new BiomeGen(event));
        generator.addProvider(new DimType(event));
        generator.addProvider(new Dim(event));
    }

    static class Structs extends ConfiguredStructureFeatureProvider
    {
        public Structs(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "configured_struct_test_0"), Structure.MINESHAFT.withConfiguration(new MineshaftConfig(0.1467f, MineshaftStructure.Type.MESA)));
        }
    }

    static class Noise extends NoiseSettingsProvider
    {
        public Noise(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "noise_test_0"), new Builder()
                    .setDefaultBlock(SNOW_BLOCK.getDefaultState())
                    .disableMobSpawning(true)
                    .setSeaLevel(76)
                    .setupNoise()
                    .setAmplified(true)
                    .setRandomDensityOffset(true)
                    .setDensityFactor(0.823)
                    .setDensityOffset(0.34)
                    .setSizeVeritcal(3)
                    .setSizeHorizontal(1)
                    .setTopSlide(-45, 4, -3)
                    .setBottomSlide(5, 0, 0)
                    .finish()
                    .setupStructures()
                    .defaultSettings(false)
            );

        }
    }

    static class Surface extends ConfiguredSurfaceBuilderProvider
    {
        public Surface(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "ice_surface_builder_test_0"), new Builder()
                    .setSurfaceBuilder(SurfaceBuilder.FROZEN_OCEAN)
                    .setTopMaterial(BLUE_ICE.getDefaultState())
                    .setUnderMaterial(ICE.getDefaultState())
                    .setUnderWaterMaterial(FROSTED_ICE.getDefaultState())
                    .build()
            );
        }
    }

    static class Jigsaws extends JigsawPatternProvider
    {
        public Jigsaws(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "jigsaw_pattern_test_0"), new Builder()
                    .setName(new ResourceLocation(MODID,"boat/ocean/captain_quarters"))
                    .addAll(ImmutableList.of(
                            Pair.of(JigsawPiece.func_242845_a(this.regOps.getObject(Registry.CONFIGURED_FEATURE_KEY, new ResourceLocation(MODID, "feature_test_0"))), 1),
                            Pair.of(JigsawPiece.func_242861_b("bastion/units/ramparts/ramparts_2", this.regOps.getObject(Registry.STRUCTURE_PROCESSOR_LIST_KEY, new ResourceLocation(MODID, "proc_list_test_0")) ), 2)
                    ), JigsawPattern.PlacementBehaviour.RIGID)
            );
        }
    }

    static class Features extends ConfiguredFeatureProvider
    {
        public Features(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            ImmutableList.Builder<BlockState> builder = ImmutableList.builder();
            this.put(new ResourceLocation(MODID, "feature_test_0"),
                    Feature.DISK.withConfiguration(new SphereReplaceConfig(
                            END_STONE.getDefaultState(), FeatureSpread.func_242253_a(2, 2), 1, builder.add(
                            STONE.getDefaultState(), SAND.getDefaultState(), RED_SAND.getDefaultState(), DIORITE.getDefaultState(), ANDESITE.getDefaultState(), GRANITE.getDefaultState())
                            .addAll(WATER.getStateContainer().getValidStates()).build()
                    )).withPlacement(Placement.ICEBERG.configure(NoPlacementConfig.INSTANCE).chance(15)).func_242731_b(4)
            );
        }
    }

    static class Carvers extends ConfiguredCarverProvider
    {
        public Carvers(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "carver_test_0"), WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.08F)));
            this.put(new ResourceLocation(MODID, "carver_test_1"), WorldCarver.NETHER_CAVE.func_242761_a(new ProbabilityConfig(0.5F)));
        }
    }

    static class Procs extends ProcessorListProvider
    {
        public Procs(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "proc_list_test_0"), new StructureProcessorList(
                    ImmutableList.of(
                            BlockIgnoreStructureProcessor.AIR,
                            BlackStoneReplacementProcessor.field_237058_b_,
                            LavaSubmergingProcessor.field_241532_b_,
                            new GravityStructureProcessor(Heightmap.Type.OCEAN_FLOOR, -10),
                            new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new BlockMatchRuleTest(END_STONE), new BlockStateMatchRuleTest(EMERALD_BLOCK.getDefaultState()), DIAMOND_BLOCK.getDefaultState())))
                    )
            ));
        }
    }

    static class BiomeGen extends BiomeDataProvider
    {
        protected BiomeGen(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "biome_test_0"),
                    BiomeMaker.makeMountainBiome(0.85F, 0.78F, ConfiguredSurfaceBuilders.field_244169_a, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_1"),
                    BiomeMaker.makeMountainBiome(0.65F, 0.33F, ConfiguredSurfaceBuilders.field_244174_f, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_2"),
                    BiomeMaker.makeMountainBiome(0.2F, 0.45F, ConfiguredSurfaceBuilders.field_244186_r, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_3"),
                    BiomeMaker.makeMountainBiome(0.5F, 0.125F, ConfiguredSurfaceBuilders.field_244176_h, false)
            );

            Biome.Builder biome = new Biome.Builder();
            MobSpawnInfo.Builder mobBuilder = new MobSpawnInfo.Builder();
            BiomeAmbience.Builder ambience = new BiomeAmbience.Builder();
            BiomeGenerationSettings.Builder biomeGen = new BiomeGenerationSettings.Builder();

            mobBuilder.withCreatureSpawnProbability(0.25F);
            DefaultBiomeFeatures.withHostileMobs(mobBuilder, 200, 0, 200);
            DefaultBiomeFeatures.withEndermen(mobBuilder);
            DefaultBiomeFeatures.withPassiveMobs(mobBuilder);
            mobBuilder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.HORSE, 5, 2, 6));
            mobBuilder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.DONKEY, 1, 1, 3));
            biome.withMobSpawnSettings(mobBuilder.copy());

            biome.precipitation(Biome.RainType.SNOW).category(Biome.Category.ICY).depth(0.2F).scale(0.95F).temperature(-10F).withTemperatureModifier(Biome.TemperatureModifier.FROZEN).downfall(0.10F);
            ambience.withGrassColorModifier(BiomeAmbience.GrassColorModifier.DARK_FOREST).setFogColor(0xc1d2db).setWaterColor(0xbbddf0).setWaterFogColor(0x8ec1de).withSkyColor(0xa9d1e8);
            biome.setEffects(ambience.build());

            biomeGen.withSurfaceBuilder(this.regOps.getObject(Registry.CONFIGURED_SURFACE_BUILDER_KEY, new ResourceLocation(MODID, "ice_surface_builder_test_0")));
            biomeGen.withCarver(GenerationStage.Carving.AIR, regOps.getObject(Registry.CONFIGURED_CARVER_KEY ,new ResourceLocation(MODID, "carver_test_1")));
            biomeGen.withCarver(GenerationStage.Carving.LIQUID, ConfiguredCarvers.field_243772_f);
            biomeGen.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, regOps.getObject(Registry.CONFIGURED_FEATURE_KEY, new ResourceLocation(MODID, "feature_test_0")));
            DefaultBiomeFeatures.withOceanStructures(biomeGen);
            DefaultBiomeFeatures.withLavaAndWaterLakes(biomeGen);
            DefaultBiomeFeatures.withMonsterRoom(biomeGen);
            DefaultBiomeFeatures.withOverworldOres(biomeGen);
            DefaultBiomeFeatures.withInfestedStone(biomeGen);
            DefaultBiomeFeatures.withChanceBerries(biomeGen);
            DefaultBiomeFeatures.withTreesInWater(biomeGen);
            DefaultBiomeFeatures.withSnowySpruces(biomeGen);
            DefaultBiomeFeatures.withSnowySpruces(biomeGen);
            DefaultBiomeFeatures.withSnowySpruces(biomeGen);
            DefaultBiomeFeatures.withLavaAndWaterSprings(biomeGen);
            DefaultBiomeFeatures.withBlueIce(biomeGen);
            DefaultBiomeFeatures.withSavannaTrees(biomeGen);
            biomeGen.withStructure(StructureFeatures.VILLAGE_SNOWY);
            biome.withGenerationSettings(biomeGen.build());

            this.put(new ResourceLocation(MODID, "biome_test_4"), biome.build());
        }
    }

    static class DimType extends DimensionTypeProvider
    {
        public DimType(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "dim_type_test_0"), new Builder().doBedWorks(false).doRespawnAnchorWork(false).setFixedTime(500L).setAmbientLight(0.5F).isUltrawarm(true));
        }
    }

    static class Dim extends DimensionProvider
    {
        protected Dim(GatherDataEvent event)
        {
            super(event.getGenerator(), event.getRegistryOpsHelper(), MODID);
        }

        @Override
        protected void start()
        {
            this.put(new ResourceLocation(MODID, "dim_test_0"), new Builder()
                    .setDimType(new ResourceLocation(MODID, "dim_type_test_0"))
                    .buildNoise()
                    .setSettings(new ResourceLocation(MODID, "noise_test_0"))
                    .buildNoiseBiome()
                    .changeAltitudeNoise(new NetherBiomeProvider.Noise(-10, ImmutableList.of(-0.9, 0.5D, 1D)))
                    .addBiome()
                    .setBiome(new ResourceLocation(MODID, "biome_test_0"))
                    .setAltitude(0.8F)
                    .setHumidity(-0.2f)
                    .setTemperature(0.3F)
                    .buildNext()
                    .setBiome(new ResourceLocation(MODID, "biome_test_1"))
                    .setAltitude(0.6F)
                    .setHumidity(-0.25F)
                    .setTemperature(0.3F)
                    .buildNext()
                    .setBiome(new ResourceLocation(MODID, "biome_test_2"))
                    .setHumidity(-0.5F)
                    .setTemperature(0.45F)
                    .setAltitude(0.05F)
                    .buildNext()
                    .setBiome(new ResourceLocation(MODID, "biome_test_3"))
                    .setHumidity(-0.3F)
                    .setTemperature(0.55F)
                    .setBiome(new ResourceLocation(MODID, "biome_test_4"))
                    .setHumidity(0.1F)
                    .setTemperature(0.05F)
                    .setAltitude(0.15F)
                    .buildNext()
                    .setBiome(regOps.getObject(Registry.BIOME_KEY, Biomes.SNOWY_MOUNTAINS))
                    .finish()
                    .finish()
                    .finish()
                    .build()
            );
        }
    }
}
