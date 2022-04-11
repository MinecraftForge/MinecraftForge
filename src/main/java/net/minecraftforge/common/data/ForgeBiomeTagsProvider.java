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
package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;

import java.util.List;

public final class ForgeBiomeTagsProvider extends BiomeTagsProvider
{

    public ForgeBiomeTagsProvider(DataGenerator arg, ExistingFileHelper existingFileHelper)
    {
        super(arg, "forge", existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        tag(Biomes.OCEAN, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.PLAINS, List.of(Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DESERT, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.WINDSWEPT_HILLS, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.FOREST, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.TAIGA, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SWAMP, List.of(Tags.Biomes.IS_WET, Tags.Biomes.IS_SWAMP, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.RIVER, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.NETHER_WASTES, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY));
        tag(Biomes.THE_END, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_DRY, Tags.Biomes.IS_END));
        tag(Biomes.FROZEN_OCEAN, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.FROZEN_RIVER, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SNOWY_PLAINS, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_WASTELAND, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.MUSHROOM_FIELDS, List.of(Tags.Biomes.IS_MUSHROOM, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.BEACH, List.of(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.JUNGLE, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_WET, Tags.Biomes.IS_DENSE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SPARSE_JUNGLE, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_WET, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DEEP_OCEAN, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.STONY_SHORE, List.of(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SNOWY_BEACH, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_BEACH, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.BIRCH_FOREST, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DARK_FOREST, List.of(Tags.Biomes.IS_SPOOKY, Tags.Biomes.IS_DENSE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SNOWY_TAIGA, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.OLD_GROWTH_PINE_TAIGA, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.WINDSWEPT_FOREST, List.of(Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SAVANNA, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_SAVANNA, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SAVANNA_PLATEAU, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_SAVANNA, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU));
        tag(Biomes.BADLANDS, List.of(Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.WOODED_BADLANDS, List.of(Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_OVERWORLD, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU));
        tag(Biomes.MEADOW, List.of(Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_PLATEAU, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.GROVE, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SNOWY_SLOPES, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.JAGGED_PEAKS, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.FROZEN_PEAKS, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.STONY_PEAKS, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SMALL_END_ISLANDS, List.of(Tags.Biomes.IS_END));
        tag(Biomes.END_MIDLANDS, List.of(Tags.Biomes.IS_END));
        tag(Biomes.END_HIGHLANDS, List.of(Tags.Biomes.IS_END));
        tag(Biomes.END_BARRENS, List.of(Tags.Biomes.IS_END));
        tag(Biomes.WARM_OCEAN, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.LUKEWARM_OCEAN, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.COLD_OCEAN, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DEEP_LUKEWARM_OCEAN, List.of(Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DEEP_COLD_OCEAN, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DEEP_FROZEN_OCEAN, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.THE_VOID, List.of(Tags.Biomes.IS_VOID));
        tag(Biomes.SUNFLOWER_PLAINS, List.of(Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.WINDSWEPT_GRAVELLY_HILLS, List.of(Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.FLOWER_FOREST, List.of(Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.ICE_SPIKES, List.of(Tags.Biomes.IS_COLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.OLD_GROWTH_BIRCH_FOREST, List.of(Tags.Biomes.IS_DENSE, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.OLD_GROWTH_SPRUCE_TAIGA, List.of(Tags.Biomes.IS_DENSE, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.WINDSWEPT_SAVANNA, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_SAVANNA, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.ERODED_BADLANDS, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.BAMBOO_JUNGLE, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_WET, Tags.Biomes.IS_RARE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.LUSH_CAVES, List.of(Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_LUSH, Tags.Biomes.IS_WET, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.DRIPSTONE_CAVES, List.of(Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_SPARSE, Tags.Biomes.IS_OVERWORLD));
        tag(Biomes.SOUL_SAND_VALLEY, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY));
        tag(Biomes.CRIMSON_FOREST, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY));
        tag(Biomes.WARPED_FOREST, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY));
        tag(Biomes.BASALT_DELTAS, List.of(Tags.Biomes.IS_HOT, Tags.Biomes.IS_DRY));
    }

    private void tag(ResourceKey<Biome> biome, List<TagKey<Biome>> tags)
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
