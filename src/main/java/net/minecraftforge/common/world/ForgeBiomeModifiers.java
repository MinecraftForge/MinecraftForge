/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeBiomeModifiers
{
    private ForgeBiomeModifiers() {} // Utility class.
    
    /**
     * <p>Stock biome modifier that adds features to biomes. Has the following json format:</p>
     * <pre>
     * {
     *   "type": "forge:add_features", // required
     *   "biomes": "#namespace:your_biome_tag" // accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "features": "namespace:your_feature", // accepts a placed feature id, [list of placed feature ids], or #namespace:feature_tag
     *   "step": "underground_ores" // accepts a Decoration enum name
     * }
     * </pre>
     * <p>Be wary of using this to add vanilla PlacedFeatures to biomes, as doing so may cause a feature cycle violation.</p>
     * 
     * @param biomes Biomes to add features to.
     * @param features PlacedFeatures to add to biomes.
     * @param step Decoration step to run features in.
     */
    public static record AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Decoration step) implements BiomeModifier
    {
        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.ADD && this.biomes.contains(biome))
            {
                BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
                this.features.forEach(holder -> generationSettings.addFeature(this.step, holder));
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return ForgeMod.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
        }
    }

    
    /**
     * <p>Stock biome modifier that removes features from biomes. Has the following json format:</p>
     * <pre>
     * {
     *   "type": "forge:removefeatures", // required
     *   "biomes": "#namespace:your_biome_tag", // accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "features": "namespace:your_feature", // accepts a placed feature id, [list of placed feature ids], or #namespace:feature_tag
     *   "steps": "underground_ores" OR ["underground_ores", "vegetal_decoration"] // one or more decoration steps; optional field, defaults to all steps if not specified
     * }
     * </pre>
     * 
     * @param biomes Biomes to remove features from.
     * @param features PlacedFeatures to remove from biomes.
     * @param steps Decoration steps to remove features from.
     */
    public static record RemoveFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<Decoration> steps) implements BiomeModifier
    {
        /**
         * Creates a modifier that removes the given features from all decoration steps in the given biomes.
         * @param biomes Biomes to remove features from.
         * @param features PlacedFeatures to remove from biomes.
         */
        public static RemoveFeaturesBiomeModifier allSteps(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features)
        {
            return new RemoveFeaturesBiomeModifier(biomes, features, EnumSet.allOf(Decoration.class));
        }
        
        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.REMOVE && this.biomes.contains(biome))
            {
                BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
                for (Decoration step : this.steps)
                {
                    generationSettings.getFeatures(step).removeIf(this.features::contains);
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return ForgeMod.REMOVE_FEATURES_BIOME_MODIFIER_TYPE.get();
        }
    }
    
    /**
     * <p>Stock biome modifier that adds a mob spawn to a biome. Has the following json format:</p>
     * <pre>
     * {
     *   "type": "forge:add_spawns", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "spawners":
     *   {
     *     "type": "namespace:entity_type", // Type of mob to spawn
     *     "weight": 100, // int, spawn weighting
     *     "minCount": 1, // int, minimum pack size
     *     "maxCount": 4, // int, maximum pack size
     *   }
     * }
     * </pre>
     * <p>Optionally accepts a list of spawner objects instead of a single spawner:</p>
     * <pre>
     * {
     *   "type": "forge:add_spawns", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "spawners":
     *   [
     *     {
     *       "type": "namespace:entity_type", // Type of mob to spawn
     *       "weight": 100, // int, spawn weighting
     *       "minCount": 1, // int, minimum pack size
     *       "maxCount": 4, // int, maximum pack size
     *     },
     *     {
     *       // additional spawner object
     *     }
     *   ]
     * }
     * </pre>
     * 
     * @param biomes Biomes to add mob spawns to.
     * @param spawners List of SpawnerDatas specifying EntityType, weight, and pack size.
     */
    public record AddSpawnsBiomeModifier(HolderSet<Biome> biomes, List<SpawnerData> spawners) implements BiomeModifier
    {
        /**
         * Convenience method for using a single spawn data.
         * @param biomes Biomes to add mob spawns to.
         * @param spawner SpawnerData specifying EntityTYpe, weight, and pack size.
         * @return AddSpawnsBiomeModifier that adds a single spawn entry to the specified biomes.
         */
        public static AddSpawnsBiomeModifier singleSpawn(HolderSet<Biome> biomes, SpawnerData spawner)
        {
            return new AddSpawnsBiomeModifier(biomes, List.of(spawner));
        }
        
        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.ADD && this.biomes.contains(biome))
            {
                MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
                for (SpawnerData spawner : this.spawners)
                {
                    EntityType<?> type = spawner.type;
                    spawns.addSpawn(type.getCategory(), spawner);
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return ForgeMod.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that removes mob spawns from a biome. Has the following json format:</p>
     * <pre>
     * {
     *   "type": "forge:add_spawns", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "entity_types": #namespace:entitytype_tag // Accepts an entity type, [list of entity types], or #namespace:entitytype_tag
     * }
     * </pre>
     * 
     * @param biomes Biomes to add mob spawns to.
     * @param entityTypes EntityTypes to remove from spawn lists.
     */
    public record RemoveSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements BiomeModifier
    {
        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.REMOVE && this.biomes.contains(biome))
            {
                MobSpawnSettingsBuilder spawnBuilder = builder.getMobSpawnSettings();
                for (MobCategory category : MobCategory.values())
                {
                    List<SpawnerData> spawns = spawnBuilder.getSpawner(category);
                    spawns.removeIf(spawnerData -> this.entityTypes.contains(ForgeRegistries.ENTITY_TYPES.getHolder(spawnerData.type).get()));
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return ForgeMod.REMOVE_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }
    }
}
