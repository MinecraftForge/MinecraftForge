/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockWithContextConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(OceanBiomeTest.MODID)
public class OceanBiomeTest {
    public static final String MODID = "ocean_biome_test";
    private static final boolean ENABLE = false;

    public OceanBiomeTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TestBiomes.BIOMES.register(modEventBus);
        modEventBus.addListener(OceanBiomeTest::setup);
    }

    public static void setup(FMLCommonSetupEvent event)
    {
        if (!ENABLE) return;
        /**
         * Adds a new ocean for the NORMAL, COLD, and FROZEN types
         */
        BiomeManager.addOceanBiome(BiomeManager.OceanType.NORMAL, new BiomeManager.OceanBiomeEntry(TestBiomes.TEST_OCEAN1.get(), TestBiomes.TEST_OCEAN1_DEEP.get(), TestBiomes.TEST_OCEAN1_DEEP.get(), 10));
        BiomeManager.addOceanBiome(BiomeManager.OceanType.COLD, new BiomeManager.OceanBiomeEntry(TestBiomes.TEST_OCEAN2.get(), Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, 7));
        BiomeManager.addOceanBiome(BiomeManager.OceanType.FROZEN, new BiomeManager.OceanBiomeEntry(TestBiomes.TEST_OCEAN3.get(), Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, 5));
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class TestBiomes
    {
        public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
        public static final RegistryObject<Biome> TEST_OCEAN1 = BIOMES.register("test_ocean1", () -> new TestOceanBiome(13550382, false));
        public static final RegistryObject<Biome> TEST_OCEAN1_DEEP = BIOMES.register("test_ocean1_deep", () -> new TestOceanBiome(16777006, true));
        public static final RegistryObject<Biome> TEST_OCEAN2 = BIOMES.register("test_ocean2", () -> new TestOceanBiome(16777215, false));
        public static final RegistryObject<Biome> TEST_OCEAN3 = BIOMES.register("test_ocean3", () -> new TestOceanBiome(65535, false));
    }

    private static class TestOceanBiome extends Biome
    {
        public TestOceanBiome(int waterColor, boolean deep)
        {
            super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.OCEAN).depth(deep ? -1.8F : -1.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(waterColor).waterFogColor(waterColor).parent(null));
            this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
            this.addStructure(Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(false)));
            this.addStructure(Feature.OCEAN_RUIN.withConfiguration(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F)));
            DefaultBiomeFeatures.addOceanCarvers(this);
            DefaultBiomeFeatures.addStructures(this);
            DefaultBiomeFeatures.addLakes(this);
            DefaultBiomeFeatures.addMonsterRooms(this);
            DefaultBiomeFeatures.addStoneVariants(this);
            DefaultBiomeFeatures.addOres(this);
            DefaultBiomeFeatures.addSedimentDisks(this);
            DefaultBiomeFeatures.addScatteredOakTrees(this);
            DefaultBiomeFeatures.addDefaultFlowers(this);
            DefaultBiomeFeatures.addSparseGrass(this);
            DefaultBiomeFeatures.addMushrooms(this);
            DefaultBiomeFeatures.addReedsAndPumpkins(this);
            DefaultBiomeFeatures.addSprings(this);
            this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.withConfiguration(new BlockWithContextConfig(Blocks.SEAGRASS.getDefaultState(), new BlockState[]{Blocks.STONE.getDefaultState()}, new BlockState[]{Blocks.WATER.getDefaultState()}, new BlockState[]{Blocks.WATER.getDefaultState()})).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.01F))));
            DefaultBiomeFeatures.addSeagrass(this);
            DefaultBiomeFeatures.addExtraKelp(this);
            DefaultBiomeFeatures.addFreezeTopLayer(this);
        }
    }
}