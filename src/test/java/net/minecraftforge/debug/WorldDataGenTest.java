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
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.worldgen.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Random;

@Mod(WorldDataGenTest.MODID)
@Mod.EventBusSubscriber(modid = WorldDataGenTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WorldDataGenTest {
    public static final String MODID = "world_data_gen";

    @SubscribeEvent
    public static void gatherDataEvent(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(new ConfiguredCarverProvider(generator, helper, MODID)
                .put(new ResourceLocation(MODID, "carver_test_0"), ConfiguredCarverProvider.getDefault().setCarver(WorldCarver.CANYON).setConfig(new ProbabilityConfig(0.08F)))
                .put(new ResourceLocation(MODID, "carver_test_1"), ConfiguredCarverProvider.getDefault().setCarver(WorldCarver.field_236240_b_).setConfig(new ProbabilityConfig(0.5F)))
        );
        generator.addProvider(new ProcessorListProvider(generator, helper, MODID)
                .put(new ResourceLocation(MODID, "proc_list_test_0"), new StructureProcessorList(ImmutableList.of(
                        BlockIgnoreStructureProcessor.AIR,
                        BlackStoneReplacementProcessor.field_237058_b_,
                        LavaSubmergingProcessor.field_241532_b_,
                        new GravityStructureProcessor(Heightmap.Type.OCEAN_FLOOR, -10),
                        new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new BlockMatchRuleTest(Blocks.END_STONE), new BlockStateMatchRuleTest(Blocks.EMERALD_BLOCK.getDefaultState()), Blocks.DIAMOND_BLOCK.getDefaultState()))))
                ))
        );
        generator.addProvider(new Biomes(event));
        generator.addProvider(new DimType(event));
        generator.addProvider(new Dim(event));
    }

    static class Biomes extends BiomeDataProvider {
        protected Biomes(GatherDataEvent event) {
            super(event.getGenerator(), event.getExistingFileHelper(), MODID);
        }

        @Override
        protected void start() {
            this.put(new ResourceLocation(MODID, "biome_test_0"),
                    BiomeMaker.func_244216_a(1.5F, 0.45F, ConfiguredSurfaceBuilders.field_244169_a, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_1"),
                    BiomeMaker.func_244216_a(0.65F, 0.25F, ConfiguredSurfaceBuilders.field_244174_f, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_2"),
                    BiomeMaker.func_244216_a(0.2F, 0.33F, ConfiguredSurfaceBuilders.field_244186_r, false)
            );
            this.put(new ResourceLocation(MODID, "biome_test_3"),
                    BiomeMaker.func_244216_a(0.5F, 0.15F, ConfiguredSurfaceBuilders.field_244176_h, false)
            );
        }
    }

    static class DimType extends DimensionTypeProvider {
        public DimType(GatherDataEvent event) {
            super(event.getGenerator(), event.getExistingFileHelper(), MODID);
        }

        @Override
        protected void start() {
            this.put(new ResourceLocation(MODID, "dim_type_test_0"), new Builder().doBedWorks(false).doRespawnAnchorWork(false).setFixedTime(500L).setAmbientLight(0.5F).isUltrawarm(true));
        }
    }

    static class Dim extends DimensionProvider {
        protected Dim(GatherDataEvent event) {
            super(event.getGenerator(), event.getExistingFileHelper(), MODID);
        }

        @Override
        protected void start() {
            this.put(new ResourceLocation(MODID, "dim_test_0"), new Builder()
                    .setDimType(new ResourceLocation(MODID, "dim_type_test_0"))
                    .buildNoise()
                    .buildNoiseBiome()
                    .changeAltitudeNoise(new NetherBiomeProvider.Noise(-10, ImmutableList.of(-0.9, 0.5D, 1D)))
                    .addBiome()
                    .setBiome(new ResourceLocation(MODID, "biome_test_0"))
                    .setAltitude(1)
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
                    .setHumidity(-0.75F)
                    .setTemperature(0.9F)
                    .finish()
                    .finish()
                    .finish()
                    .build()
            );
        }
    }
}
