package net.minecraftforge.common.data;

import java.io.IOException;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.Tags;

/** Class specifically for adding forge's builtin resource key tags (mods should just use/extend the ResourceKeyTagsProvider) **/
public final class ForgeResourceKeyTagsProvider implements IDataProvider
{
    private final DataGenerator generator;
    private final ExistingFileHelper fileHelper;
    
    public ForgeResourceKeyTagsProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        this.generator = generator;
        this.fileHelper = fileHelper;
    }

    @Override
    public void run(DirectoryCache cache) throws IOException
    {
        new ResourceKeyTagsProvider<Biome>(generator, fileHelper, "forge", Registry.BIOME_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.Biomes.HILLS)
                    .add(Biomes.BAMBOO_JUNGLE_HILLS, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.DESERT_HILLS,
                        Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.JUNGLE_HILLS, Biomes.SNOWY_TAIGA_HILLS,
                        Biomes.SWAMP_HILLS, Biomes.TAIGA_HILLS, Biomes.TALL_BIRCH_HILLS, Biomes.WOODED_HILLS)
                    .add(Biomes.FLOWER_FOREST, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_MOUNTAINS, Biomes.TALL_BIRCH_FOREST);
                this.tag(Tags.Biomes.PLATEAUS)
                    .add(Biomes.BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU)
                    .add(Biomes.SAVANNA_PLATEAU, Biomes.SHATTERED_SAVANNA_PLATEAU);
                this.tag(Tags.Biomes.MODIFIED)
                    .add(Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
                this.tag(Tags.Biomes.RARE)
                    .add(Biomes.JUNGLE_EDGE, Biomes.SAVANNA_PLATEAU, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES,
                        Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS,
                        Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST,
                        Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA,
                        Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU,
                        Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.BAMBOO_JUNGLE,
                        Biomes.BAMBOO_JUNGLE_HILLS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE);
                
                this.tag(Tags.Biomes.OCEANS)
                    .addTags(Tags.Biomes.DEEP_OCEANS, Tags.Biomes.SHALLOW_OCEANS);
                this.tag(Tags.Biomes.DEEP_OCEANS)
                    .add(Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_WARM_OCEAN);
                this.tag(Tags.Biomes.SHALLOW_OCEANS)
                    .add(Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.WARM_OCEAN);
                this.tag(Tags.Biomes.RIVERS)
                    .add(Biomes.RIVER, Biomes.FROZEN_RIVER);
                this.tag(Tags.Biomes.WATER)
                    .addTags(Tags.Biomes.OCEANS, Tags.Biomes.RIVERS);
                

                this.tag(Tags.Biomes.BADLANDS)
                    .add(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS,
                        Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU);
                this.tag(Tags.Biomes.BEACHES)
                    .add(Biomes.BEACH, Biomes.MUSHROOM_FIELD_SHORE, Biomes.SNOWY_BEACH, Biomes.STONE_SHORE);
                this.tag(Tags.Biomes.DESERTS)
                    .add(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES);
                this.tag(Tags.Biomes.FORESTS)
                    .addTags(Tags.Biomes.BIRCH_FORESTS, Tags.Biomes.DARK_FORESTS, Tags.Biomes.JUNGLE_FORESTS,
                        Tags.Biomes.NETHER_FORESTS, Tags.Biomes.OAK_FORESTS, Tags.Biomes.TAIGA_FORESTS);
                this.tag(Tags.Biomes.BIRCH_FORESTS)
                    .add(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS);
                this.tag(Tags.Biomes.DARK_FORESTS)
                    .add(Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS);
                this.tag(Tags.Biomes.JUNGLE_FORESTS)
                    .addTags(Tags.Biomes.BAMBOO_JUNGLE_FORESTS)
                    .add(Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE);
                this.tag(Tags.Biomes.BAMBOO_JUNGLE_FORESTS)
                    .add(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS);
                this.tag(Tags.Biomes.NETHER_FORESTS)
                    .add(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST);
                this.tag(Tags.Biomes.OAK_FORESTS)
                    .add(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WOODED_HILLS);
                this.tag(Tags.Biomes.TAIGA_FORESTS)
                    .add(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS)
                    .add(Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS)
                    .add(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);
                this.tag(Tags.Biomes.MUSHROOM)
                    .add(Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE);
                this.tag(Tags.Biomes.MOUNTAINS)
                    .add(Biomes.MOUNTAIN_EDGE, Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.TAIGA_MOUNTAINS, Biomes.WOODED_MOUNTAINS)
                    .add(Biomes.MODIFIED_JUNGLE, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU);
                this.tag(Tags.Biomes.PLAINS)
                    .add(Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SUNFLOWER_PLAINS);
                this.tag(Tags.Biomes.GRASSLANDS)
                    .addTags(Tags.Biomes.PLAINS, Tags.Biomes.SAVANNAS);
                this.tag(Tags.Biomes.SAVANNAS)
                    .add(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU);
                this.tag(Tags.Biomes.SNOWY)
                    .add(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_RIVER)
                    .add(Biomes.SNOWY_BEACH, Biomes.SNOWY_MOUNTAINS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.SNOWY_TUNDRA);
                this.tag(Tags.Biomes.SWAMPS)
                    .add(Biomes.SWAMP, Biomes.SWAMP_HILLS);
                this.tag(Tags.Biomes.VOIDS)
                    .add(Biomes.THE_VOID);
                
                this.tag(Tags.Biomes.OVERWORLD)
                    .add(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS,
                        Biomes.BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.COLD_OCEAN,
                        Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN,
                        Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DESERT,
                        Biomes.DESERT_HILLS, Biomes.DESERT_LAKES, Biomes.ERODED_BADLANDS, Biomes.FLOWER_FOREST,
                        Biomes.FOREST, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.GIANT_SPRUCE_TAIGA,
                        Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GRAVELLY_MOUNTAINS,
                        Biomes.ICE_SPIKES, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS,
                        Biomes.LUKEWARM_OCEAN, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.MODIFIED_JUNGLE,
                        Biomes.MODIFIED_JUNGLE_EDGE, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MOUNTAIN_EDGE, Biomes.MOUNTAINS,
                        Biomes.MUSHROOM_FIELD_SHORE, Biomes.MUSHROOM_FIELDS, Biomes.OCEAN, Biomes.PLAINS,
                        Biomes.RIVER, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SHATTERED_SAVANNA,
                        Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.SNOWY_BEACH, Biomes.SNOWY_MOUNTAINS, Biomes.SNOWY_TAIGA,
                        Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.SNOWY_TUNDRA, Biomes.STONE_SHORE,
                        Biomes.SUNFLOWER_PLAINS, Biomes.SWAMP, Biomes.SWAMP_HILLS, Biomes.TAIGA,
                        Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS,
                        Biomes.WARM_OCEAN, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.WOODED_HILLS, Biomes.WOODED_MOUNTAINS);
                this.tag(Tags.Biomes.NETHER)
                    .addTags(Tags.Biomes.NETHER_FORESTS)
                    .add(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS);
                this.tag(Tags.Biomes.END)
                    .add(Biomes.THE_END, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS, Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS);
                
            }
        }.run(cache);
        
        // tags for the "noise_settings" that specify structure placement entries for dimensions
        new ResourceKeyTagsProvider<DimensionSettings>(generator, fileHelper, "forge", Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.NoiseSettings.AMPLIFIED).add(DimensionSettings.AMPLIFIED);
                this.tag(Tags.NoiseSettings.CAVES).add(DimensionSettings.CAVES);
                this.tag(Tags.NoiseSettings.END).add(DimensionSettings.END);
                this.tag(Tags.NoiseSettings.FLOATING_ISLANDS).add(DimensionSettings.FLOATING_ISLANDS);
                this.tag(Tags.NoiseSettings.NETHER).add(DimensionSettings.NETHER);
                this.tag(Tags.NoiseSettings.OVERWORLD).add(DimensionSettings.OVERWORLD);
            }
            
        };

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

        new ResourceKeyTagsProvider<World>(generator, fileHelper, "forge", Registry.DIMENSION_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(Tags.Dimensions.END).add(World.END);
                this.tag(Tags.Dimensions.NETHER).add(World.NETHER);
                this.tag(Tags.Dimensions.OVERWORLD).add(World.OVERWORLD);
            }
        }.run(cache);
    }

    @Override
    public String getName()
    {
        return "Forge Resource Key Tags Provider";
    }
    
    
}
