/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.*;
import static net.minecraftforge.common.BiomeDictionary.Type.*;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class BiomeDictionary
{
    private static final boolean DEBUG = false;
    private static final Logger LOGGER = LogManager.getLogger();

    public static final class Type
    {

        private static final Map<String, Type> byName = new HashMap<String, Type>();
        private static Collection<Type> allTypes = Collections.unmodifiableCollection(byName.values());

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
        public static final Type RARE = new Type("RARE");

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
        public static final Type VOID = new Type("VOID");

        private final String name;
        private final List<Type> subTypes;
        private final Set<Biome> biomes = new HashSet<Biome>();
        private final Set<Biome> biomesUn = Collections.unmodifiableSet(biomes);

        private Type(String name, Type... subTypes)
        {
            this.name = name;
            this.subTypes = ImmutableList.copyOf(subTypes);

            byName.put(name, this);
        }

        /**
         * Gets the name for this type.
         */
        public String getName()
        {
            return name;
        }

        public String toString()
        {
            return name;
        }

        /**
         * Retrieves a Type instance by name,
         * if one does not exist already it creates one.
         * This can be used as intermediate measure for modders to
         * add their own Biome types.
         * <p>
         * There are <i>no</i> naming conventions besides:
         * <ul><li><b>Must</b> be all upper case (enforced by name.toUpper())</li>
         * <li><b>No</b> Special characters. {Unenforced, just don't be a pain, if it becomes a issue I WILL
         * make this RTE with no worry about backwards compatibility}</li></ul>
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

        /**
         * @return An unmodifiable collection of all current biome types.
         */
        public static Collection<Type> getAll()
        {
            return allTypes;
        }

        @Nullable
        public static Type fromVanilla(Biome.Category category)
        {
            if (category == Biome.Category.NONE)
                return null;
            if (category == Biome.Category.THEEND)
                return VOID;
            return getType(category.name());
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
     * Adds the given types to the biome.
     *
     */
    public static void addTypes(Biome biome, Type... types)
    {
        Preconditions.checkArgument(ForgeRegistries.BIOMES.containsValue(biome), "Cannot add types to unregistered biome %s", biome);

        Collection<Type> supertypes = listSupertypes(types);
        Collections.addAll(supertypes, types);

        for (Type type : supertypes)
        {
            type.biomes.add(biome);
        }

        BiomeInfo biomeInfo = getBiomeInfo(biome);
        Collections.addAll(biomeInfo.types, types);
        biomeInfo.types.addAll(supertypes);
    }

    /**
     * Gets the set of biomes that have the given type.
     *
     */
    @Nonnull
    public static Set<Biome> getBiomes(Type type)
    {
        return type.biomesUn;
    }

    /**
     * Gets the set of types that have been added to the given biome.
     *
     */
    @Nonnull
    public static Set<Type> getTypes(Biome biome)
    {
        ensureHasTypes(biome);
        return getBiomeInfo(biome).typesUn;
    }

    /**
     * Checks if the two given biomes have types in common.
     *
     * @return returns true if a common type is found, false otherwise
     */
    public static boolean areSimilar(Biome biomeA, Biome biomeB)
    {
        for (Type type : getTypes(biomeA))
        {
            if (getTypes(biomeB).contains(type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given type has been added to the given biome.
     *
     */
    public static boolean hasType(Biome biome, Type type)
    {
        return getTypes(biome).contains(type);
    }

    /**
     * Checks if any type has been added to the given biome.
     *
     */
    public static boolean hasAnyType(Biome biome)
    {
        return !getBiomeInfo(biome).types.isEmpty();
    }

    /**
     * Automatically adds appropriate types to a given biome based on certain heuristics.
     * If a biome's types are requested and no types have been added to the biome so far, the biome's types
     * will be determined and added using this method.
     *
     */
    public static void makeBestGuess(Biome biome)
    {
        Type type = Type.fromVanilla(biome.getCategory());
        if (type != null)
        {
            BiomeDictionary.addTypes(biome, type);
        }

        if (biome.getDownfall() > 0.85f)
        {
            BiomeDictionary.addTypes(biome, WET);
        }

        if (biome.getDownfall() < 0.15f)
        {
            BiomeDictionary.addTypes(biome, DRY);
        }

        if (biome.getDefaultTemperature() > 0.85f)
        {
            BiomeDictionary.addTypes(biome, HOT);
        }

        if (biome.getDefaultTemperature() < 0.15f)
        {
            BiomeDictionary.addTypes(biome, COLD);
        }

        if (biome.isHighHumidity() && biome.getDepth() < 0.0F && (biome.getScale() <= 0.3F && biome.getScale() >= 0.0F))
        {
            BiomeDictionary.addTypes(biome, SWAMP);
        }

        if (biome.getDepth() <= -0.5F)
        {
            if (biome.getScale() == 0.0F)
            {
                BiomeDictionary.addTypes(biome, RIVER);
            }
            else
            {
                BiomeDictionary.addTypes(biome, OCEAN);
            }
        }

        if (biome.getScale() >= 0.4F && biome.getScale() < 1.5F)
        {
            BiomeDictionary.addTypes(biome, HILLS);
        }

        if (biome.getScale() >= 1.5F)
        {
            BiomeDictionary.addTypes(biome, MOUNTAIN);
        }
    }

    //Internal implementation
    private static BiomeInfo getBiomeInfo(Biome biome)
    {
        return biomeInfoMap.computeIfAbsent(biome.getRegistryName(), k -> new BiomeInfo());
    }

    /**
     * Ensure that at least one type has been added to the given biome.
     */
    static void ensureHasTypes(Biome biome)
    {
        if (!hasAnyType(biome))
        {
            makeBestGuess(biome);
            LOGGER.warn("No types have been added to Biome {}, types have been assigned on a best-effort guess: {}", biome.getRegistryName(), getTypes(biome));
        }
    }

    private static Collection<Type> listSupertypes(Type... types)
    {
        Set<Type> supertypes = new HashSet<Type>();
        Deque<Type> next = new ArrayDeque<Type>();
        Collections.addAll(next, types);

        while (!next.isEmpty())
        {
            Type type = next.remove();

            for (Type sType : Type.byName.values())
            {
                if (sType.subTypes.contains(type) && supertypes.add(sType))
                    next.add(sType);
            }
        }

        return supertypes;
    }

    private static void registerVanillaBiomes()
    {
        addTypes(Biomes.OCEAN,                            OCEAN                                                   );
        addTypes(Biomes.PLAINS,                           PLAINS                                                  );
        addTypes(Biomes.DESERT,                           HOT,      DRY,        SANDY                             );
        addTypes(Biomes.MOUNTAINS,                    MOUNTAIN, HILLS                                         );
        addTypes(Biomes.FOREST,                           FOREST                                                  );
        addTypes(Biomes.TAIGA,                            COLD,     CONIFEROUS, FOREST                            );
        addTypes(Biomes.SWAMP,                        WET,      SWAMP                                         );
        addTypes(Biomes.RIVER,                            RIVER                                                   );
        addTypes(Biomes.NETHER,                             HOT,      DRY,        NETHER                            );
        addTypes(Biomes.THE_END,                              COLD,     DRY,        END                               );
        addTypes(Biomes.FROZEN_OCEAN,                     COLD,     OCEAN,      SNOWY                             );
        addTypes(Biomes.FROZEN_RIVER,                     COLD,     RIVER,      SNOWY                             );
        addTypes(Biomes.SNOWY_TUNDRA,                       COLD,     SNOWY,      WASTELAND                         );
        addTypes(Biomes.SNOWY_MOUNTAINS,                    COLD,     SNOWY,      MOUNTAIN                          );
        addTypes(Biomes.MUSHROOM_FIELDS,                  MUSHROOM, RARE                                          );
        addTypes(Biomes.MUSHROOM_FIELD_SHORE,            MUSHROOM, BEACH,      RARE                              );
        addTypes(Biomes.BEACH,                            BEACH                                                   );
        addTypes(Biomes.DESERT_HILLS,                     HOT,      DRY,        SANDY,    HILLS                   );
        addTypes(Biomes.WOODED_HILLS,                     FOREST,   HILLS                                         );
        addTypes(Biomes.TAIGA_HILLS,                      COLD,     CONIFEROUS, FOREST,   HILLS                   );
        addTypes(Biomes.MOUNTAIN_EDGE,               MOUNTAIN                                                );
        addTypes(Biomes.JUNGLE,                           HOT,      WET,        DENSE,    JUNGLE                  );
        addTypes(Biomes.JUNGLE_HILLS,                     HOT,      WET,        DENSE,    JUNGLE,   HILLS         );
        addTypes(Biomes.JUNGLE_EDGE,                      HOT,      WET,        JUNGLE,   FOREST,   RARE          );
        addTypes(Biomes.DEEP_OCEAN,                       OCEAN                                                   );
        addTypes(Biomes.STONE_SHORE,                      BEACH                                                   );
        addTypes(Biomes.SNOWY_BEACH,                       COLD,     BEACH,      SNOWY                             );
        addTypes(Biomes.BIRCH_FOREST,                     FOREST                                                  );
        addTypes(Biomes.BIRCH_FOREST_HILLS,               FOREST,   HILLS                                         );
        addTypes(Biomes.DARK_FOREST,                    SPOOKY,   DENSE,      FOREST                            );
        addTypes(Biomes.SNOWY_TAIGA,                       COLD,     CONIFEROUS, FOREST,   SNOWY                   );
        addTypes(Biomes.SNOWY_TAIGA_HILLS,                 COLD,     CONIFEROUS, FOREST,   SNOWY,    HILLS         );
        addTypes(Biomes.GIANT_TREE_TAIGA,                    COLD,     CONIFEROUS, FOREST                            );
        addTypes(Biomes.GIANT_TREE_TAIGA_HILLS,              COLD,     CONIFEROUS, FOREST,   HILLS                   );
        addTypes(Biomes.WOODED_MOUNTAINS,         MOUNTAIN, FOREST,     SPARSE                            );
        addTypes(Biomes.SAVANNA,                          HOT,      SAVANNA,    PLAINS,   SPARSE                  );
        addTypes(Biomes.SAVANNA_PLATEAU,                  HOT,      SAVANNA,    PLAINS,   SPARSE,   RARE          );
        addTypes(Biomes.BADLANDS,                             MESA,     SANDY,  DRY                               );
        addTypes(Biomes.WOODED_BADLANDS_PLATEAU,                        MESA,     SANDY,    DRY,    SPARSE        );
        addTypes(Biomes.BADLANDS_PLATEAU,                  MESA,     SANDY,     DRY                               );
        addTypes(Biomes.SMALL_END_ISLANDS,                   END                                                     );
        addTypes(Biomes.END_MIDLANDS,                   END                                                     );
        addTypes(Biomes.END_HIGHLANDS,                   END                                                     );
        addTypes(Biomes.END_BARRENS,                   END                                                     );
        addTypes(Biomes.WARM_OCEAN,                   OCEAN,   HOT                                            );
        addTypes(Biomes.LUKEWARM_OCEAN,                   OCEAN                                                   );
        addTypes(Biomes.COLD_OCEAN,                   OCEAN,   COLD                                           );
        addTypes(Biomes.DEEP_WARM_OCEAN,                   OCEAN,   HOT                                            );
        addTypes(Biomes.DEEP_LUKEWARM_OCEAN,                   OCEAN                                                   );
        addTypes(Biomes.DEEP_COLD_OCEAN,                   OCEAN,   COLD                                           );
        addTypes(Biomes.DEEP_FROZEN_OCEAN,                   OCEAN,   COLD                                           );
        addTypes(Biomes.THE_VOID,                             VOID                                                    );
        addTypes(Biomes.SUNFLOWER_PLAINS,                   PLAINS,   RARE                                          );
        addTypes(Biomes.DESERT_LAKES,                   HOT,      DRY,        SANDY,    RARE                    );
        addTypes(Biomes.GRAVELLY_MOUNTAINS,            MOUNTAIN, SPARSE,     RARE                              );
        addTypes(Biomes.FLOWER_FOREST,                   FOREST,   HILLS,      RARE                              );
        addTypes(Biomes.TAIGA_MOUNTAINS,                    COLD,     CONIFEROUS, FOREST,   MOUNTAIN, RARE          );
        addTypes(Biomes.SWAMP_HILLS,                WET,      SWAMP,      HILLS,    RARE                    );
        addTypes(Biomes.ICE_SPIKES,                COLD,     SNOWY,      HILLS,    RARE                    );
        addTypes(Biomes.MODIFIED_JUNGLE,                   HOT,      WET,        DENSE,    JUNGLE,   MOUNTAIN, RARE);
        addTypes(Biomes.MODIFIED_JUNGLE_EDGE,              HOT,      SPARSE,     JUNGLE,   HILLS,    RARE          );
        addTypes(Biomes.TALL_BIRCH_FOREST,             FOREST,   DENSE,      HILLS,    RARE                    );
        addTypes(Biomes.TALL_BIRCH_HILLS,       FOREST,   DENSE,      MOUNTAIN, RARE                    );
        addTypes(Biomes.DARK_FOREST_HILLS,            SPOOKY,   DENSE,      FOREST,   MOUNTAIN, RARE          );
        addTypes(Biomes.SNOWY_TAIGA_MOUNTAINS,               COLD,     CONIFEROUS, FOREST,   SNOWY,    MOUNTAIN, RARE);
        addTypes(Biomes.GIANT_SPRUCE_TAIGA,            DENSE,    FOREST,     RARE                              );
        addTypes(Biomes.GIANT_SPRUCE_TAIGA_HILLS,      DENSE,    FOREST,     HILLS,    RARE                    );
        addTypes(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, MOUNTAIN, SPARSE,     RARE                              );
        addTypes(Biomes.SHATTERED_SAVANNA,                  HOT,      DRY,        SPARSE,   SAVANNA,  MOUNTAIN, RARE);
        addTypes(Biomes.SHATTERED_SAVANNA_PLATEAU,             HOT,      DRY,        SPARSE,   SAVANNA,  HILLS,    RARE);
        addTypes(Biomes.ERODED_BADLANDS,                     HOT,      DRY,        SPARSE,  MOUNTAIN, RARE);
        addTypes(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,                HOT,      DRY,        SPARSE,   HILLS,    RARE          );
        addTypes(Biomes.MODIFIED_BADLANDS_PLATEAU,          HOT,      DRY,        SPARSE,  MOUNTAIN, RARE);


        if (DEBUG)
        {
            StringBuilder buf = new StringBuilder();
            buf.append("BiomeDictionary:\n");
            Type.byName.forEach((name, type) -> buf.append("    ").append(type.name).append(": ").append(type.biomes.stream().map(b -> b.getRegistryName().toString()).collect(Collectors.joining(", "))).append('\n'));
            LOGGER.debug(buf.toString());
        }
    }
}
