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
import net.minecraftforge.common.Tags;
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

        tag(IS_END).addTag(BiomeTags.IS_END);
        tag(IS_NETHER).addTag(BiomeTags.IS_NETHER);
        tag(IS_OVERWORLD).addTag(BiomeTags.IS_OVERWORLD);

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
                .add(Biomes.WARM_OCEAN)
                .addOptionalTag(forgeTagKey("is_hot/overworld"));
        tag(IS_HOT_NETHER)
                .add(Biomes.NETHER_WASTES)
                .add(Biomes.CRIMSON_FOREST)
                .add(Biomes.WARPED_FOREST)
                .add(Biomes.SOUL_SAND_VALLEY)
                .add(Biomes.BASALT_DELTAS)
                .addOptionalTag(forgeTagKey("is_hot/nether"));
        tag(IS_HOT_END); // forge:is_hot/end
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
                .add(Biomes.DEEP_FROZEN_OCEAN)
                .addOptionalTag(forgeTagKey("is_cold/overworld"));
        tag(IS_COLD_NETHER); // forge:is_cold/nether
        tag(IS_COLD_END)
                .add(Biomes.THE_END)
                .add(Biomes.SMALL_END_ISLANDS)
                .add(Biomes.END_MIDLANDS)
                .add(Biomes.END_HIGHLANDS)
                .add(Biomes.END_BARRENS)
                .addOptionalTag(forgeTagKey("is_cold/end"));
        tag(IS_COLD).addTag(IS_COLD_OVERWORLD).addOptionalTag(IS_COLD_NETHER.location()).addTag(IS_COLD_END);

        tag(IS_DEAD)
                .addOptionalTag(forgeTagKey("is_dead"));

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
                .add(Biomes.FROZEN_PEAKS)
                .addOptionalTag(forgeTagKey("is_sparse/overworld"));
        tag(IS_SPARSE_NETHER); // forge:is_sparse/nether
        tag(IS_SPARSE_END); // forge:is_sparse/end
        tag(IS_SPARSE_VEGETATION).addTag(IS_SPARSE_VEGETATION_OVERWORLD).addOptionalTag(IS_SPARSE_NETHER.location()).addOptionalTag(IS_SPARSE_END.location());

        tag(IS_DENSE_VEGETATION_OVERWORLD)
                .add(Biomes.DARK_FOREST)
                .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
                .add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.MANGROVE_SWAMP)
                .addOptionalTag(forgeTagKey("is_dense/overworld"));
        tag(IS_DENSE_NETHER);
        tag(IS_DENSE_END);
        tag(IS_DENSE_VEGETATION).addTag(IS_DENSE_VEGETATION_OVERWORLD).addOptionalTag(IS_DENSE_NETHER.location()).addOptionalTag(IS_DENSE_END.location());

        tag(IS_WET_OVERWORLD)
                .add(Biomes.SWAMP)
                .add(Biomes.MANGROVE_SWAMP)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.SPARSE_JUNGLE)
                .add(Biomes.BEACH)
                .add(Biomes.LUSH_CAVES)
                .add(Biomes.DRIPSTONE_CAVES)
                .addOptionalTag(forgeTagKey("is_wet/overworld"));
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
                .add(Biomes.WINDSWEPT_SAVANNA)
                .addOptionalTag(forgeTagKey("is_dry/overworld"));
        tag(IS_DRY_NETHER)
                .add(Biomes.NETHER_WASTES)
                .add(Biomes.CRIMSON_FOREST)
                .add(Biomes.WARPED_FOREST)
                .add(Biomes.SOUL_SAND_VALLEY)
                .add(Biomes.BASALT_DELTAS)
                .addOptionalTag(forgeTagKey("is_dry/nether"));
        tag(IS_DRY_END)
                .add(Biomes.THE_END)
                .add(Biomes.SMALL_END_ISLANDS)
                .add(Biomes.END_MIDLANDS)
                .add(Biomes.END_HIGHLANDS)
                .add(Biomes.END_BARRENS)
                .addOptionalTag(forgeTagKey("is_dry/end"));
        tag(IS_DRY)
                .addTag(IS_DRY_OVERWORLD).addTag(IS_DRY_NETHER).addTag(IS_DRY_END)
                .addOptionalTag(forgeTagKey("is_dry"));

        tag(IS_CONIFEROUS_TREE)
                .addTag(IS_TAIGA)
                .add(Biomes.GROVE)
                .addOptionalTag(forgeTagKey("is_coniferous"));
        tag(IS_SAVANNA_TREE).addTag(IS_SAVANNA);
        tag(IS_JUNGLE_TREE).addTag(IS_JUNGLE);
        tag(IS_DECIDUOUS_TREE).add(Biomes.FOREST).add(Biomes.FLOWER_FOREST).add(Biomes.BIRCH_FOREST).add(Biomes.DARK_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.WINDSWEPT_FOREST);

        tag(IS_MOUNTAIN_SLOPE).add(Biomes.SNOWY_SLOPES).add(Biomes.MEADOW).add(Biomes.GROVE).add(Biomes.CHERRY_GROVE);
        tag(IS_MOUNTAIN_PEAK)
                .add(Biomes.JAGGED_PEAKS).add(Biomes.FROZEN_PEAKS).add(Biomes.STONY_PEAKS)
                .addOptionalTag(forgeTagKey("is_peak"));
        tag(IS_MOUNTAIN)
                .addTag(BiomeTags.IS_MOUNTAIN).addTag(IS_MOUNTAIN_PEAK).addTag(IS_MOUNTAIN_SLOPE);
                //.addOptionalTag(forgeTagKey("is_mountain")); // can't because forge:is_mountain contains savanna plateau, while c:is_mountain does not

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
                .add(Biomes.SNOWY_BEACH).add(Biomes.SNOWY_PLAINS).add(Biomes.ICE_SPIKES).add(Biomes.SNOWY_TAIGA).add(Biomes.GROVE).add(Biomes.SNOWY_SLOPES).add(Biomes.JAGGED_PEAKS).add(Biomes.FROZEN_PEAKS)
                .addOptionalTag(forgeTagKey("is_snowy"));
        tag(IS_ICY).add(Biomes.ICE_SPIKES).add(Biomes.FROZEN_PEAKS);
        tag(IS_SWAMP).add(Biomes.SWAMP).add(Biomes.MANGROVE_SWAMP);
        tag(IS_OLD_GROWTH).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.OLD_GROWTH_PINE_TAIGA).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        tag(IS_LUSH).add(Biomes.LUSH_CAVES);
        tag(IS_MAGICAL);
        tag(IS_MODIFIED);
        tag(IS_SANDY).add(Biomes.DESERT).add(Biomes.BADLANDS).add(Biomes.WOODED_BADLANDS).add(Biomes.ERODED_BADLANDS).add(Biomes.BEACH);
        tag(IS_MUSHROOM).add(Biomes.MUSHROOM_FIELDS);
        tag(IS_PLATEAU).add(Biomes.WOODED_BADLANDS).add(Biomes.SAVANNA_PLATEAU).add(Biomes.CHERRY_GROVE).add(Biomes.MEADOW);
        tag(IS_SPOOKY).add(Biomes.DARK_FOREST).add(Biomes.DEEP_DARK);
        tag(IS_WASTELAND);
        tag(IS_RARE).add(Biomes.SUNFLOWER_PLAINS).add(Biomes.FLOWER_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA).add(Biomes.BAMBOO_JUNGLE).add(Biomes.SPARSE_JUNGLE).add(Biomes.ERODED_BADLANDS).add(Biomes.SAVANNA_PLATEAU).add(Biomes.WINDSWEPT_SAVANNA).add(Biomes.ICE_SPIKES).add(Biomes.WINDSWEPT_GRAVELLY_HILLS).add(Biomes.MUSHROOM_FIELDS).add(Biomes.DEEP_DARK);

        tag(IS_RIVER).addTag(BiomeTags.IS_RIVER);
        tag(IS_SHALLOW_OCEAN).add(Biomes.OCEAN).add(Biomes.LUKEWARM_OCEAN).add(Biomes.WARM_OCEAN).add(Biomes.COLD_OCEAN).add(Biomes.FROZEN_OCEAN);
        tag(IS_DEEP_OCEAN).addTag(BiomeTags.IS_DEEP_OCEAN);
        tag(IS_OCEAN).addTag(BiomeTags.IS_OCEAN).addTag(IS_SHALLOW_OCEAN).addTag(IS_DEEP_OCEAN);
        tag(IS_AQUATIC_ICY).add(Biomes.FROZEN_RIVER).add(Biomes.DEEP_FROZEN_OCEAN).add(Biomes.FROZEN_OCEAN);
        tag(IS_AQUATIC).addTag(IS_OCEAN).addTag(IS_RIVER);

        tag(IS_CAVE)
                .add(Biomes.LUSH_CAVES).add(Biomes.DRIPSTONE_CAVES).add(Biomes.DEEP_DARK)
                .addOptionalTag(forgeTagKey("is_cave"));
        tag(IS_UNDERGROUND).addTag(IS_CAVE);

        tag(IS_NETHER_FOREST).add(Biomes.CRIMSON_FOREST).add(Biomes.WARPED_FOREST);
        tag(IS_OUTER_END_ISLAND).add(Biomes.END_HIGHLANDS).add(Biomes.END_MIDLANDS).add(Biomes.END_BARRENS);

        // Backwards compat definitions for pre-1.21 legacy `forge:` tags.
        // TODO: Remove backwards compat tag entries in 1.22
        tag(Biomes.PLAINS, forgeTagKey("is_plains"));
        tag(Biomes.DESERT, forgeTagKey("is_hot/overworld"), forgeTagKey("is_dry/overworld"), forgeTagKey("is_sandy"), forgeTagKey("is_desert"));
        tag(Biomes.TAIGA, forgeTagKey("is_cold/overworld"), forgeTagKey("is_coniferous"));
        tag(Biomes.SWAMP, forgeTagKey("is_wet/overworld"), forgeTagKey("is_swamp"));
        tag(Biomes.NETHER_WASTES, forgeTagKey("is_hot/nether"), forgeTagKey("is_dry/nether"));
        tag(Biomes.THE_END, forgeTagKey("is_cold/end"), forgeTagKey("is_dry/end"));
        tag(Biomes.FROZEN_OCEAN, forgeTagKey("is_cold/overworld"), forgeTagKey("is_snowy"));
        tag(Biomes.FROZEN_RIVER, forgeTagKey("is_cold/overworld"), forgeTagKey("is_snowy"));
        tag(Biomes.SNOWY_PLAINS, forgeTagKey("is_cold/overworld"), forgeTagKey("is_snowy"), forgeTagKey("is_wasteland"), forgeTagKey("is_plains"));
        tag(Biomes.MUSHROOM_FIELDS, forgeTagKey("is_mushroom"), forgeTagKey("is_rare"));
        tag(Biomes.JUNGLE, forgeTagKey("is_hot/overworld"), forgeTagKey("is_wet/overworld"), forgeTagKey("is_dense/overworld"));
        tag(Biomes.SPARSE_JUNGLE, forgeTagKey("is_hot/overworld"), forgeTagKey("is_wet/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.BEACH, forgeTagKey("is_wet/overworld"), forgeTagKey("is_sandy"));
        tag(Biomes.SNOWY_BEACH, forgeTagKey("is_cold/overworld"), forgeTagKey("is_snowy"));
        tag(Biomes.DARK_FOREST, forgeTagKey("is_spooky"), forgeTagKey("is_dense/overworld"));
        tag(Biomes.SNOWY_TAIGA, forgeTagKey("is_cold/overworld"), forgeTagKey("is_coniferous"), forgeTagKey("is_snowy"));
        tag(Biomes.OLD_GROWTH_PINE_TAIGA, forgeTagKey("is_cold/overworld"), forgeTagKey("is_coniferous"));
        tag(Biomes.WINDSWEPT_FOREST, forgeTagKey("is_sparse/overworld"));
        tag(Biomes.SAVANNA, forgeTagKey("is_hot/overworld"), forgeTagKey("is_sparse/overworld"));
        tag(Biomes.SAVANNA_PLATEAU, forgeTagKey("is_hot/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_rare"), forgeTagKey("is_slope"), Tags.Biomes.IS_PLATEAU);
        tag(Biomes.BADLANDS, forgeTagKey("is_sandy"), forgeTagKey("is_dry/overworld"));
        tag(Biomes.WOODED_BADLANDS, forgeTagKey("is_sandy"), forgeTagKey("is_dry/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_slope"), Tags.Biomes.IS_PLATEAU);
        tag(Biomes.MEADOW, forgeTagKey("is_plains"), Tags.Biomes.IS_PLATEAU, forgeTagKey("is_slope"));
        tag(Biomes.GROVE, forgeTagKey("is_cold/overworld"), forgeTagKey("is_coniferous"), forgeTagKey("is_snowy"), forgeTagKey("is_slope"));
        tag(Biomes.SNOWY_SLOPES, forgeTagKey("is_cold/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_snowy"), forgeTagKey("is_slope"));
        tag(Biomes.JAGGED_PEAKS, forgeTagKey("is_cold/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_snowy"), forgeTagKey("is_peak"));
        tag(Biomes.FROZEN_PEAKS, forgeTagKey("is_cold/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_snowy"), forgeTagKey("is_peak"));
        tag(Biomes.STONY_PEAKS, forgeTagKey("is_hot/overworld"), forgeTagKey("is_peak"));
        tag(Biomes.SMALL_END_ISLANDS, forgeTagKey("is_cold/end"), forgeTagKey("is_dry/end"));
        tag(Biomes.END_MIDLANDS, forgeTagKey("is_cold/end"), forgeTagKey("is_dry/end"));
        tag(Biomes.END_HIGHLANDS, forgeTagKey("is_cold/end"), forgeTagKey("is_dry/end"));
        tag(Biomes.END_BARRENS, forgeTagKey("is_cold/end"), forgeTagKey("is_dry/end"));
        tag(Biomes.WARM_OCEAN, forgeTagKey("is_hot/overworld"));
        tag(Biomes.COLD_OCEAN, forgeTagKey("is_cold/overworld"));
        tag(Biomes.DEEP_COLD_OCEAN, forgeTagKey("is_cold/overworld"));
        tag(Biomes.DEEP_FROZEN_OCEAN, forgeTagKey("is_cold/overworld"));
        tag(Biomes.THE_VOID, forgeTagKey("is_void"));
        tag(Biomes.SUNFLOWER_PLAINS, forgeTagKey("is_plains"), forgeTagKey("is_rare"));
        tag(Biomes.WINDSWEPT_GRAVELLY_HILLS, forgeTagKey("is_sparse/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.FLOWER_FOREST, forgeTagKey("is_rare"));
        tag(Biomes.ICE_SPIKES, forgeTagKey("is_cold/overworld"), forgeTagKey("is_snowy"), forgeTagKey("is_rare"));
        tag(Biomes.OLD_GROWTH_BIRCH_FOREST, forgeTagKey("is_dense/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.OLD_GROWTH_SPRUCE_TAIGA, forgeTagKey("is_dense/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.WINDSWEPT_SAVANNA, forgeTagKey("is_hot/overworld"), forgeTagKey("is_dry/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.ERODED_BADLANDS, forgeTagKey("is_hot/overworld"), forgeTagKey("is_dry/overworld"), forgeTagKey("is_sparse/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.BAMBOO_JUNGLE, forgeTagKey("is_hot/overworld"), forgeTagKey("is_wet/overworld"), forgeTagKey("is_rare"));
        tag(Biomes.LUSH_CAVES, forgeTagKey("is_cave"), forgeTagKey("is_lush"), forgeTagKey("is_wet/overworld"));
        tag(Biomes.DRIPSTONE_CAVES, forgeTagKey("is_cave"), forgeTagKey("is_sparse/overworld"));
        tag(Biomes.SOUL_SAND_VALLEY, forgeTagKey("is_hot/nether"), forgeTagKey("is_dry/nether"));
        tag(Biomes.CRIMSON_FOREST, forgeTagKey("is_hot/nether"), forgeTagKey("is_dry/nether"));
        tag(Biomes.WARPED_FOREST, forgeTagKey("is_hot/nether"), forgeTagKey("is_dry/nether"));
        tag(Biomes.BASALT_DELTAS, forgeTagKey("is_hot/nether"), forgeTagKey("is_dry/nether"));
        tag(Biomes.MANGROVE_SWAMP, forgeTagKey("is_wet/overworld"), forgeTagKey("is_hot/overworld"), forgeTagKey("is_swamp"));
        tag(Biomes.DEEP_DARK, forgeTagKey("is_cave"), forgeTagKey("is_rare"), forgeTagKey("is_spooky"));

        tag(forgeTagKey("is_hot")).addTag(forgeTagKey("is_hot/overworld")).addTag(forgeTagKey("is_hot/nether")).addOptionalTag(Tags.Biomes.IS_HOT_END.location());
        tag(forgeTagKey("is_cold")).addTag(forgeTagKey("is_cold/overworld")).addOptionalTag(Tags.Biomes.IS_COLD_NETHER.location()).addTag(forgeTagKey("is_cold/end"));
        tag(forgeTagKey("is_sparse")).addTag(forgeTagKey("is_sparse/overworld")).addOptionalTag(Tags.Biomes.IS_SPARSE_NETHER.location()).addOptionalTag(Tags.Biomes.IS_SPARSE_END.location());
        tag(forgeTagKey("is_dense")).addTag(forgeTagKey("is_dense/overworld")).addOptionalTag(Tags.Biomes.IS_DENSE_NETHER.location()).addOptionalTag(Tags.Biomes.IS_DENSE_END.location());
        tag(forgeTagKey("is_wet")).addTag(forgeTagKey("is_wet/overworld")).addOptionalTag(Tags.Biomes.IS_WET_NETHER.location()).addOptionalTag(Tags.Biomes.IS_WET_END.location());
        tag(forgeTagKey("is_dry")).addTag(forgeTagKey("is_dry/overworld")).addTag(forgeTagKey("is_dry/nether")).addTag(forgeTagKey("is_dry/end"));
        tag(forgeTagKey("is_dead"));

        tag(forgeTagKey("is_water")).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER);
        tag(forgeTagKey("is_mountain")).addTag(forgeTagKey("is_peak")).addTag(forgeTagKey("is_slope"));
        tag(forgeTagKey("is_underground")).addTag(forgeTagKey("is_cave"));
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

    private static TagKey<Biome> forgeTagKey(String path) {
        return BiomeTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    @Override
    public String getName() {
        return "Forge Biome Tags";
    }
}
