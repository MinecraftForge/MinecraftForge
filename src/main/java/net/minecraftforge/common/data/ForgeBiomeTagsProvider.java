/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;

public final class ForgeBiomeTagsProvider extends BiomeTagsProvider
{

    public ForgeBiomeTagsProvider(DataGenerator arg, ExistingFileHelper existingFileHelper)
    {
        super(arg, "forge", existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        tag(Biomes.OCEAN, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.PLAINS, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DESERT, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.WINDSWEPT_HILLS, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.FOREST, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.TAIGA, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SWAMP, Tags.Biomes.IS_WET, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_SWAMP, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.RIVER, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.NETHER_WASTES, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.THE_END, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_END, Tags.Biomes.IS_END);
        tag(Biomes.FROZEN_OCEAN, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.FROZEN_RIVER, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SNOWY_PLAINS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_WASTELAND, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.MUSHROOM_FIELDS, Tags.Biomes.IS_MUSHROOM, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.BEACH, Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.JUNGLE, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_DENSE, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SPARSE_JUNGLE, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET, Tags.Biomes.IS_WET, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DEEP_OCEAN, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.STONY_SHORE, Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SNOWY_BEACH, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_BEACH, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.BIRCH_FOREST, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DARK_FOREST, Tags.Biomes.IS_SPOOKY, Tags.Biomes.IS_DENSE, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SNOWY_TAIGA, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.OLD_GROWTH_PINE_TAIGA, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.WINDSWEPT_FOREST, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SAVANNA, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SAVANNA, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SAVANNA_PLATEAU, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SAVANNA, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU);
        tag(Biomes.BADLANDS, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.WOODED_BADLANDS, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_OVERWORLD, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU);
        tag(Biomes.MEADOW, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_PLATEAU, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.GROVE, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SNOWY_SLOPES, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.JAGGED_PEAKS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.FROZEN_PEAKS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.STONY_PEAKS, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SMALL_END_ISLANDS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_END, Tags.Biomes.IS_END);
        tag(Biomes.END_MIDLANDS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_END, Tags.Biomes.IS_END);
        tag(Biomes.END_HIGHLANDS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_END, Tags.Biomes.IS_END);
        tag(Biomes.END_BARRENS, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_END, Tags.Biomes.IS_END);
        tag(Biomes.WARM_OCEAN, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.LUKEWARM_OCEAN, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.COLD_OCEAN, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DEEP_LUKEWARM_OCEAN, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DEEP_COLD_OCEAN, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DEEP_FROZEN_OCEAN, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.THE_VOID, Tags.Biomes.IS_VOID);
        tag(Biomes.SUNFLOWER_PLAINS, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.WINDSWEPT_GRAVELLY_HILLS, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.FLOWER_FOREST, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.ICE_SPIKES, Tags.Biomes.IS_COLD, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.OLD_GROWTH_BIRCH_FOREST, Tags.Biomes.IS_DENSE, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.OLD_GROWTH_SPRUCE_TAIGA, Tags.Biomes.IS_DENSE, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.WINDSWEPT_SAVANNA, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SAVANNA, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.ERODED_BADLANDS, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.BAMBOO_JUNGLE, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.LUSH_CAVES, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_LUSH, Tags.Biomes.IS_WET, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.DRIPSTONE_CAVES, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_OVERWORLD);
        tag(Biomes.SOUL_SAND_VALLEY, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.CRIMSON_FOREST, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.WARPED_FOREST, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.BASALT_DELTAS, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY, Tags.Biomes.IS_DRY_NETHER);

        tag(Tags.Biomes.IS_WATER).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER);
        tag(BiomeTags.IS_MOUNTAIN).addTag(Tags.Biomes.IS_PEAK).addTag(Tags.Biomes.IS_SLOPE);
    }

    @SafeVarargs
    private void tag(ResourceKey<Biome> biome, TagKey<Biome>... tags)
    {
        for(TagKey<Biome> key : tags)
        {
            tag(key).add(biome);
        }
    }

    @Override
    public String getName() {
        return "Forge Biome Tags";
    }
}
