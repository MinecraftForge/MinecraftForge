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

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Optional;

/**
 * To test:
 *
 * Set ENABLED to true
 * When enabled the "biome_variant_test:main" biome should generate in the overworld.
 * Within the "biome area" there should be hills ("biome_variant_test:hills") and potentially rivers ("biome_variant_test:river") flowing to/from other biomes.
 * At the edge of the "biome area" there should be an edge biome ("biome_variant_test:edge").
 * If the "biome area" is next to an ocean there should be a shore ("biome_variant_test:shore").
 */
@Mod(BiomeVariantTest.MODID)
public class BiomeVariantTest
{

    static final String MODID = "biome_variant_test";
    private static final boolean ENABLED = false;

    private static final RegistryKey<Biome> MAIN_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "main"));
    private static final RegistryKey<Biome> HILLS_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "hills"));
    private static final RegistryKey<Biome> EDGE_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "edge"));
    private static final RegistryKey<Biome> RIVER_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "river"));
    private static final RegistryKey<Biome> SHORE_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "shore"));

    public BiomeVariantTest()
    {
        if(ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Biome.class, this::onRegisterBiome);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        }
    }

    private void onRegisterBiome(RegistryEvent.Register<Biome> event)
    {
        //Core biome
        BiomeGenerationSettings.Builder main_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.END);
        DefaultBiomeFeatures.addDefaultCarvers(main_generation);
        DefaultBiomeFeatures.addDefaultLakes(main_generation);

        Biome main = (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.PLAINS).depth(0.1F).scale(0.1F).temperature(0.4F).downfall(0.4F).specialEffects((new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings(main_generation.build()).build();
        event.getRegistry().register(main.setRegistryName(MODID, "main"));

        //Hills variant
        BiomeGenerationSettings.Builder hills_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.END);
        DefaultBiomeFeatures.addDefaultCarvers(hills_generation);
        DefaultBiomeFeatures.addDefaultLakes(hills_generation);

        Biome hills = (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.PLAINS).depth(0.4F).scale(0.3F).temperature(0.8F).downfall(0.4F).specialEffects((new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings(hills_generation.build()).build();
        event.getRegistry().register(hills.setRegistryName(MODID, "hills"));

        //Edge variant
        BiomeGenerationSettings.Builder edge_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.END);
        DefaultBiomeFeatures.addDefaultCarvers(edge_generation);
        DefaultBiomeFeatures.addDefaultLakes(edge_generation);

        Biome edge = (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.PLAINS).depth(0F).scale(0F).temperature(0.8F).downfall(0F).specialEffects((new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings(edge_generation.build()).build();
        event.getRegistry().register(edge.setRegistryName(MODID, "edge"));

        //River variant
        BiomeGenerationSettings.Builder river_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.END);
        DefaultBiomeFeatures.addDefaultCarvers(river_generation);
        DefaultBiomeFeatures.addDefaultLakes(river_generation);
        DefaultBiomeFeatures.addDefaultSprings(river_generation);

        Biome river = (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.PLAINS).depth(-0.5F).scale(0F).temperature(0.8F).downfall(0F).specialEffects((new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings(river_generation.build()).build();
        event.getRegistry().register(river.setRegistryName(MODID, "river"));


        //Shore variant
        BiomeGenerationSettings.Builder shore_generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.SOUL_SAND_VALLEY);
        DefaultBiomeFeatures.addDefaultCarvers(shore_generation);
        DefaultBiomeFeatures.addDefaultLakes(shore_generation);

        Biome shore = (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.PLAINS).depth(0f).scale(0F).temperature(0.5F).downfall(0F).specialEffects((new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(0).ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings(shore_generation.build()).build();
        event.getRegistry().register(shore.setRegistryName(MODID, "shore"));

    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(this::addBiomesToGenerator);
    }

    private void addBiomesToGenerator()
    {
        //Add only the main biome entry
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MAIN_KEY, 10));
        //Tell the BiomeManager all biomes that might spawn
        BiomeManager.addAdditionalOverworldBiomes(MAIN_KEY);
        BiomeManager.addAdditionalOverworldBiomes(HILLS_KEY);
        BiomeManager.addAdditionalOverworldBiomes(EDGE_KEY);
        BiomeManager.addAdditionalOverworldBiomes(RIVER_KEY);
        BiomeManager.addAdditionalOverworldBiomes(SHORE_KEY);

        //Register our biome layer modifier
        BiomeManager.addBiomeLayerModifier(new BiomeManager.IBiomeLayerModifier() {

            @Override
            public Optional<RegistryKey<Biome>> getHillsBiome(RegistryKey<Biome> biome)
            {
                return biome.equals(MAIN_KEY) ? Optional.of(HILLS_KEY) : Optional.empty();
            }

            @Override
            public Optional<RegistryKey<Biome>> getEdgeBiome(RegistryKey<Biome> biome, RegistryKey<Biome> north, RegistryKey<Biome> west, RegistryKey<Biome> south, RegistryKey<Biome> east)
            {
                if(biome.equals(MAIN_KEY))
                {
                    if(!north.equals(MAIN_KEY) || !west.equals(MAIN_KEY) || !south.equals(MAIN_KEY) || !east.equals(MAIN_KEY))
                    {
                        //If any of the adjacent biomes is not the core biome, we are at the biome edge
                        return Optional.of(EDGE_KEY);
                    }
                }
                return Optional.empty();
            }

            @Override
            public Optional<RegistryKey<Biome>> getRiverBiome(RegistryKey<Biome> biome)
            {
                return biome.equals(MAIN_KEY) ? Optional.of(RIVER_KEY) : Optional.empty();
            }

            @Override
            public Optional<RegistryKey<Biome>> getShoreBiome(RegistryKey<Biome> biome, RegistryKey<Biome> north, RegistryKey<Biome> west, RegistryKey<Biome> south, RegistryKey<Biome> east)
            {
                if(biome == MAIN_KEY && (BiomeDictionary.hasType(north, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(west, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(south, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(east, BiomeDictionary.Type.OCEAN)))
                {
                    //If any adjacent biome is of ocean type, we want a shore
                    return Optional.of(SHORE_KEY);
                }
                return Optional.empty();
            }
        });
    }
}