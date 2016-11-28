/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common;

import java.util.*;

import javax.annotation.Nonnull;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.*;
import static net.minecraftforge.common.BiomeDictionary.Type.*;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class BiomeDictionary
{
    public static final class Type
    {
        /*Temperature-based tags. Specifying neither implies a biome is temperate*/
        public static final Type HOT = new Type("HOT");
        public static final Type COLD = new Type("COLD");

        /*Tags specifying the amount of vegetation a biome has. Specifying neither implies a biome to have moderate amounts*/
        public static final Type SPARSE = new Type("SPARSE");
        public static final Type DENSE = new Type("DENSE");

        /*Tags specifying how moist a biome is. Specifying neither implies the biome as having moderate humidity*/
        public static final Type WET = new Type("WET");
        public static final Type DRY = new Type("DRY");

        /*Tree-based tags, SAVANNA refers to dry, desert-like trees (Such as Acacia), CONIFEROUS refers to snowy trees (Such as Spruce) and JUNGLE refers to jungle trees.
         * Specifying no tag implies a biome has temperate trees (Such as Oak)*/
        public static final Type SAVANNA = new Type("SAVANNA");
        public static final Type CONIFEROUS = new Type("CONIFEROUS");
        public static final Type JUNGLE = new Type("JUNGLE");

        /*Tags specifying the nature of a biome*/
        public static final Type SPOOKY = new Type("SPOOKY");
        public static final Type DEAD = new Type("DEAD");
        public static final Type LUSH = new Type("LUSH");
        public static final Type NETHER = new Type("NETHER");
        public static final Type END = new Type("END");
        public static final Type MUSHROOM = new Type("MUSHROOM");
        public static final Type MAGICAL = new Type("MAGICAL");

        public static final Type OCEAN = new Type("OCEAN");
        public static final Type RIVER = new Type("RIVER");
        /**
         * A general tag for all water-based biomes. Shown as present if OCEAN or RIVER are.
         **/
        public static final Type WATER = new Type("WATER", OCEAN, RIVER);

        /*Generic types which a biome can be*/
        public static final Type MESA = new Type("MESA");
        public static final Type FOREST = new Type("FOREST");
        public static final Type PLAINS = new Type("PLAINS");
        public static final Type MOUNTAIN = new Type("MOUNTAIN");
        public static final Type HILLS = new Type("HILLS");
        public static final Type SWAMP = new Type("SWAMP");
        public static final Type SANDY = new Type("SANDY");
        public static final Type SNOWY = new Type("SNOWY");
        public static final Type WASTELAND = new Type("WASTELAND");
        public static final Type BEACH = new Type("BEACH");

        private static final Map<String, Type> byName = new HashMap<String, Type>();

        private final String name;
        private final List<Type> subTags;
        private final Set<Biome> biomes = new HashSet<Biome>();
        private final Set<Biome> biomesUn = Collections.unmodifiableSet(biomes);

        private Type(String name, Type... subTags)
        {
            this.name = name;
            this.subTags = ImmutableList.copyOf(subTags);

            byName.put(name, this);
        }

        private boolean hasSubTags()
        {
            return !subTags.isEmpty();
        }

        public String name()
        {
            return name;
        }

        /**
         * Retrieves a Type value by name,
         * if one does not exist already it creates one.
         * This can be used as intermediate measure for modders to
         * add their own category of Biome.
         * <p>
         * There are NO naming conventions besides:
         * MUST be all upper case (enforced by name.toUpper())
         * NO Special characters. {Unenforced, just don't be a pain, if it becomes a issue I WILL
         * make this RTE with no worry about backwards compatibility}
         * <p>
         * Note: For performance sake, the return value of this function SHOULD be cached.
         * Two calls with the same name SHOULD return the same value.
         *
         * @param name The name of this Type
         * @return An instance of Type for this name.
         */
        public static Type getType(String name, Type... subTypes)
        {
            name = name.toUpperCase();
            Type t = byName.get(name);
            if (t == null)
            {
                t = new Type(name, subTypes);
            }
            return t;
        }
    }

    private static final Map<ResourceLocation, BiomeInfo> biomeInfoMap = new HashMap<ResourceLocation, BiomeInfo>();

    private static class BiomeInfo
    {

        private final Set<Type> types = new HashSet<Type>();
        private final Set<Type> typesUn = Collections.unmodifiableSet(this.types);

    }

    static
    {
        registerVanillaBiomes();
    }

    /**
     * Tags a biome with the given types.
     *
     * @param biome the biome to be tagged
     * @param types the types to tag with
     */
    public static void addTypes(Biome biome, Type... types)
    {
        Preconditions.checkArgument(ForgeRegistries.BIOMES.containsValue(biome), "Cannot register biome types for unregistered biome");

        List<Type> subTags = listSubTags(types);

        for (Type type : subTags)
        {
            type.biomes.add(biome);
        }

        getBiomeInfo(biome).types.addAll(subTags);
    }

    /**
     * Returns the set of biomes that have been tagged with the given type.
     *
     * @param type the Type to look for
     * @return the set of biomes
     */
    @Nonnull
    public static Set<Biome> getBiomes(Type type)
    {
        return type.biomesUn;
    }

    /**
     * Get the set of types that the given biome has been tagged with.
     *
     * @param biome the biome
     * @return the set of types
     */
    @Nonnull
    public static Set<Type> getTypes(Biome biome)
    {
        ensureTagged(biome);
        return getBiomeInfo(biome).typesUn;
    }

    /**
     * Checks to see if the two given biomes have tags in common.
     *
     * @param biomeA the first biome
     * @param biomeB the second biome
     * @return returns true if a common type is found, false otherwise
     */
    public static boolean areSimilar(Biome biomeA, Biome biomeB)
    {
        for (Type type : getTypes(biomeA))
        {
            if (containsType(getTypes(biomeB), type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see if the given biome is has been tagged with the given type.
     *
     * @param biome the biome
     * @param type  the type to check for
     * @return true if the biome has been tagged with the type, false otherwise
     */
    public static boolean hasType(Biome biome, Type type)
    {
        return getTypes(biome).contains(type);
    }

    /**
     * Checks to see if the given biome has been tagged with any type.
     *
     * @param biome the biome to consider
     * @return true if the biome has been tagged with any type
     */
    public static boolean hasTypes(Biome biome)
    {
        return !getBiomeInfo(biome).types.isEmpty();
    }

    /**
     * Automatically tags a given biome with appropriate types based on certain heuristics.
     * If a biome's types are requested and the biome has not yet been tagged with any types, the biome's tags will be
     * determined using this method
     *
     * @param biome the biome to be tagged
     */
    public static void makeBestGuess(Biome biome)
    {
        if (biome.theBiomeDecorator.treesPerChunk >= 3)
        {
            if (biome.isHighHumidity() && biome.getTemperature() >= 0.9F)
            {
                BiomeDictionary.addTypes(biome, JUNGLE);
            }
            else if (!biome.isHighHumidity())
            {
                BiomeDictionary.addTypes(biome, FOREST);

                if (biome.getTemperature() <= 0.2f)
                {
                    BiomeDictionary.addTypes(biome, CONIFEROUS);
                }
            }
        }
        else if (biome.getHeightVariation() <= 0.3F && biome.getHeightVariation() >= 0.0F)
        {
            if (!biome.isHighHumidity() || biome.getBaseHeight() >= 0.0F)
            {
                BiomeDictionary.addTypes(biome, PLAINS);
            }
        }

        if (biome.getRainfall() > 0.85f)
        {
            BiomeDictionary.addTypes(biome, WET);
        }

        if (biome.getRainfall() < 0.15f)
        {
            BiomeDictionary.addTypes(biome, DRY);
        }

        if (biome.getTemperature() > 0.85f)
        {
            BiomeDictionary.addTypes(biome, HOT);
        }

        if (biome.getTemperature() < 0.15f)
        {
            BiomeDictionary.addTypes(biome, COLD);
        }

        if (biome.theBiomeDecorator.treesPerChunk > 0 && biome.theBiomeDecorator.treesPerChunk < 3)
        {
            BiomeDictionary.addTypes(biome, SPARSE);
        }
        else if (biome.theBiomeDecorator.treesPerChunk >= 10)
        {
            BiomeDictionary.addTypes(biome, DENSE);
        }

        if (biome.isHighHumidity() && biome.getBaseHeight() < 0.0F && (biome.getHeightVariation() <= 0.3F && biome.getHeightVariation() >= 0.0F))
        {
            BiomeDictionary.addTypes(biome, SWAMP);
        }

        if (biome.getBaseHeight() <= -0.5F)
        {
            if (biome.getHeightVariation() == 0.0F)
            {
                BiomeDictionary.addTypes(biome, RIVER);
            }
            else
            {
                BiomeDictionary.addTypes(biome, OCEAN);
            }
        }

        if (biome.getHeightVariation() >= 0.4F && biome.getHeightVariation() < 1.5F)
        {
            BiomeDictionary.addTypes(biome, HILLS);
        }

        if (biome.getHeightVariation() >= 1.5F)
        {
            BiomeDictionary.addTypes(biome, MOUNTAIN);
        }

        if (biome.getEnableSnow())
        {
            BiomeDictionary.addTypes(biome, SNOWY);
        }

        if (biome.topBlock != Blocks.SAND && biome.getTemperature() >= 1.0f && biome.getRainfall() < 0.2f)
        {
            BiomeDictionary.addTypes(biome, SAVANNA);
        }

        if (biome.topBlock == Blocks.SAND)
        {
            BiomeDictionary.addTypes(biome, SANDY);
        }
        else if (biome.topBlock == Blocks.MYCELIUM)
        {
            BiomeDictionary.addTypes(biome, MUSHROOM);
        }
        if (biome.fillerBlock == Blocks.HARDENED_CLAY)
        {
            BiomeDictionary.addTypes(biome, MESA);
        }
    }

    //Internal implementation
    private static BiomeInfo getBiomeInfo(Biome biome)
    {
        BiomeInfo info = biomeInfoMap.get(biome.getRegistryName());
        if (info == null)
        {
            info = new BiomeInfo();
            biomeInfoMap.put(biome.getRegistryName(), info);
        }
        return info;
    }

    /**
     * Ensure that the given biome has been tagged with at least one type
     * @param biome the biome
     */
    static void ensureTagged(Biome biome)
    {
        if (!hasTypes(biome))
        {
            FMLLog.warning("Biome %s has not been tagged with any types, types will be assigned on a best-effort guess.");
            makeBestGuess(biome);
        }
    }

    private static boolean containsType(Set<Type> types, Type type)
    {
        if (type.hasSubTags())
        {
            return !Collections.disjoint(types, type.subTags);
        }
        else
        {
            return types.contains(type);
        }
    }

    private static List<Type> listSubTags(Type... types)
    {
        List<Type> subTags = new ArrayList<Type>();

        for (Type type : types)
        {
            if (type.hasSubTags())
            {
                subTags.addAll(type.subTags);
            }
            else
            {
                subTags.add(type);
            }
        }

        return subTags;
    }

    private static void registerVanillaBiomes()
    {
        addTypes(Biomes.OCEAN,                    OCEAN                                        );
        addTypes(Biomes.PLAINS,                   PLAINS                                       );
        addTypes(Biomes.DESERT,                   HOT,      DRY,        SANDY                  );
        addTypes(Biomes.EXTREME_HILLS,            MOUNTAIN, HILLS                              );
        addTypes(Biomes.FOREST,                   FOREST                                       );
        addTypes(Biomes.TAIGA,                    COLD,     CONIFEROUS, FOREST                 );
        addTypes(Biomes.TAIGA_HILLS,              COLD,     CONIFEROUS, FOREST,   HILLS        );
        addTypes(Biomes.SWAMPLAND,                WET,      SWAMP                              );
        addTypes(Biomes.RIVER,                    RIVER                                        );
        addTypes(Biomes.FROZEN_OCEAN,             COLD,     OCEAN,      SNOWY                  );
        addTypes(Biomes.FROZEN_RIVER,             COLD,     RIVER,      SNOWY                  );
        addTypes(Biomes.ICE_PLAINS,               COLD,     SNOWY,      WASTELAND              );
        addTypes(Biomes.ICE_MOUNTAINS,            COLD,     SNOWY,      MOUNTAIN               );
        addTypes(Biomes.BEACH,                    BEACH                                        );
        addTypes(Biomes.DESERT_HILLS,             HOT,      DRY,        SANDY,    HILLS        );
        addTypes(Biomes.JUNGLE,                   HOT,      WET,        DENSE,    JUNGLE       );
        addTypes(Biomes.JUNGLE_HILLS,             HOT,      WET,        DENSE,    JUNGLE, HILLS);
        addTypes(Biomes.FOREST_HILLS,             FOREST,   HILLS                              );
        addTypes(Biomes.SKY,                      COLD,     DRY,        END                    );
        addTypes(Biomes.HELL,                     HOT,      DRY,        NETHER                 );
        addTypes(Biomes.MUSHROOM_ISLAND,          MUSHROOM                                     );
        addTypes(Biomes.EXTREME_HILLS_EDGE,       MOUNTAIN                                     );
        addTypes(Biomes.MUSHROOM_ISLAND_SHORE,    MUSHROOM, BEACH                              );
        addTypes(Biomes.JUNGLE_EDGE,              HOT,      WET,        JUNGLE,   FOREST       );
        addTypes(Biomes.DEEP_OCEAN,               OCEAN                                        );
        addTypes(Biomes.STONE_BEACH,              BEACH                                        );
        addTypes(Biomes.COLD_BEACH,               COLD,     BEACH,      SNOWY                  );
        addTypes(Biomes.BIRCH_FOREST,             FOREST                                       );
        addTypes(Biomes.BIRCH_FOREST_HILLS,       FOREST,   HILLS                              );
        addTypes(Biomes.ROOFED_FOREST,            SPOOKY,   DENSE,      FOREST                 );
        addTypes(Biomes.COLD_TAIGA,               COLD,     CONIFEROUS, FOREST,   SNOWY        );
        addTypes(Biomes.COLD_TAIGA_HILLS,         COLD,     CONIFEROUS, FOREST,   SNOWY,  HILLS);
        addTypes(Biomes.REDWOOD_TAIGA,            COLD,     CONIFEROUS, FOREST                 );
        addTypes(Biomes.REDWOOD_TAIGA_HILLS,      COLD,     CONIFEROUS, FOREST,   HILLS        );
        addTypes(Biomes.EXTREME_HILLS_WITH_TREES, MOUNTAIN, FOREST,     SPARSE                 );
        addTypes(Biomes.SAVANNA,                  HOT,      SAVANNA,    PLAINS,   SPARSE       );
        addTypes(Biomes.SAVANNA_PLATEAU,          HOT,      SAVANNA,    PLAINS,   SPARSE       );
        addTypes(Biomes.MESA,                     MESA,     SANDY                              );
        addTypes(Biomes.MESA_ROCK,                MESA,     SPARSE,     SANDY                  );
        addTypes(Biomes.MESA_CLEAR_ROCK,          MESA,     SANDY                              );
    }
}
