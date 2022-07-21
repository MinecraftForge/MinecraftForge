/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.nbt.Tag;
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
        tag(Biomes.PLAINS, Tags.Biomes.IS_PLAINS, Tags.BiomeSpecific.PLAINS);
        tag(Biomes.DESERT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SANDY, Tags.BiomeSpecific.DESERT);
        tag(Biomes.TAIGA, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS);
        tag(Biomes.SWAMP, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_SWAMP, Tags.BiomeSpecific.SWAMP);
        tag(Biomes.NETHER_WASTES, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.THE_END, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        tag(Biomes.FROZEN_OCEAN, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY);
        tag(Biomes.FROZEN_RIVER, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.BiomeSpecific.FROZEN_RIVER);
        tag(Biomes.SNOWY_PLAINS, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_WASTELAND, Tags.Biomes.IS_PLAINS, Tags.BiomeSpecific.SNOWY_PLAINS);
        tag(Biomes.MUSHROOM_FIELDS, Tags.Biomes.IS_MUSHROOM, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.MUSHROOM_FIELDS);
        tag(Biomes.JUNGLE, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.BiomeSpecific.JUNGLE);
        tag(Biomes.SPARSE_JUNGLE, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.SPARSE_JUNGLE);
        tag(Biomes.BEACH, Tags.BiomeSpecific.BEACH);
        tag(Biomes.SNOWY_BEACH, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.BiomeSpecific.SNOWY_BEACH);
        tag(Biomes.DARK_FOREST, Tags.Biomes.IS_SPOOKY, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.BiomeSpecific.DARK_FOREST);
        tag(Biomes.SNOWY_TAIGA, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.BiomeSpecific.SNOWY_TAIGA);
        tag(Biomes.OLD_GROWTH_PINE_TAIGA, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS);
        tag(Biomes.WINDSWEPT_FOREST, Tags.Biomes.IS_SPARSE_OVERWORLD);
        tag(Biomes.SAVANNA, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.BiomeSpecific.SAVANNA);
        tag(Biomes.SAVANNA_PLATEAU, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU, Tags.BiomeSpecific.SAVANNA_PLATEAU);
        tag(Biomes.BADLANDS, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.BiomeSpecific.BADLANDS);
        tag(Biomes.WOODED_BADLANDS, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU, Tags.BiomeSpecific.WOODED_BADLANDS);
        tag(Biomes.MEADOW, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_PLATEAU, Tags.Biomes.IS_SLOPE, Tags.BiomeSpecific.MEADOW);
        tag(Biomes.GROVE, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE, Tags.BiomeSpecific.GROVE);
        tag(Biomes.SNOWY_SLOPES, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE, Tags.BiomeSpecific.SNOWY_SLOPES);
        tag(Biomes.JAGGED_PEAKS, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK, Tags.BiomeSpecific.JAGGED_PEAKS);
        tag(Biomes.FROZEN_PEAKS, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK, Tags.BiomeSpecific.FROZEN_PEAKS);
        tag(Biomes.STONY_PEAKS, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_PEAK, Tags.BiomeSpecific.STONY_PEAKS);
        tag(Biomes.SMALL_END_ISLANDS, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        tag(Biomes.END_MIDLANDS, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        tag(Biomes.END_HIGHLANDS, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        tag(Biomes.END_BARRENS, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        tag(Biomes.WARM_OCEAN, Tags.Biomes.IS_HOT_OVERWORLD, Tags.BiomeSpecific.WARM_OCEAN);
        tag(Biomes.COLD_OCEAN, Tags.Biomes.IS_COLD_OVERWORLD, Tags.BiomeSpecific.COLD_OCEAN);
        tag(Biomes.DEEP_COLD_OCEAN, Tags.Biomes.IS_COLD_OVERWORLD, Tags.BiomeSpecific.DEEP_COLD_OCEAN);
        tag(Biomes.DEEP_FROZEN_OCEAN, Tags.Biomes.IS_COLD_OVERWORLD, Tags.BiomeSpecific.DEEP_FROZEN_OCEAN);
        tag(Biomes.THE_VOID, Tags.Biomes.IS_VOID);
        tag(Biomes.SUNFLOWER_PLAINS, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.SUNFLOWER_PLAINS);
        tag(Biomes.WINDSWEPT_GRAVELLY_HILLS, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE);
        tag(Biomes.FLOWER_FOREST, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.FLOWER_FOREST);
        tag(Biomes.ICE_SPIKES, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.ICE_SPIKES);
        tag(Biomes.FOREST, Tags.BiomeSpecific.FOREST);
        tag(Biomes.BIRCH_FOREST, Tags.BiomeSpecific.BIRCH_FOREST);
        tag(Biomes.OLD_GROWTH_BIRCH_FOREST, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_RARE);
        tag(Biomes.OLD_GROWTH_SPRUCE_TAIGA, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_RARE);
        tag(Biomes.WINDSWEPT_SAVANNA, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.WINDSWEPT_SAVANNA);
        tag(Biomes.ERODED_BADLANDS, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.ERODED_BADLANDS);
        tag(Biomes.BAMBOO_JUNGLE, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_RARE, Tags.BiomeSpecific.BAMBOO_JUNGLE);
        tag(Biomes.LUSH_CAVES, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_LUSH, Tags.Biomes.IS_WET_OVERWORLD, Tags.BiomeSpecific.LUSH_CAVES);
        tag(Biomes.DRIPSTONE_CAVES, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.BiomeSpecific.DRIPSTONE_CAVES);
        tag(Biomes.SOUL_SAND_VALLEY, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.CRIMSON_FOREST, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.WARPED_FOREST, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.BASALT_DELTAS, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        tag(Biomes.MANGROVE_SWAMP, Tags.Biomes.IS_WET, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_HOT, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SWAMP, Tags.BiomeSpecific.MANGROVE_SWAMP);
        tag(Biomes.DEEP_DARK, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_RARE, Tags.Biomes.IS_SPOOKY, Tags.BiomeSpecific.DEEP_DARK);

        tag(Biomes.OCEAN, Tags.BiomeSpecific.OCEAN);
        tag(Biomes.RIVER, Tags.BiomeSpecific.RIVER);
        tag(Biomes.LUKEWARM_OCEAN, Tags.BiomeSpecific.LUKEWARM_OCEAN);
        tag(Biomes.DEEP_OCEAN, Tags.BiomeSpecific.DEEP_OCEAN);
        tag(Biomes.FROZEN_OCEAN, Tags.BiomeSpecific.FROZEN_OCEAN);

        tag(Tags.Biomes.IS_HOT).addTag(Tags.Biomes.IS_HOT_OVERWORLD).addTag(Tags.Biomes.IS_HOT_NETHER).addOptionalTag(Tags.Biomes.IS_HOT_END.location());
        tag(Tags.Biomes.IS_COLD).addTag(Tags.Biomes.IS_COLD_OVERWORLD).addOptionalTag(Tags.Biomes.IS_COLD_NETHER.location()).addTag(Tags.Biomes.IS_COLD_END);
        tag(Tags.Biomes.IS_SPARSE).addTag(Tags.Biomes.IS_SPARSE_OVERWORLD).addOptionalTag(Tags.Biomes.IS_SPARSE_NETHER.location()).addOptionalTag(Tags.Biomes.IS_SPARSE_END.location());
        tag(Tags.Biomes.IS_DENSE).addTag(Tags.Biomes.IS_DENSE_OVERWORLD).addOptionalTag(Tags.Biomes.IS_DENSE_NETHER.location()).addOptionalTag(Tags.Biomes.IS_DENSE_END.location());
        tag(Tags.Biomes.IS_WET).addTag(Tags.Biomes.IS_WET_OVERWORLD).addOptionalTag(Tags.Biomes.IS_WET_NETHER.location()).addOptionalTag(Tags.Biomes.IS_WET_END.location());
        tag(Tags.Biomes.IS_DRY).addTag(Tags.Biomes.IS_DRY_OVERWORLD).addTag(Tags.Biomes.IS_DRY_NETHER).addTag(Tags.Biomes.IS_DRY_END);

        tag(Tags.Biomes.IS_WATER).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER);
        tag(Tags.Biomes.IS_MOUNTAIN).addTag(Tags.Biomes.IS_PEAK).addTag(Tags.Biomes.IS_SLOPE);
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
    public String getName()
    {
        return "Forge Biome Tags";
    }
}
