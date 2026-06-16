/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.common.Tags.Biomes.*;

@ApiStatus.Internal
public final class ForgeBiomeTagsProvider extends BiomeTagsProvider {
    public ForgeBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(NO_DEFAULT_MONSTERS).add(Biomes.MUSHROOM_FIELDS).add(Biomes.DEEP_DARK);
        tag(HIDDEN_FROM_LOCATOR_SELECTION); // Create tag file for visibility

        tag(IS_VOID).add(Biomes.THE_VOID);

        tag(IS_END).addTag(BiomeTags.IS_END);
        tag(IS_NETHER).addTag(BiomeTags.IS_NETHER);
        tag(IS_OVERWORLD).addTag(BiomeTags.IS_OVERWORLD);

        tag(IS_HOT_OVERWORLD)
                .add(Biomes.MUSHROOM_FIELDS)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.SPARSE_JUNGLE)
                .add(Biomes.DESERT)
                .add(Biomes.BADLANDS)
                .add(Biomes.WOODED_BADLANDS)
                .add(Biomes.ERODED_BADLANDS)
                .add(Biomes.SAVANNA)
                .add(Biomes.SAVANNA_PLATEAU)
                .add(Biomes.WINDSWEPT_SAVANNA)
                .add(Biomes.STONY_PEAKS)
                .add(Biomes.WARM_OCEAN);
        tag(IS_HOT_NETHER)
                .add(Biomes.NETHER_WASTES)
                .add(Biomes.CRIMSON_FOREST)
                .add(Biomes.WARPED_FOREST)
                .add(Biomes.SOUL_SAND_VALLEY)
                .add(Biomes.BASALT_DELTAS);
        tag(IS_HOT_END);
        tag(IS_HOT).addTags(IS_HOT_OVERWORLD, IS_HOT_NETHER, IS_HOT_END);

        tag(IS_COLD_OVERWORLD)
                .add(Biomes.TAIGA)
                .add(Biomes.OLD_GROWTH_PINE_TAIGA)
                .add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                .add(Biomes.WINDSWEPT_HILLS)
                .add(Biomes.WINDSWEPT_GRAVELLY_HILLS)
                .add(Biomes.WINDSWEPT_FOREST)
                .add(Biomes.SNOWY_PLAINS)
                .add(Biomes.ICE_SPIKES)
                .add(Biomes.GROVE)
                .add(Biomes.SNOWY_SLOPES)
                .add(Biomes.JAGGED_PEAKS)
                .add(Biomes.FROZEN_PEAKS)
                .add(Biomes.STONY_SHORE)
                .add(Biomes.SNOWY_BEACH)
                .add(Biomes.SNOWY_TAIGA)
                .add(Biomes.FROZEN_RIVER)
                .add(Biomes.COLD_OCEAN)
                .add(Biomes.FROZEN_OCEAN)
                .add(Biomes.DEEP_COLD_OCEAN)
                .add(Biomes.DEEP_FROZEN_OCEAN);
        tag(IS_COLD_NETHER);
        tag(IS_COLD_END)
                .add(Biomes.THE_END)
                .add(Biomes.SMALL_END_ISLANDS)
                .add(Biomes.END_MIDLANDS)
                .add(Biomes.END_HIGHLANDS)
                .add(Biomes.END_BARRENS);
        tag(IS_COLD).addTags(IS_COLD_OVERWORLD, IS_COLD_NETHER, IS_COLD_END);

        tag(IS_DARK_FOREST)
                .add(Biomes.DARK_FOREST)
                .add(Biomes.PALE_GARDEN);
        tag(IS_DEAD);

        tag(IS_SPARSE_VEGETATION_OVERWORLD)
                .add(Biomes.WOODED_BADLANDS)
                .add(Biomes.SAVANNA)
                .add(Biomes.SAVANNA_PLATEAU)
                .add(Biomes.SPARSE_JUNGLE)
                .add(Biomes.WINDSWEPT_SAVANNA)
                .add(Biomes.WINDSWEPT_FOREST)
                .add(Biomes.WINDSWEPT_HILLS)
                .add(Biomes.WINDSWEPT_GRAVELLY_HILLS)
                .add(Biomes.SNOWY_SLOPES)
                .add(Biomes.JAGGED_PEAKS)
                .add(Biomes.FROZEN_PEAKS);
        tag(IS_SPARSE_VEGETATION_NETHER);
        tag(IS_SPARSE_VEGETATION_END);
        tag(IS_SPARSE_VEGETATION).addTags(IS_SPARSE_VEGETATION_OVERWORLD, IS_SPARSE_VEGETATION_NETHER, IS_SPARSE_VEGETATION_END);

        tag(IS_DENSE_VEGETATION_OVERWORLD)
                .add(Biomes.DARK_FOREST)
                .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
                .add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.MANGROVE_SWAMP);
        tag(IS_DENSE_VEGETATION_NETHER);
        tag(IS_DENSE_VEGETATION_END);
        tag(IS_DENSE_VEGETATION).addTags(IS_DENSE_VEGETATION_OVERWORLD, IS_DENSE_VEGETATION_NETHER, IS_DENSE_VEGETATION_END);

        tag(IS_WET_OVERWORLD)
                .add(Biomes.SWAMP)
                .add(Biomes.MANGROVE_SWAMP)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.SPARSE_JUNGLE)
                .add(Biomes.BEACH)
                .add(Biomes.LUSH_CAVES)
                .add(Biomes.DRIPSTONE_CAVES);
        tag(IS_WET_NETHER);
        tag(IS_WET_END);
        tag(IS_WET).addTags(IS_WET_OVERWORLD, IS_WET_NETHER, IS_WET_END);

        tag(IS_DRY_OVERWORLD)
                .add(Biomes.DESERT)
                .add(Biomes.BADLANDS)
                .add(Biomes.WOODED_BADLANDS)
                .add(Biomes.ERODED_BADLANDS)
                .add(Biomes.SAVANNA)
                .add(Biomes.SAVANNA_PLATEAU)
                .add(Biomes.WINDSWEPT_SAVANNA);
        tag(IS_DRY_NETHER)
                .add(Biomes.NETHER_WASTES)
                .add(Biomes.CRIMSON_FOREST)
                .add(Biomes.WARPED_FOREST)
                .add(Biomes.SOUL_SAND_VALLEY)
                .add(Biomes.BASALT_DELTAS);
        tag(IS_DRY_END)
                .add(Biomes.THE_END)
                .add(Biomes.SMALL_END_ISLANDS)
                .add(Biomes.END_MIDLANDS)
                .add(Biomes.END_HIGHLANDS)
                .add(Biomes.END_BARRENS);
        tag(IS_DRY)
                .addTags(IS_DRY_OVERWORLD, IS_DRY_NETHER, IS_DRY_END);

        tag(IS_TEMPERATE_OVERWORLD)
                .add(Biomes.BEACH)
                .add(Biomes.BIRCH_FOREST)
                .add(Biomes.CHERRY_GROVE)
                .add(Biomes.DARK_FOREST)
                .add(Biomes.DEEP_OCEAN)
                .add(Biomes.FLOWER_FOREST)
                .add(Biomes.FOREST)
                .add(Biomes.MANGROVE_SWAMP)
                .add(Biomes.MEADOW)
                .add(Biomes.OCEAN)
                .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
                .add(Biomes.PLAINS)
                .add(Biomes.SUNFLOWER_PLAINS)
                .add(Biomes.SWAMP);
        tag(IS_TEMPERATE_NETHER);
        tag(IS_TEMPERATE_END);
        tag(IS_TEMPERATE)
                .addTag(IS_TEMPERATE_OVERWORLD)
                .addTag(IS_TEMPERATE_NETHER)
                .addTag(IS_TEMPERATE_END);

        tag(PRIMARY_WOOD_TYPE_ACACIA).add(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA);
        tag(PRIMARY_WOOD_TYPE_BAMBOO).add(Biomes.BAMBOO_JUNGLE);
        tag(PRIMARY_WOOD_TYPE_BIRCH).add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST);
        tag(PRIMARY_WOOD_TYPE_CHERRY).add(Biomes.CHERRY_GROVE);
        tag(PRIMARY_WOOD_TYPE_CRIMSON).add(Biomes.CRIMSON_FOREST);
        tag(PRIMARY_WOOD_TYPE_DARK_OAK).add(Biomes.DARK_FOREST);
        tag(PRIMARY_WOOD_TYPE_JUNGLE).add(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE);
        tag(PRIMARY_WOOD_TYPE_MANGROVE).add(Biomes.MANGROVE_SWAMP);
        tag(PRIMARY_WOOD_TYPE_OAK).add(Biomes.FLOWER_FOREST, Biomes.FOREST, Biomes.SWAMP, Biomes.WOODED_BADLANDS);
        tag(PRIMARY_WOOD_TYPE_PALE_OAK).add(Biomes.PALE_GARDEN);
        tag(PRIMARY_WOOD_TYPE_SPRUCE).add(Biomes.GROVE, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.SNOWY_TAIGA, Biomes.TAIGA);
        tag(PRIMARY_WOOD_TYPE_WARPED).add(Biomes.WARPED_FOREST);
        tag(PRIMARY_WOOD_TYPE)
                .addTags(
                        PRIMARY_WOOD_TYPE_ACACIA,
                        PRIMARY_WOOD_TYPE_BAMBOO,
                        PRIMARY_WOOD_TYPE_BIRCH,
                        PRIMARY_WOOD_TYPE_CHERRY,
                        PRIMARY_WOOD_TYPE_CRIMSON,
                        PRIMARY_WOOD_TYPE_DARK_OAK,
                        PRIMARY_WOOD_TYPE_JUNGLE,
                        PRIMARY_WOOD_TYPE_MANGROVE,
                        PRIMARY_WOOD_TYPE_OAK,
                        PRIMARY_WOOD_TYPE_PALE_OAK,
                        PRIMARY_WOOD_TYPE_SPRUCE,
                        PRIMARY_WOOD_TYPE_WARPED
                );

        tag(IS_CONIFEROUS_TREE)
                .addTag(IS_TAIGA)
                .add(Biomes.GROVE);
        tag(IS_SAVANNA_TREE).addTag(IS_SAVANNA);
        tag(IS_JUNGLE_TREE).addTag(IS_JUNGLE);
        tag(IS_DECIDUOUS_TREE).add(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.PALE_GARDEN, Biomes.WINDSWEPT_FOREST);

        tag(IS_MOUNTAIN_SLOPE).add(Biomes.SNOWY_SLOPES).add(Biomes.MEADOW).add(Biomes.GROVE).add(Biomes.CHERRY_GROVE);
        tag(IS_MOUNTAIN_PEAK)
                .add(Biomes.JAGGED_PEAKS).add(Biomes.FROZEN_PEAKS).add(Biomes.STONY_PEAKS);
        tag(IS_MOUNTAIN)
                .addTag(BiomeTags.IS_MOUNTAIN).addTag(IS_MOUNTAIN_PEAK).addTag(IS_MOUNTAIN_SLOPE);

        tag(IS_FOREST).addTag(BiomeTags.IS_FOREST);
        tag(IS_BIRCH_FOREST).add(Biomes.BIRCH_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST);
        tag(IS_FLOWER_FOREST).add(Biomes.FLOWER_FOREST);
        tag(IS_FLORAL).addTag(IS_FLOWER_FOREST).add(Biomes.SUNFLOWER_PLAINS).add(Biomes.CHERRY_GROVE).add(Biomes.MEADOW);
        tag(IS_BEACH).addTag(BiomeTags.IS_BEACH);
        tag(IS_STONY_SHORES).add(Biomes.STONY_SHORE);
        tag(IS_DESERT).add(Biomes.DESERT);
        tag(IS_BADLANDS).addTag(BiomeTags.IS_BADLANDS);
        tag(IS_PLAINS).add(Biomes.PLAINS).add(Biomes.SUNFLOWER_PLAINS);
        tag(IS_SNOWY_PLAINS).add(Biomes.SNOWY_PLAINS);
        tag(IS_TAIGA).addTag(BiomeTags.IS_TAIGA);
        tag(IS_HILL).addTag(BiomeTags.IS_HILL);
        tag(IS_WINDSWEPT).add(Biomes.WINDSWEPT_HILLS).add(Biomes.WINDSWEPT_GRAVELLY_HILLS).add(Biomes.WINDSWEPT_FOREST).add(Biomes.WINDSWEPT_SAVANNA);
        tag(IS_SAVANNA).addTag(BiomeTags.IS_SAVANNA);
        tag(IS_JUNGLE).addTag(BiomeTags.IS_JUNGLE);
        tag(IS_SNOWY)
            .add(Biomes.SNOWY_BEACH)
            .add(Biomes.SNOWY_PLAINS)
            .add(Biomes.ICE_SPIKES)
            .add(Biomes.SNOWY_TAIGA)
            .add(Biomes.GROVE)
            .add(Biomes.SNOWY_SLOPES)
            .add(Biomes.JAGGED_PEAKS)
            .add(Biomes.FROZEN_PEAKS);
        tag(IS_ICY).add(Biomes.ICE_SPIKES).add(Biomes.FROZEN_PEAKS);
        tag(IS_SWAMP).add(Biomes.SWAMP).add(Biomes.MANGROVE_SWAMP);
        tag(IS_OLD_GROWTH).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.OLD_GROWTH_PINE_TAIGA).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        tag(IS_LUSH).add(Biomes.LUSH_CAVES);
        tag(IS_MAGICAL);
        tag(IS_SANDY).add(Biomes.DESERT).add(Biomes.BADLANDS).add(Biomes.WOODED_BADLANDS).add(Biomes.ERODED_BADLANDS).add(Biomes.BEACH);
        tag(IS_MUSHROOM).add(Biomes.MUSHROOM_FIELDS);
        tag(IS_PLATEAU).add(Biomes.WOODED_BADLANDS).add(Biomes.SAVANNA_PLATEAU).add(Biomes.CHERRY_GROVE).add(Biomes.MEADOW);
        tag(IS_SPOOKY).add(Biomes.DARK_FOREST).add(Biomes.DEEP_DARK).add(Biomes.PALE_GARDEN);
        tag(IS_WASTELAND);
        tag(IS_RARE)
                .add(Biomes.SUNFLOWER_PLAINS)
                .add(Biomes.FLOWER_FOREST)
                .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
                .add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.SPARSE_JUNGLE)
                .add(Biomes.ERODED_BADLANDS)
                .add(Biomes.SAVANNA_PLATEAU)
                .add(Biomes.WINDSWEPT_SAVANNA)
                .add(Biomes.ICE_SPIKES)
                .add(Biomes.WINDSWEPT_GRAVELLY_HILLS)
                .add(Biomes.MUSHROOM_FIELDS)
                .add(Biomes.DEEP_DARK)
                .add(Biomes.PALE_GARDEN);

        tag(IS_RIVER).addTag(BiomeTags.IS_RIVER);
        tag(IS_SHALLOW_OCEAN).add(Biomes.OCEAN).add(Biomes.LUKEWARM_OCEAN).add(Biomes.WARM_OCEAN).add(Biomes.COLD_OCEAN).add(Biomes.FROZEN_OCEAN);
        tag(IS_DEEP_OCEAN).addTag(BiomeTags.IS_DEEP_OCEAN);
        tag(IS_OCEAN).addTag(BiomeTags.IS_OCEAN).addTag(IS_SHALLOW_OCEAN).addTag(IS_DEEP_OCEAN);
        tag(IS_AQUATIC_ICY).add(Biomes.FROZEN_RIVER).add(Biomes.DEEP_FROZEN_OCEAN).add(Biomes.FROZEN_OCEAN);
        tag(IS_AQUATIC).addTag(IS_OCEAN).addTag(IS_RIVER);

        tag(IS_CAVE)
                .add(Biomes.LUSH_CAVES).add(Biomes.DRIPSTONE_CAVES).add(Biomes.DEEP_DARK);
        tag(IS_UNDERGROUND).addTag(IS_CAVE);

        tag(IS_NETHER_FOREST).add(Biomes.CRIMSON_FOREST).add(Biomes.WARPED_FOREST);
        tag(IS_OUTER_END_ISLAND).add(Biomes.END_HIGHLANDS).add(Biomes.END_MIDLANDS).add(Biomes.END_BARRENS);
    }

    @Override
    public String getName() {
        return "Forge Biome Tags";
    }

}
