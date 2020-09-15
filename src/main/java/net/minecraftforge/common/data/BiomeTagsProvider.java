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

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class BiomeTagsProvider extends ForgeRegistryTagsProvider<Biome>
{

    public BiomeTagsProvider(DataGenerator gen, String modId, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(gen, ForgeRegistries.BIOMES, modId, existingFileHelper);
    }

    public BiomeTagsProvider(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
    {
        this(gen, "forge", existingFileHelper);
    }

    @Override
    protected void registerTags()
    {
        func_240522_a_(Tags.Biomes.WATER).addTags(Tags.Biomes.OCEAN, Tags.Biomes.RIVER);
        func_240522_a_(Tags.Biomes.HOT).add(Biomes.DESERT, Biomes.field_235254_j_, Biomes.field_235252_ay_, Biomes.field_235253_az_, Biomes.field_235250_aA_, Biomes.field_235251_aB_,
                Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE,
                Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DESERT_LAKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE,
                Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU,
                Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS);
        func_240522_a_(Tags.Biomes.COLD).add(Biomes.TAIGA, Biomes.THE_END, Biomes.SMALL_END_ISLANDS, Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS, Biomes.END_BARRENS,
                Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.TAIGA_HILLS, Biomes.SNOWY_BEACH, Biomes.SNOWY_TAIGA,
                Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.TAIGA_MOUNTAINS,
                Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA_MOUNTAINS);
        func_240522_a_(Tags.Biomes.SPARSE).add(Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.GRAVELLY_MOUNTAINS,
                Biomes.MODIFIED_JUNGLE_EDGE, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS,
                Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.DENSE).add(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.DARK_FOREST, Biomes.MODIFIED_JUNGLE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS,
                Biomes.DARK_FOREST_HILLS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS);
        func_240522_a_(Tags.Biomes.WET).add(Biomes.SWAMP, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.SWAMP_HILLS, Biomes.MODIFIED_JUNGLE,
                Biomes.MODIFIED_JUNGLE_EDGE, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS);
        func_240522_a_(Tags.Biomes.DRY).add(Biomes.DESERT, Biomes.field_235254_j_, Biomes.field_235252_ay_, Biomes.field_235253_az_, Biomes.field_235250_aA_, Biomes.field_235251_aB_,
                Biomes.THE_END, Biomes.SMALL_END_ISLANDS, Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS,
                Biomes.END_BARRENS, Biomes.DESERT_HILLS, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.DESERT_LAKES,
                Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.SAVANNA).add(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU);
        func_240522_a_(Tags.Biomes.CONIFEROUS).add(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA,
                Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.SNOWY_TAIGA_MOUNTAINS);
        func_240522_a_(Tags.Biomes.JUNGLE).add(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.BAMBOO_JUNGLE,
                Biomes.BAMBOO_JUNGLE_HILLS);
        func_240522_a_(Tags.Biomes.SPOOKY).add(Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS);
        func_240522_a_(Tags.Biomes.MUSHROOM).add(Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE);
        func_240522_a_(Tags.Biomes.RARE).add(Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.JUNGLE_EDGE, Biomes.SAVANNA_PLATEAU, Biomes.SUNFLOWER_PLAINS,
                Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE,
                Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA,
                Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS,
                Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.PLATEAU).add(Biomes.SAVANNA_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.SHATTERED_SAVANNA_PLATEAU,
                Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.MODIFIED).add(Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
                Biomes.MODIFIED_BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.OCEAN).add(Biomes.OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_OCEAN, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN,
                Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        func_240522_a_(Tags.Biomes.RIVER).add(Biomes.RIVER, Biomes.FROZEN_RIVER);
        func_240522_a_(Tags.Biomes.MESA).add(Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.FOREST).add(Biomes.FOREST, Biomes.TAIGA, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.JUNGLE_EDGE, Biomes.BIRCH_FOREST,
                Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS,
                Biomes.WOODED_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS,
                Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.field_235253_az_, Biomes.field_235250_aA_);
        func_240522_a_(Tags.Biomes.PLAINS).add(Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SUNFLOWER_PLAINS);
        func_240522_a_(Tags.Biomes.MOUNTAIN).add(Biomes.MOUNTAINS, Biomes.SNOWY_MOUNTAINS, Biomes.MOUNTAIN_EDGE, Biomes.WOODED_MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS,
                Biomes.TAIGA_MOUNTAINS, Biomes.MODIFIED_JUNGLE, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS,
                Biomes.SHATTERED_SAVANNA, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU);
        func_240522_a_(Tags.Biomes.HILLS).add(Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.JUNGLE_HILLS, Biomes.BIRCH_FOREST_HILLS,
                Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.FLOWER_FOREST, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE_EDGE,
                Biomes.TALL_BIRCH_FOREST, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.BAMBOO_JUNGLE_HILLS);
        func_240522_a_(Tags.Biomes.SWAMP).add(Biomes.SWAMP, Biomes.SWAMP_HILLS);
        func_240522_a_(Tags.Biomes.SANDY).add(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.DESERT_LAKES);
        func_240522_a_(Tags.Biomes.SNOWY).add(Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.SNOWY_BEACH, Biomes.SNOWY_TAIGA,
                Biomes.SNOWY_TAIGA_HILLS, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA_MOUNTAINS);
        func_240522_a_(Tags.Biomes.WASTELAND).add(Biomes.SNOWY_TUNDRA, Biomes.field_235254_j_);
        func_240522_a_(Tags.Biomes.BEACH).add(Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH);
        func_240522_a_(Tags.Biomes.VOID).add(Biomes.THE_VOID);
        func_240522_a_(Tags.Biomes.OVERWORLD).add(Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA,
                Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS,
                Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE,
                Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST,
                Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS,
                Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU,
                Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN,
                Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS,
                Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS,
                Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA,
                Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU,
                Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS);
        func_240522_a_(Tags.Biomes.NETHER).add(Biomes.field_235254_j_, Biomes.field_235252_ay_, Biomes.field_235253_az_, Biomes.field_235250_aA_, Biomes.field_235251_aB_);
        func_240522_a_(Tags.Biomes.END).add(Biomes.THE_END, Biomes.SMALL_END_ISLANDS, Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS, Biomes.END_BARRENS);
    }

    @Override
    public String getName()
    {
        return "Biome Tags";
    }
}