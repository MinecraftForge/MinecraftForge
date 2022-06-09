/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

/**
 * <p>Stock biome modifier that adds a mob spawn to a biome. Has the following json format:</p>
 * <pre>
 * {
 *   "type": "forge:add_spawn", // Required
 *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
 *   "spawner":
 *   {
 *     "type": "namespace:entity_type", // Type of mob to spawn
 *     "weight": 100, // int, spawn weighting
 *     "minCount": 1, // int, minimum pack size
 *     "maxCount": 4, // int, maximum pack size
 *   }
 * }
 * </pre>
 * 
 * @param biomes Biomes to add mob spawns to.
 * @param spawner SpawnerData specifying EntityType, weight, and pack size.
 */
public record AddSpawnBiomeModifier(HolderSet<Biome> biomes, SpawnerData spawner) implements BiomeModifier
{
    @Override
    public void modify(Holder<Biome> biome, Phase phase, Builder builder)
    {
        if (phase == Phase.ADD && this.biomes().contains(biome))
        {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
            EntityType<?> type = this.spawner.type;
            spawns.addSpawn(type.getCategory(), this.spawner);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec()
    {
        return ForgeMod.ADD_SPAWN_BIOME_MODIFIER_TYPE.get();
    }
}
