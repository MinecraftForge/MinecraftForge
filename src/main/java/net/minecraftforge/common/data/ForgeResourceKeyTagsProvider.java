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

import java.io.IOException;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.Tags;

/** Class specifically for adding forge's builtin resource key tags (mods should just use/extend the ResourceKeyTagsProvider) **/
public final class ForgeResourceKeyTagsProvider implements DataProvider
{
    private final DataGenerator generator;
    private final ExistingFileHelper fileHelper;
    
    public ForgeResourceKeyTagsProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        this.generator = generator;
        this.fileHelper = fileHelper;
    }

    @Override
    public void run(HashCache cache) throws IOException
    {
        new ResourceKeyTagsProvider<Biome>(generator, fileHelper, "forge", Registry.BIOME_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.Biomes.HILLS)
                    .add(Biomes.FLOWER_FOREST, Biomes.ICE_SPIKES, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.OLD_GROWTH_BIRCH_FOREST);
                this.tag(Tags.Biomes.PLATEAUS)
                    .add(Biomes.SAVANNA_PLATEAU, Biomes.WOODED_BADLANDS, Biomes.MEADOW);
                this.tag(Tags.Biomes.RARE)
                    .add(Biomes.SPARSE_JUNGLE, Biomes.SAVANNA_PLATEAU, Biomes.SUNFLOWER_PLAINS, Biomes.WINDSWEPT_GRAVELLY_HILLS,
                        Biomes.FLOWER_FOREST, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.OLD_GROWTH_SPRUCE_TAIGA,
                        Biomes.WINDSWEPT_SAVANNA, Biomes.ERODED_BADLANDS, Biomes.BAMBOO_JUNGLE, Biomes.MUSHROOM_FIELDS);
                
                this.tag(Tags.Biomes.OCEANS)
                    .addTags(Tags.Biomes.DEEP_OCEANS, Tags.Biomes.SHALLOW_OCEANS);
                this.tag(Tags.Biomes.DEEP_OCEANS)
                    .add(Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN);
                this.tag(Tags.Biomes.SHALLOW_OCEANS)
                    .add(Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.WARM_OCEAN);
                this.tag(Tags.Biomes.RIVERS)
                    .add(Biomes.RIVER, Biomes.FROZEN_RIVER);
                this.tag(Tags.Biomes.WATER)
                    .addTags(Tags.Biomes.OCEANS, Tags.Biomes.RIVERS);
                

                this.tag(Tags.Biomes.BADLANDS)
                    .add(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS);
                this.tag(Tags.Biomes.BEACHES)
                    .add(Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE);
                this.tag(Tags.Biomes.DESERTS)
                    .add(Biomes.DESERT);
                this.tag(Tags.Biomes.FORESTS)
                    .addTags(Tags.Biomes.BIRCH_FORESTS, Tags.Biomes.DARK_FORESTS, Tags.Biomes.JUNGLE_FORESTS,
                        Tags.Biomes.NETHER_FORESTS, Tags.Biomes.OAK_FORESTS, Tags.Biomes.TAIGA_FORESTS);
                this.tag(Tags.Biomes.BIRCH_FORESTS)
                    .add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST);
                this.tag(Tags.Biomes.DARK_FORESTS)
                    .add(Biomes.DARK_FOREST);
                this.tag(Tags.Biomes.JUNGLE_FORESTS)
                    .addTags(Tags.Biomes.BAMBOO_JUNGLE_FORESTS)
                    .add(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE);
                this.tag(Tags.Biomes.BAMBOO_JUNGLE_FORESTS)
                    .add(Biomes.BAMBOO_JUNGLE);
                this.tag(Tags.Biomes.NETHER_FORESTS)
                    .add(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST);
                this.tag(Tags.Biomes.OAK_FORESTS)
                    .add(Biomes.FOREST, Biomes.FLOWER_FOREST);
                this.tag(Tags.Biomes.TAIGA_FORESTS)
                    .add(Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.GROVE);
                this.tag(Tags.Biomes.MUSHROOM)
                    .add(Biomes.MUSHROOM_FIELDS);
                this.tag(Tags.Biomes.MOUNTAINS)
                    .add(Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_SAVANNA);
                this.tag(Tags.Biomes.PLAINS)
                    .add(Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW);
                this.tag(Tags.Biomes.GRASSLANDS)
                    .addTags(Tags.Biomes.PLAINS, Tags.Biomes.SAVANNAS);
                this.tag(Tags.Biomes.SAVANNAS)
                    .add(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA);
                this.tag(Tags.Biomes.SNOWY)
                    .add(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_RIVER)
                    .add(Biomes.SNOWY_BEACH, Biomes.SNOWY_TAIGA, Biomes.SNOWY_PLAINS,
                        Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.JAGGED_PEAKS, Biomes.FROZEN_PEAKS);
                this.tag(Tags.Biomes.SWAMPS)
                    .add(Biomes.SWAMP);
                this.tag(Tags.Biomes.SLOPES)
                    .add(Biomes.MEADOW, Biomes.GROVE, Biomes.SNOWY_SLOPES);
                this.tag(Tags.Biomes.PEAKS)
                    .add(Biomes.JAGGED_PEAKS, Biomes.FROZEN_PEAKS, Biomes.STONY_PEAKS);
                this.tag(Tags.Biomes.VOIDS)
                    .add(Biomes.THE_VOID);
                
                this.tag(Tags.Biomes.OVERWORLD)
                    .addTags(Tags.Biomes.OVERWORLD_SURFACE, Tags.Biomes.OVERWORLD_UNDERGROUND);
                this.tag(Tags.Biomes.OVERWORLD_SURFACE)
                    .add(Biomes.BADLANDS,
                        Biomes.BAMBOO_JUNGLE,
                        Biomes.BEACH,
                        Biomes.BIRCH_FOREST,
                        Biomes.COLD_OCEAN,
                        Biomes.DARK_FOREST,
                        Biomes.DEEP_COLD_OCEAN,
                        Biomes.DEEP_FROZEN_OCEAN,
                        Biomes.DEEP_LUKEWARM_OCEAN,
                        Biomes.DEEP_OCEAN,
                        Biomes.DESERT,
                        Biomes.ERODED_BADLANDS,
                        Biomes.FLOWER_FOREST,
                        Biomes.FOREST,
                        Biomes.FROZEN_OCEAN,
                        Biomes.FROZEN_PEAKS,
                        Biomes.FROZEN_RIVER,
                        Biomes.GROVE,
                        Biomes.ICE_SPIKES,
                        Biomes.JAGGED_PEAKS,
                        Biomes.JUNGLE,
                        Biomes.LUKEWARM_OCEAN,
                        Biomes.MEADOW,
                        Biomes.MUSHROOM_FIELDS,
                        Biomes.OCEAN,
                        Biomes.OLD_GROWTH_BIRCH_FOREST,
                        Biomes.OLD_GROWTH_PINE_TAIGA,
                        Biomes.OLD_GROWTH_SPRUCE_TAIGA,
                        Biomes.PLAINS,
                        Biomes.RIVER,
                        Biomes.SAVANNA,
                        Biomes.SAVANNA_PLATEAU,
                        Biomes.SNOWY_BEACH,
                        Biomes.SNOWY_SLOPES,
                        Biomes.SNOWY_TAIGA,
                        Biomes.SNOWY_PLAINS,
                        Biomes.SPARSE_JUNGLE,
                        Biomes.STONY_PEAKS,
                        Biomes.STONY_SHORE,
                        Biomes.SUNFLOWER_PLAINS,
                        Biomes.SWAMP,
                        Biomes.TAIGA,
                        Biomes.WARM_OCEAN,
                        Biomes.WOODED_BADLANDS,
                        Biomes.WINDSWEPT_FOREST,
                        Biomes.WINDSWEPT_GRAVELLY_HILLS,
                        Biomes.WINDSWEPT_HILLS,
                        Biomes.WINDSWEPT_SAVANNA);
                this.tag(Tags.Biomes.OVERWORLD_UNDERGROUND)
                    .add(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES);
                this.tag(Tags.Biomes.NETHER)
                    .addTags(Tags.Biomes.NETHER_FORESTS)
                    .add(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS);
                this.tag(Tags.Biomes.END)
                    .add(Biomes.THE_END, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS, Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS);
                
            }
        }.run(cache);
        
        // tags for the "noise_settings" that specify structure placement entries for dimensions
        new ResourceKeyTagsProvider<NoiseGeneratorSettings>(generator, fileHelper, "forge", Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.NoiseSettings.AMPLIFIED).add(NoiseGeneratorSettings.AMPLIFIED);
                this.tag(Tags.NoiseSettings.CAVES).add(NoiseGeneratorSettings.CAVES);
                this.tag(Tags.NoiseSettings.END).add(NoiseGeneratorSettings.END);
                this.tag(Tags.NoiseSettings.FLOATING_ISLANDS).add(NoiseGeneratorSettings.FLOATING_ISLANDS);
                this.tag(Tags.NoiseSettings.NETHER).add(NoiseGeneratorSettings.NETHER);
                this.tag(Tags.NoiseSettings.OVERWORLD).add(NoiseGeneratorSettings.OVERWORLD);
            }
            
        }.run(cache);

        new ResourceKeyTagsProvider<DimensionType>(generator, fileHelper, "forge", Registry.DIMENSION_TYPE_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.DimensionTypes.END).add(DimensionType.END_LOCATION);
                this.tag(Tags.DimensionTypes.NETHER).add(DimensionType.NETHER_LOCATION);
                this.tag(Tags.DimensionTypes.OVERWORLD)
                    .addTags(Tags.DimensionTypes.OVERWORLD_CAVES)
                    .add(DimensionType.OVERWORLD_LOCATION);
                this.tag(Tags.DimensionTypes.OVERWORLD_CAVES).add(DimensionType.OVERWORLD_CAVES_LOCATION);
            }
        }.run(cache);

        new ResourceKeyTagsProvider<Level>(generator, fileHelper, "forge", Registry.DIMENSION_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.Dimensions.END).add(Level.END);
                this.tag(Tags.Dimensions.NETHER).add(Level.NETHER);
                this.tag(Tags.Dimensions.OVERWORLD).add(Level.OVERWORLD);
            }
        }.run(cache);
    }

    @Override
    public String getName()
    {
        return "Forge Resource Key Tags Provider";
    }
    
    
}
