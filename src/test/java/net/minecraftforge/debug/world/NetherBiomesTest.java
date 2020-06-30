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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Blocks;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.biome.ParticleEffectAmbience;
import net.minecraft.world.biome.SoundAdditionsAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(NetherBiomesTest.MODID)
public class NetherBiomesTest
{
    public static final String MODID = "nether_biomes_test";

    private static final boolean ENABLED = false;
    private static Logger logger;
    private static Biome biome;

    public NetherBiomesTest()
    {
        logger = LogManager.getLogger();

        if (ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(NetherBiomesTest::onSetup);

            DeferredRegister<Biome> register = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
            logger.info("Creating Nether Biome");
            register.register("biome", () -> (biome = new ASimpleIllustratoryNetherBiome()));
            register.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    public static void onSetup(FMLCommonSetupEvent event)
    {
        logger.info("Adding Nether Biome");
        BiomeManager.addNetherBiome(biome);
    }

    private static class ASimpleIllustratoryNetherBiome extends Biome
    {
        private ASimpleIllustratoryNetherBiome()
        {
            super((new Builder())
                    .surfaceBuilder(SurfaceBuilder.field_237189_ad_, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG)
                    .precipitation(Biome.RainType.NONE)
                    .category(Biome.Category.NETHER)
                    .depth(0.1F)
                    .scale(0.2F)
                    .temperature(2.0F)
                    .downfall(0.0F)
                    .func_235097_a_((new BiomeAmbience.Builder())
                            .func_235246_b_(0x1ef90e)
                            .func_235248_c_(329011)
                            .func_235239_a_(1705242)
                            .func_235244_a_(new ParticleEffectAmbience(ParticleTypes.ENCHANT, 0.01428F))
                            .func_235241_a_(SoundEvents.field_232839_o_)
                            .func_235243_a_(new MoodSoundAmbience(SoundEvents.field_232844_p_, 6000, 8, 2.0D))
                            .func_235242_a_(new SoundAdditionsAmbience(SoundEvents.field_232820_n_, 0.0111D))
                            .func_235240_a_(BackgroundMusicTracks.func_232677_a_(SoundEvents.field_232763_is_))
                            .func_235238_a_())
                    .parent((String)null)
                    .func_235098_a_(ImmutableList.of(
                            // This Biome.Attributes class is what specifies the positioning of the biome in the nether.
                            new Biome.Attributes(0.0F, 0.24F, 0.24F, 0.0F, 0.375F))));

            DefaultBiomeFeatures.addMushrooms(this);
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.SPRING_FEATURE.withConfiguration(DefaultBiomeFeatures.NETHER_SPRING_CONFIG).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(8, 4, 8, 128))));
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.field_235147_ap_).withPlacement(Placement.field_236960_A_.configure(new FrequencyConfig(10))));
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.field_235148_aq_).withPlacement(Placement.field_236960_A_.configure(new FrequencyConfig(10))));
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.GLOWSTONE_BLOB.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.LIGHT_GEM_CHANCE.configure(new FrequencyConfig(10))));
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.GLOWSTONE_BLOB.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(10, 0, 0, 128))));
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Blocks.MAGMA_BLOCK.getDefaultState(), 33)).withPlacement(Placement.MAGMA.configure(new FrequencyConfig(4))));
            this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.SPRING_FEATURE.withConfiguration(DefaultBiomeFeatures.ENCLOSED_NETHER_SPRING_CONFIG).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(16, 10, 20, 128))));

            this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 1, 4, 4));
        }
    }
}
