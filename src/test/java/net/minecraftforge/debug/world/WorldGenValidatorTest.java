/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HeightmapConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WorldGenValidatorTest.MODID)
public class WorldGenValidatorTest
{
    static final String MODID = "worldgen_validator_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final boolean ENABLED = false;
    private static ConfiguredFeature<?, ?> COAL_ORE_FEATURE;

    public WorldGenValidatorTest()
    {
        if (ENABLED)
        {
            LOGGER.warn("WorldGenValidatorTest is now active. Crash with worldgen info will occur.");
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonEvent);
            MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoading);
        }
    }

    public void onFMLCommonEvent(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            COAL_ORE_FEATURE = Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.COAL_ORE.defaultBlockState(), 17)).count(128).squared().count(20);
            Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(MODID, "test"), COAL_ORE_FEATURE);
        });
    }

    public void onBiomeLoading(BiomeLoadingEvent event)
    {
        if (event.getName().equals(Biomes.SUNFLOWER_PLAINS.location()))
        {
            // registered cf
            event.getGeneration().getFeatures(GenerationStep.Decoration.RAW_GENERATION).add(() -> COAL_ORE_FEATURE);

            // broken and unregistered configured feature
            event.getGeneration().getFeatures(GenerationStep.Decoration.RAW_GENERATION)
                    .add(() -> Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                            new SimpleStateProvider(Blocks.ACACIA_LOG.defaultBlockState()),
                            new StraightTrunkPlacer(5, -99999, 99999),
                            new SimpleStateProvider(Blocks.ACACIA_LEAVES.defaultBlockState()),
                            new SimpleStateProvider(Blocks.ACACIA_SAPLING.defaultBlockState()),
                            new SpruceFoliagePlacer(UniformInt.of(2, 3),
                                    UniformInt.of(1, 5),
                                    UniformInt.of(1, 5)),
                            new TwoLayersFeatureSize(2, 0, 2)))
                            .ignoreVines()
                            .build())
                            .decorated(FeatureDecorator.HEIGHTMAP.configured(new HeightmapConfiguration(Heightmap.Types.MOTION_BLOCKING)))
                            .squared()
                            .rarity(5));

            // unregistered configured feature
            event.getGeneration().getFeatures(GenerationStep.Decoration.RAW_GENERATION)
                    .add(() -> Feature.DESERT_WELL.configured(FeatureConfiguration.NONE)
                            .decorated(FeatureDecorator.HEIGHTMAP.configured(new HeightmapConfiguration(Heightmap.Types.MOTION_BLOCKING)))
                            .squared()
                            .rarity(5));
        }
    }
}
