/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.common.Tags.Biomes.*;

@ApiStatus.Internal
public final class ForgeBiomeTagsProvider extends BiomeTagsProvider {
    public ForgeBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(NO_DEFAULT_MONSTERS).add(Biomes.MUSHROOM_FIELDS).add(Biomes.DEEP_DARK);
        tag(HIDDEN_FROM_LOCATOR_SELECTION); // Create tag file for visibility

        tag(IS_VOID).add(Biomes.THE_VOID);

        tag(IS_END).addTags(BiomeTags.IS_END);
        tag(IS_NETHER).addTags(BiomeTags.IS_NETHER);
        tag(IS_OVERWORLD).addTags(BiomeTags.IS_OVERWORLD);

        tag(IS_HOT_OVERWORLD)
                .add(Biomes.SWAMP)
                .add(Biomes.MANGROVE_SWAMP)
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
        tag(IS_HOT).addTag(IS_HOT_OVERWORLD).addTag(IS_HOT_NETHER).addOptionalTag(IS_HOT_END.location());

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
        tag(IS_COLD).addTag(IS_COLD_OVERWORLD).addOptionalTag(IS_COLD_NETHER.location()).addTag(IS_COLD_END);

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
        tag(IS_SPARSE_VEGETATION).addTag(IS_SPARSE_VEGETATION_OVERWORLD).addOptionalTag(IS_SPARSE_VEGETATION_NETHER.location()).addOptionalTag(IS_SPARSE_VEGETATION_END.location());

        tag(IS_DENSE_VEGETATION_OVERWORLD)
                .add(Biomes.DARK_FOREST)
                .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
                .add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.MANGROVE_SWAMP);
        tag(IS_DENSE_VEGETATION_NETHER);
        tag(IS_DENSE_VEGETATION_END);
        tag(IS_DENSE_VEGETATION).addTag(IS_DENSE_VEGETATION_OVERWORLD).addOptionalTag(IS_DENSE_VEGETATION_NETHER.location()).addOptionalTag(IS_DENSE_VEGETATION_END.location());

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
        tag(IS_WET).addTag(IS_WET_OVERWORLD).addOptionalTag(IS_WET_NETHER.location()).addOptionalTag(IS_WET_END.location());

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
        tag(IS_DRY).addTag(IS_DRY_OVERWORLD).addTag(IS_DRY_NETHER).addTag(IS_DRY_END);

        tag(IS_CONIFEROUS_TREE).addTags(IS_TAIGA).add(Biomes.GROVE);
        tag(IS_SAVANNA_TREE).addTags(IS_SAVANNA);
        tag(IS_JUNGLE_TREE).addTags(IS_JUNGLE);
        tag(IS_DECIDUOUS_TREE).add(Biomes.FOREST).add(Biomes.FLOWER_FOREST).add(Biomes.BIRCH_FOREST).add(Biomes.DARK_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.WINDSWEPT_FOREST);

        tag(IS_MOUNTAIN_SLOPE).add(Biomes.SNOWY_SLOPES).add(Biomes.MEADOW).add(Biomes.GROVE).add(Biomes.CHERRY_GROVE);
        tag(IS_MOUNTAIN_PEAK).add(Biomes.JAGGED_PEAKS).add(Biomes.FROZEN_PEAKS).add(Biomes.STONY_PEAKS);
        tag(IS_MOUNTAIN).addTag(BiomeTags.IS_MOUNTAIN).addTag(IS_MOUNTAIN_PEAK).addTag(IS_MOUNTAIN_SLOPE);

        tag(IS_FOREST).addTags(BiomeTags.IS_FOREST);
        tag(IS_BIRCH_FOREST).add(Biomes.BIRCH_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST);
        tag(IS_FLOWER_FOREST).add(Biomes.FLOWER_FOREST);
        tag(IS_FLORAL).addTags(IS_FLOWER_FOREST).add(Biomes.SUNFLOWER_PLAINS).add(Biomes.CHERRY_GROVE).add(Biomes.MEADOW);
        tag(IS_BEACH).addTags(BiomeTags.IS_BEACH);
        tag(IS_STONY_SHORES).add(Biomes.STONY_SHORE);
        tag(IS_DESERT).add(Biomes.DESERT);
        tag(IS_BADLANDS).addTags(BiomeTags.IS_BADLANDS);
        tag(IS_PLAINS).add(Biomes.PLAINS).add(Biomes.SUNFLOWER_PLAINS);
        tag(IS_SNOWY_PLAINS).add(Biomes.SNOWY_PLAINS);
        tag(IS_TAIGA).addTags(BiomeTags.IS_TAIGA);
        tag(IS_HILL).addTags(BiomeTags.IS_HILL);
        tag(IS_WINDSWEPT).add(Biomes.WINDSWEPT_HILLS).add(Biomes.WINDSWEPT_GRAVELLY_HILLS).add(Biomes.WINDSWEPT_FOREST).add(Biomes.WINDSWEPT_SAVANNA);
        tag(IS_SAVANNA).addTags(BiomeTags.IS_SAVANNA);
        tag(IS_JUNGLE).addTags(BiomeTags.IS_JUNGLE);
        tag(IS_SNOWY).add(Biomes.SNOWY_BEACH).add(Biomes.SNOWY_PLAINS).add(Biomes.ICE_SPIKES).add(Biomes.SNOWY_TAIGA).add(Biomes.GROVE).add(Biomes.SNOWY_SLOPES).add(Biomes.JAGGED_PEAKS).add(Biomes.FROZEN_PEAKS);
        tag(IS_ICY).add(Biomes.ICE_SPIKES).add(Biomes.FROZEN_PEAKS);
        tag(IS_SWAMP).add(Biomes.SWAMP).add(Biomes.MANGROVE_SWAMP);
        tag(IS_OLD_GROWTH).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.OLD_GROWTH_PINE_TAIGA).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        tag(IS_LUSH).add(Biomes.LUSH_CAVES);
        tag(IS_SANDY).add(Biomes.DESERT).add(Biomes.BADLANDS).add(Biomes.WOODED_BADLANDS).add(Biomes.ERODED_BADLANDS).add(Biomes.BEACH);
        tag(IS_MUSHROOM).add(Biomes.MUSHROOM_FIELDS);
        tag(IS_PLATEAU).add(Biomes.WOODED_BADLANDS).add(Biomes.SAVANNA_PLATEAU).add(Biomes.CHERRY_GROVE).add(Biomes.MEADOW);
        tag(IS_SPOOKY).add(Biomes.DARK_FOREST).add(Biomes.DEEP_DARK);
        tag(IS_WASTELAND);
        tag(IS_RARE).add(Biomes.SUNFLOWER_PLAINS).add(Biomes.FLOWER_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA).add(Biomes.BAMBOO_JUNGLE).add(Biomes.SPARSE_JUNGLE).add(Biomes.ERODED_BADLANDS).add(Biomes.SAVANNA_PLATEAU).add(Biomes.WINDSWEPT_SAVANNA).add(Biomes.ICE_SPIKES).add(Biomes.WINDSWEPT_GRAVELLY_HILLS).add(Biomes.MUSHROOM_FIELDS).add(Biomes.DEEP_DARK);

        tag(IS_RIVER).addTags(BiomeTags.IS_RIVER);
        tag(IS_SHALLOW_OCEAN).add(Biomes.OCEAN).add(Biomes.LUKEWARM_OCEAN).add(Biomes.WARM_OCEAN).add(Biomes.COLD_OCEAN).add(Biomes.FROZEN_OCEAN);
        tag(IS_DEEP_OCEAN).addTags(BiomeTags.IS_DEEP_OCEAN);
        tag(IS_OCEAN).addTags(BiomeTags.IS_OCEAN).addTags(IS_SHALLOW_OCEAN).addTags(IS_DEEP_OCEAN);
        tag(IS_AQUATIC_ICY).add(Biomes.FROZEN_RIVER).add(Biomes.DEEP_FROZEN_OCEAN).add(Biomes.FROZEN_OCEAN);
        tag(IS_AQUATIC).addTag(IS_OCEAN).addTag(IS_RIVER);

        tag(IS_CAVE).add(Biomes.LUSH_CAVES).add(Biomes.DRIPSTONE_CAVES).add(Biomes.DEEP_DARK);
        tag(IS_UNDERGROUND).addTag(IS_CAVE);

        tag(IS_NETHER_FOREST).add(Biomes.CRIMSON_FOREST).add(Biomes.WARPED_FOREST);
        tag(IS_OUTER_END_ISLAND).add(Biomes.END_HIGHLANDS).add(Biomes.END_MIDLANDS).add(Biomes.END_BARRENS);

        // Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
        // TODO: Remove backwards compat tag entries in 1.22
        tag(IS_MOUNTAIN_SLOPE).addOptionalTag(forgeRl("is_slope"));
        tag(IS_MOUNTAIN_PEAK).addOptionalTag(forgeRl("is_peak"));
        tagWithOptionalLegacy(IS_MOUNTAIN);
        tagWithOptionalLegacy(IS_HOT_OVERWORLD);
        tagWithOptionalLegacy(IS_HOT_NETHER);
        tagWithOptionalLegacy(IS_HOT_END);
        tagWithOptionalLegacy(IS_HOT);
        tagWithOptionalLegacy(IS_COLD_OVERWORLD);
        tagWithOptionalLegacy(IS_COLD_NETHER);
        tagWithOptionalLegacy(IS_COLD_END);
        tagWithOptionalLegacy(IS_COLD);
        tagWithOptionalLegacy(IS_SPARSE_VEGETATION_OVERWORLD);
        tagWithOptionalLegacy(IS_SPARSE_VEGETATION_NETHER);
        tagWithOptionalLegacy(IS_SPARSE_VEGETATION_END);
        tagWithOptionalLegacy(IS_SPARSE_VEGETATION);
        tag(IS_SPARSE_VEGETATION_OVERWORLD).addOptionalTag(forgeRl("is_sparse/overworld"));
        tag(IS_SPARSE_VEGETATION_NETHER).addOptionalTag(forgeRl("is_sparse/nether"));
        tag(IS_SPARSE_VEGETATION_END).addOptionalTag(forgeRl("is_sparse/end"));
        tag(IS_SPARSE_VEGETATION).addOptionalTag(forgeRl("is_sparse"));
        tag(IS_DENSE_VEGETATION_OVERWORLD).addOptionalTag(forgeRl("is_dense/overworld"));
        tag(IS_DENSE_VEGETATION_NETHER).addOptionalTag(forgeRl("is_dense/nether"));
        tag(IS_DENSE_VEGETATION_END).addOptionalTag(forgeRl("is_dense/end"));
        tag(IS_DENSE_VEGETATION).addOptionalTag(forgeRl("is_dense"));
        tagWithOptionalLegacy(IS_WET_OVERWORLD);
        tagWithOptionalLegacy(IS_WET_NETHER);
        tagWithOptionalLegacy(IS_WET_END);
        tagWithOptionalLegacy(IS_WET);
        tagWithOptionalLegacy(IS_DRY_OVERWORLD);
        tagWithOptionalLegacy(IS_DRY_NETHER);
        tagWithOptionalLegacy(IS_DRY_END);
        tagWithOptionalLegacy(IS_DRY);
        tagWithOptionalLegacy(IS_CONIFEROUS_TREE);
        tagWithOptionalLegacy(IS_SPOOKY);
        tagWithOptionalLegacy(IS_DEAD);
        tagWithOptionalLegacy(IS_LUSH);
        tagWithOptionalLegacy(IS_MUSHROOM);
        tagWithOptionalLegacy(IS_MAGICAL);
        tagWithOptionalLegacy(IS_RARE);
        tagWithOptionalLegacy(IS_PLATEAU);
        tagWithOptionalLegacy(IS_MODIFIED);
        tagWithOptionalLegacy(IS_FLORAL);
        tag(IS_AQUATIC).addOptionalTag(forgeRl("is_water"));
        tagWithOptionalLegacy(IS_DESERT);
        tagWithOptionalLegacy(IS_PLAINS);
        tagWithOptionalLegacy(IS_SWAMP);
        tagWithOptionalLegacy(IS_SANDY);
        tagWithOptionalLegacy(IS_SNOWY);
        tagWithOptionalLegacy(IS_WASTELAND);
        tagWithOptionalLegacy(IS_VOID);
        tagWithOptionalLegacy(IS_CAVE);
        tagWithOptionalLegacy(IS_END);
        tagWithOptionalLegacy(IS_NETHER);
        tagWithOptionalLegacy(IS_OVERWORLD);
    }

    @SafeVarargs
    private void tag(ResourceKey<Biome> biome, TagKey<Biome>... tags) {
        for (TagKey<Biome> key : tags) {
            tag(key).add(biome);
        }
    }

    private TagAppender<Biome> tagWithOptionalLegacy(TagKey<Biome> tag) {
        return tag(tag).addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", tag.location().getPath()));
    }

    private static ResourceLocation forgeRl(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
    }

    @Override
    public String getName() {
        return "Forge Biome Tags";
    }
}
