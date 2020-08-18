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
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
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
        generator.addProvider(new BiomeGen(event));
        generator.addProvider(new DimType(event));
        generator.addProvider(new Dim(event));
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
                    )).withPlacement(Placement.ICEBERG.configure(NoPlacementConfig.field_236556_b_).func_242729_a(15)).func_242731_b(4)
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
            this.put(new ResourceLocation(MODID, "carver_test_1"), WorldCarver.field_236240_b_.func_242761_a(new ProbabilityConfig(0.5F)));
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
                    BiomeMaker.func_244216_a(0.85F, 0.78F, ConfiguredSurfaceBuilders.field_244169_a, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_1"),
                    BiomeMaker.func_244216_a(0.65F, 0.33F, ConfiguredSurfaceBuilders.field_244174_f, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_2"),
                    BiomeMaker.func_244216_a(0.2F, 0.45F, ConfiguredSurfaceBuilders.field_244186_r, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_3"),
                    BiomeMaker.func_244216_a(0.5F, 0.125F, ConfiguredSurfaceBuilders.field_244176_h, false)
            );

            Biome.Builder biome = new Biome.Builder();
            MobSpawnInfo.Builder mobBuilder = new MobSpawnInfo.Builder();
            BiomeAmbience.Builder ambience = new BiomeAmbience.Builder();
            BiomeGenerationSettings.Builder biomeGen = new BiomeGenerationSettings.Builder();

            mobBuilder.func_242572_a(0.25F);
            DefaultBiomeFeatures.func_243735_b(mobBuilder, 200, 0, 200);
            DefaultBiomeFeatures.func_243749_i(mobBuilder);
            DefaultBiomeFeatures.func_243714_a(mobBuilder);
            mobBuilder.func_242575_a(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.HORSE, 5, 2, 6));
            mobBuilder.func_242575_a(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.DONKEY, 1, 1, 3));
            biome.func_242458_a(mobBuilder.func_242577_b());

            biome.precipitation(Biome.RainType.SNOW).category(Biome.Category.ICY).depth(0.2F).scale(0.95F).temperature(-10F).func_242456_a(Biome.TemperatureModifier.FROZEN).downfall(0.10F);
            ambience.func_242537_a(BiomeAmbience.GrassColorModifier.DARK_FOREST).func_235239_a_(0xc1d2db).func_235246_b_(0xbbddf0).func_235248_c_(0x8ec1de).func_242539_d(0xa9d1e8);
            biome.func_235097_a_(ambience.func_235238_a_());

            biomeGen.func_242517_a(ConfiguredSurfaceBuilders.field_244180_l);
            biomeGen.func_242512_a(GenerationStage.Carving.AIR, regOps.getObject(Registry.field_243551_at ,new ResourceLocation(MODID, "carver_test_1")));
            biomeGen.func_242512_a(GenerationStage.Carving.LIQUID, ConfiguredCarvers.field_243772_f);
            biomeGen.func_242513_a(GenerationStage.Decoration.UNDERGROUND_ORES, regOps.getObject(Registry.field_243552_au, new ResourceLocation(MODID, "feature_test_0")));
            DefaultBiomeFeatures.func_243736_c(biomeGen);
            DefaultBiomeFeatures.func_243742_f(biomeGen);
            DefaultBiomeFeatures.func_243746_h(biomeGen);
            DefaultBiomeFeatures.func_243750_j(biomeGen);
            DefaultBiomeFeatures.func_243753_m(biomeGen);
            DefaultBiomeFeatures.func_243758_r(biomeGen);
            DefaultBiomeFeatures.func_243763_w(biomeGen);
            DefaultBiomeFeatures.func_243694_H(biomeGen);
            DefaultBiomeFeatures.func_243694_H(biomeGen);
            DefaultBiomeFeatures.func_243694_H(biomeGen);
            DefaultBiomeFeatures.func_243727_ak(biomeGen);
            DefaultBiomeFeatures.func_243729_am(biomeGen);
            DefaultBiomeFeatures.func_243687_A(biomeGen);
            biomeGen.func_242516_a(StructureFeatures.field_244157_w);
            biome.func_242457_a(biomeGen.func_242508_a());

            this.put(new ResourceLocation(MODID, "biome_test_4"), biome.func_242455_a());
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
                    .setBiome(regOps.getObject(Registry.field_239720_u_, Biomes.SNOWY_MOUNTAINS))
                    .finish()
                    .finish()
                    .finish()
                    .build()
            );
        }
    }
}
