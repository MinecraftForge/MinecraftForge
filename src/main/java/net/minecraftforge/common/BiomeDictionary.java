/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Registry;
import static net.minecraftforge.common.BiomeDictionary.Type.*;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.biome.Biome;

public class BiomeDictionary
{
    private static final boolean DEBUG = false;
    private static final Logger LOGGER = LogManager.getLogger();

    public static final class Type
    {
        private static final Map<String, Type> byName = new TreeMap<>();
        private static Collection<Type> allTypes = Collections.unmodifiableCollection(byName.values());

        /*Temperature-based tags. Specifying neither implies a biome is temperate*/
        public static final Type HOT = new Type("HOT");
        public static final Type COLD = new Type("COLD");

        //Tags specifying the amount of vegetation a biome has. Specifying neither implies a biome to have moderate amounts*/
        public static final Type SPARSE = new Type("SPARSE");
        public static final Type DENSE = new Type("DENSE");

        //Tags specifying how moist a biome is. Specifying neither implies the biome as having moderate humidity*/
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
        public static final Type MUSHROOM = new Type("MUSHROOM");
        public static final Type MAGICAL = new Type("MAGICAL");
        public static final Type RARE = new Type("RARE");
        public static final Type PLATEAU = new Type("PLATEAU");
        public static final Type MODIFIED = new Type("MODIFIED");

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
        public static final Type HILLS = new Type("HILLS");
        public static final Type SWAMP = new Type("SWAMP");
        public static final Type SANDY = new Type("SANDY");
        public static final Type SNOWY = new Type("SNOWY");
        public static final Type WASTELAND = new Type("WASTELAND");
        public static final Type BEACH = new Type("BEACH");
        public static final Type VOID = new Type("VOID");
        public static final Type UNDERGROUND = new Type("UNDERGROUND");

        /*Mountain related tags*/
        public static final Type PEAK = new Type("PEAK");
        public static final Type SLOPE = new Type("SLOPE");
        public static final Type MOUNTAIN = new Type("MOUNTAIN", PEAK, SLOPE);

        /*Tags specifying the dimension a biome generates in. Specifying none implies a biome that generates in a modded dimension*/
        public static final Type OVERWORLD = new Type("OVERWORLD");
        public static final Type NETHER = new Type("NETHER");
        public static final Type END = new Type("END");

        private final String name;
        private final List<Type> subTypes;
        private final Set<ResourceKey<Biome>> biomes = new HashSet<>();
        private final Set<ResourceKey<Biome>> biomesUn = Collections.unmodifiableSet(biomes);

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
         * Checks if a type instance exists for a given name. Does not have any side effects if a type does not already exist.
         * This can be used for checking if a user-defined type is valid, for example, in a codec which accepts biome dictionary names.
         * @param name The name.
         * @return {@code true} if a type exists with this name.
         *
         * @see #getType(String, Type...) #getType for type naming conventions.
         */
        public static boolean hasType(String name)
        {
            return byName.containsKey(name.toUpperCase());
        }

        /**
         * @return An unmodifiable collection of all current biome types.
         */
        public static Collection<Type> getAll()
        {
            return allTypes;
        }

        @Nullable
        public static Type fromVanilla(Biome.BiomeCategory category)
        {
            if (category == Biome.BiomeCategory.NONE)
                return null;
            if (category == Biome.BiomeCategory.THEEND)
                return VOID;
            return getType(category.name());
        }
    }

    private static final Map<ResourceKey<Biome>, BiomeInfo> biomeInfoMap = new HashMap<>();

    private static class BiomeInfo
    {
        private final Set<Type> types = new HashSet<Type>();
        private final Set<Type> typesUn = Collections.unmodifiableSet(this.types);
    }

    public static void init() {}
    static
    {
        registerVanillaBiomes();
    }

    /**
     * Adds the given types to the biome.
     *
     */
    public static void addTypes(ResourceKey<Biome> biome, Type... types)
    {
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
    public static Set<ResourceKey<Biome>> getBiomes(Type type)
    {
        return type.biomesUn;
    }

    /**
     * Gets the set of types that have been added to the given biome.
     *
     */
    @Nonnull
    public static Set<Type> getTypes(ResourceKey<Biome> biome)
    {
        return getBiomeInfo(biome).typesUn;
    }

    /**
     * Checks if the two given biomes have types in common.
     *
     * @return returns true if a common type is found, false otherwise
     */
    public static boolean areSimilar(ResourceKey<Biome> biomeA, ResourceKey<Biome> biomeB)
    {
        Set<Type> typesA = getTypes(biomeA);
        Set<Type> typesB = getTypes(biomeB);
        return typesA.stream().anyMatch(typesB::contains);
    }

    /**
     * Checks if the given type has been added to the given biome.
     *
     */
    public static boolean hasType(ResourceKey<Biome> biome, Type type)
    {
        return getTypes(biome).contains(type);
    }

    /**
     * Checks if any type has been added to the given biome.
     *
     */
    public static boolean hasAnyType(ResourceKey<Biome> biome)
    {
        return !getBiomeInfo(biome).types.isEmpty();
    }

    //Internal implementation
    private static BiomeInfo getBiomeInfo(ResourceKey<Biome> biome)
    {
        return biomeInfoMap.computeIfAbsent(biome, k -> new BiomeInfo());
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
        addTypes(Biomes.OCEAN, OCEAN, OVERWORLD);
        addTypes(Biomes.PLAINS, PLAINS, OVERWORLD);
        addTypes(Biomes.DESERT, HOT, DRY, SANDY, OVERWORLD);
        addTypes(Biomes.WINDSWEPT_HILLS, HILLS, OVERWORLD);
        addTypes(Biomes.FOREST, FOREST, OVERWORLD);
        addTypes(Biomes.TAIGA, COLD, CONIFEROUS, FOREST, OVERWORLD);
        addTypes(Biomes.SWAMP, WET, SWAMP, OVERWORLD);
        addTypes(Biomes.RIVER, RIVER, OVERWORLD);
        addTypes(Biomes.NETHER_WASTES, HOT, DRY, NETHER);
        addTypes(Biomes.THE_END, COLD, DRY, END);
        addTypes(Biomes.FROZEN_OCEAN, COLD, OCEAN, SNOWY, OVERWORLD);
        addTypes(Biomes.FROZEN_RIVER, COLD, RIVER, SNOWY, OVERWORLD);
        addTypes(Biomes.SNOWY_PLAINS, COLD, SNOWY, WASTELAND, OVERWORLD);
        addTypes(Biomes.MUSHROOM_FIELDS, MUSHROOM, RARE, OVERWORLD);
        addTypes(Biomes.BEACH, BEACH, OVERWORLD);
        addTypes(Biomes.JUNGLE, HOT, WET, DENSE, JUNGLE, OVERWORLD);
        addTypes(Biomes.SPARSE_JUNGLE, HOT, WET, JUNGLE, FOREST, RARE, OVERWORLD);
        addTypes(Biomes.DEEP_OCEAN, OCEAN, OVERWORLD);
        addTypes(Biomes.STONY_SHORE, BEACH, OVERWORLD);
        addTypes(Biomes.SNOWY_BEACH, COLD, BEACH, SNOWY, OVERWORLD);
        addTypes(Biomes.BIRCH_FOREST, FOREST, OVERWORLD);
        addTypes(Biomes.DARK_FOREST, SPOOKY, DENSE, FOREST, OVERWORLD);
        addTypes(Biomes.SNOWY_TAIGA, COLD, CONIFEROUS, FOREST, SNOWY, OVERWORLD);
        addTypes(Biomes.OLD_GROWTH_PINE_TAIGA, COLD, CONIFEROUS, FOREST, OVERWORLD);
        addTypes(Biomes.WINDSWEPT_FOREST, HILLS, FOREST, SPARSE, OVERWORLD);
        addTypes(Biomes.SAVANNA, HOT, SAVANNA, PLAINS, SPARSE, OVERWORLD);
        addTypes(Biomes.SAVANNA_PLATEAU, HOT, SAVANNA, PLAINS, SPARSE, RARE, OVERWORLD, SLOPE, PLATEAU);
        addTypes(Biomes.BADLANDS, MESA, SANDY, DRY, OVERWORLD);
        addTypes(Biomes.WOODED_BADLANDS, MESA, SANDY, DRY, SPARSE, OVERWORLD, SLOPE, PLATEAU);
        addTypes(Biomes.MEADOW, PLAINS, PLATEAU, SLOPE, OVERWORLD);
        addTypes(Biomes.GROVE, COLD, CONIFEROUS, FOREST, SNOWY, SLOPE, OVERWORLD);
        addTypes(Biomes.SNOWY_SLOPES, COLD, SPARSE, SNOWY, SLOPE, OVERWORLD);
        addTypes(Biomes.JAGGED_PEAKS, COLD, SPARSE, SNOWY, PEAK, OVERWORLD);
        addTypes(Biomes.FROZEN_PEAKS, COLD, SPARSE, SNOWY, PEAK, OVERWORLD);
        addTypes(Biomes.STONY_PEAKS, HOT, PEAK, OVERWORLD);
        addTypes(Biomes.SMALL_END_ISLANDS, END);
        addTypes(Biomes.END_MIDLANDS, END);
        addTypes(Biomes.END_HIGHLANDS, END);
        addTypes(Biomes.END_BARRENS, END);
        addTypes(Biomes.WARM_OCEAN, OCEAN, HOT, OVERWORLD);
        addTypes(Biomes.LUKEWARM_OCEAN, OCEAN, OVERWORLD);
        addTypes(Biomes.COLD_OCEAN, OCEAN, COLD, OVERWORLD);
        addTypes(Biomes.DEEP_LUKEWARM_OCEAN, OCEAN, OVERWORLD);
        addTypes(Biomes.DEEP_COLD_OCEAN, OCEAN, COLD, OVERWORLD);
        addTypes(Biomes.DEEP_FROZEN_OCEAN, OCEAN, COLD, OVERWORLD);
        addTypes(Biomes.THE_VOID, VOID);
        addTypes(Biomes.SUNFLOWER_PLAINS, PLAINS, RARE, OVERWORLD);
        addTypes(Biomes.WINDSWEPT_GRAVELLY_HILLS, HILLS, SPARSE, RARE, OVERWORLD);
        addTypes(Biomes.FLOWER_FOREST, FOREST, RARE, OVERWORLD);
        addTypes(Biomes.ICE_SPIKES, COLD, SNOWY, RARE, OVERWORLD);
        addTypes(Biomes.OLD_GROWTH_BIRCH_FOREST, FOREST, DENSE, RARE, OVERWORLD);
        addTypes(Biomes.OLD_GROWTH_SPRUCE_TAIGA, DENSE, FOREST, RARE, OVERWORLD);
        addTypes(Biomes.WINDSWEPT_SAVANNA, HOT, DRY, SPARSE, SAVANNA, HILLS, RARE, OVERWORLD);
        addTypes(Biomes.ERODED_BADLANDS, MESA, HOT, DRY, SPARSE, RARE, OVERWORLD);
        addTypes(Biomes.BAMBOO_JUNGLE, HOT, WET, RARE, JUNGLE, OVERWORLD);
        addTypes(Biomes.LUSH_CAVES, UNDERGROUND, LUSH, WET, OVERWORLD);
        addTypes(Biomes.DRIPSTONE_CAVES, UNDERGROUND, SPARSE, OVERWORLD);
        addTypes(Biomes.SOUL_SAND_VALLEY, HOT, DRY, NETHER);
        addTypes(Biomes.CRIMSON_FOREST, HOT, DRY, NETHER, FOREST);
        addTypes(Biomes.WARPED_FOREST, HOT, DRY, NETHER, FOREST);
        addTypes(Biomes.BASALT_DELTAS, HOT, DRY, NETHER);

        if (DEBUG)
        {
            StringBuilder buf = new StringBuilder();
            buf.append("BiomeDictionary:\n");
            Type.byName.forEach((name, type) ->
                buf.append("    ").append(type.name).append(": ")
                .append(type.biomes.stream()
                    .map(ResourceKey::location)
                    .sorted((a,b) -> a.compareNamespaced(b))
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))
                )
                .append('\n')
            );

            boolean missing = false;
            List<ResourceKey<Biome>> all = StreamSupport.stream(ForgeRegistries.BIOMES.spliterator(), false)
                .map(b -> ResourceKey.create(Registry.BIOME_REGISTRY, b.getRegistryName()))
                .sorted().collect(Collectors.toList());

            for (ResourceKey<Biome> key : all) {
                if (!biomeInfoMap.containsKey(key)) {
                    if (!missing) {
                        buf.append("Missing:\n");
                        missing = true;
                    }
                    buf.append("    ").append(key.location()).append('\n');
                }
            }
            LOGGER.debug(buf.toString());
        }
    }
}
