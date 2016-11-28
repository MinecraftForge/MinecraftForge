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

import net.minecraftforge.fml.common.FMLLog;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.DeferredBiomeDecorator;
import static net.minecraftforge.common.BiomeDictionary.Type.*;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.google.common.base.Preconditions;

public class BiomeDictionary
{
    public enum Type
    {
        /*Temperature-based tags. Specifying neither implies a biome is temperate*/
        HOT,
        COLD,
        /*Tags specifying the amount of vegetation a biome has. Specifying neither implies a biome to have moderate amounts*/
        SPARSE,
        DENSE,
        /*Tags specifying how moist a biome is. Specifying neither implies the biome as having moderate humidity*/
        WET,
        DRY,
        /*Tree-based tags, SAVANNA refers to dry, desert-like trees (Such as Acacia), CONIFEROUS refers to snowy trees (Such as Spruce) and JUNGLE refers to jungle trees.
         * Specifying no tag implies a biome has temperate trees (Such as Oak)*/
        SAVANNA,
        CONIFEROUS,
        JUNGLE,

        /*Tags specifying the nature of a biome*/
        SPOOKY,
        DEAD,
        LUSH,
        NETHER,
        END,
        MUSHROOM,
        MAGICAL,

        OCEAN,
        RIVER,
        /**A general tag for all water-based biomes. Shown as present if OCEAN or RIVER are.**/
        WATER(OCEAN, RIVER),

        /*Generic types which a biome can be*/
        MESA,
        FOREST,
        PLAINS,
        MOUNTAIN,
        HILLS,
        SWAMP,
        SANDY,
        SNOWY,
        WASTELAND,
        BEACH;

        private List<Type> subTags;

        private Type(Type... subTags)
        {
            this.subTags = Arrays.asList(subTags);
        }

        private boolean hasSubTags()
        {
            return subTags != null && !subTags.isEmpty();
        }

        /**
         * Retrieves a Type value by name,
         * if one does not exist already it creates one.
         * This can be used as intermediate measure for modders to
         * add their own category of Biome.
         *
         * There are NO naming conventions besides:
         *   MUST be all upper case (enforced by name.toUpper())
         *   NO Special characters. {Unenforced, just don't be a pain, if it becomes a issue I WILL
         *                             make this RTE with no worry about backwards compatibility}
         *
         * Note: For performance sake, the return value of this function SHOULD be cached.
         * Two calls with the same name SHOULD return the same value.
         *
         *
         * @param name The name of this Type
         * @return An instance of Type for this name.
         */
        public static Type getType(String name, Type... subTypes)
        {
            name = name.toUpperCase();
            for (Type t : values())
            {
                if (t.name().equals(name))
                    return t;
            }
            Type ret = EnumHelper.addEnum(Type.class, name, new Class[]{Type[].class}, new Object[]{subTypes});
            if (ret.ordinal() >= typeInfoList.length)
            {
                typeInfoList = Arrays.copyOf(typeInfoList, ret.ordinal()+1);
            }
            for(BiomeInfo bInfo:biomeInfoMap.values())
            {
                if(bInfo != null)
                {
                    EnumSet<Type> oldSet = bInfo.typeList;
                    bInfo.typeList = EnumSet.noneOf(Type.class);
                    bInfo.typeList.addAll(oldSet);
                }
            }
            return ret;
        }
    }

    private static HashMap<ResourceLocation, BiomeInfo> biomeInfoMap = new HashMap<ResourceLocation, BiomeInfo>();
    @SuppressWarnings("unchecked")
    private static ArrayList<Biome>[] typeInfoList = new ArrayList[Type.values().length];

    private static class BiomeInfo
    {
        public EnumSet<Type> typeList;

        public BiomeInfo(Type[] types)
        {
            typeList = EnumSet.noneOf(Type.class);
            for(Type t : types)
            {
                typeList.add(t);
            }
        }
    }

    static
    {
        registerVanillaBiomes();
    }

    /**
     * Registers a biome with a specific biome type
     *
     * @param biome the biome to be registered
     * @param types the types to register the biome as
     * @return returns true if the biome was registered successfully
     */
    public static boolean registerBiomeType(Biome biome, Type ... types)
    {
        Preconditions.checkArgument(ForgeRegistries.BIOMES.containsValue(biome), "Cannot register biome types for unregistered biome");

        types = listSubTags(types);

        for (Type type : types)
        {
            if (typeInfoList[type.ordinal()] == null)
            {
                typeInfoList[type.ordinal()] = new ArrayList<Biome>();
            }

                typeInfoList[type.ordinal()].add(biome);
            }

        if (!isBiomeRegistered(biome))
        {
            ResourceLocation location = ForgeRegistries.BIOMES.getKey(biome);
            biomeInfoMap.put(location, new BiomeInfo(types));
        }
        else
        {
            for (Type type : types)
            {
                getBiomeInfo(biome).typeList.add(type);
            }
        }

        return true;
    }

    /**
     * Returns a list of biomes registered with a specific type
     *
     * @param type the Type to look for
     * @return a list of biomes of the specified type, null if there are none
     */
    public static Biome[] getBiomesForType(Type type)
    {
        if(typeInfoList[type.ordinal()] != null)
        {
            return typeInfoList[type.ordinal()].toArray(new Biome[0]);
        }

        return new Biome[0];
    }

    /**
     * Gets a list of Types that a specific biome is registered with
     *
     * @param biome the biome to check
     * @return the list of types, null if there are none
     */
    public static Type[] getTypesForBiome(Biome biome)
    {
        checkRegistration(biome);
        return getBiomeInfo(biome).typeList.toArray(new Type[0]);
    }

    /**
     * Checks to see if two biomes are registered as having the same type
     *
     * @param biomeA
     * @param biomeB
     * @return returns true if a common type is found, false otherwise
     */
    public static boolean areBiomesEquivalent(Biome biomeA, Biome biomeB)
    {
        checkRegistration(biomeA);
        checkRegistration(biomeB);

        for(Type type : getTypesForBiome(biomeA))
        {
            if(containsType(getBiomeInfo(biomeB), type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see if the given biome is registered as being a specific type
     *
     * @param biome the biome to be considered
     * @param type the type to check for
     * @return returns true if the biome is registered as being of type type, false otherwise
     */
    public static boolean isBiomeOfType(Biome biome, Type type)
    {
        checkRegistration(biome);
        return containsType(getBiomeInfo(biome), type);
    }

    /**
     * Checks to see if the given biome has been registered as being of any type
     * @param biome the biome to consider
     * @return returns true if the biome has been registered, false otherwise
     */
    public static boolean isBiomeRegistered(Biome biome)
    {
        return biomeInfoMap.containsKey(ForgeRegistries.BIOMES.getKey(biome));
    }

    public static void registerAllBiomes()
    {
        FMLLog.warning("Redundant call to BiomeDictionary.registerAllBiomes ignored");
    }
    /**
     * Loops through the biome list and automatically adds tags to any biome that does not have any
     * This is called by Forge at postinit time. It will additionally dispatch any deferred decorator
     * creation events.
     *
     * DO NOT call this during world generation
     */
    public static void registerAllBiomesAndGenerateEvents()
    {
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            if (biome.theBiomeDecorator instanceof DeferredBiomeDecorator)
            {
                DeferredBiomeDecorator decorator = (DeferredBiomeDecorator)biome.theBiomeDecorator;
                decorator.fireCreateEventAndReplace(biome);
            }

            checkRegistration(biome);
        }
    }

    /**
     * Automatically looks for and registers a given biome with appropriate tags
     * This method is called automatically if a biome has not been registered with any tags,
     * And another method requests information about it
     *
     * @param biome the biome to be considered
     */
    public static void makeBestGuess(Biome biome)
    {
        if (biome.theBiomeDecorator.treesPerChunk >= 3)
        {
            if (biome.isHighHumidity() && biome.getTemperature() >= 0.9F)
            {
                BiomeDictionary.registerBiomeType(biome, JUNGLE);
            }
            else if (!biome.isHighHumidity())
            {
                BiomeDictionary.registerBiomeType(biome, FOREST);

                if (biome.getTemperature() <= 0.2f)
                {
                    BiomeDictionary.registerBiomeType(biome, CONIFEROUS);
                }
            }
        }
        else if(biome.getHeightVariation() <= 0.3F && biome.getHeightVariation() >= 0.0F)
        {
            if(!biome.isHighHumidity() || biome.getBaseHeight() >= 0.0F)
            {
                BiomeDictionary.registerBiomeType(biome, PLAINS);
            }
        }

        if (biome.getRainfall() > 0.85f)
        {
            BiomeDictionary.registerBiomeType(biome, WET);
        }

        if (biome.getRainfall() < 0.15f)
        {
            BiomeDictionary.registerBiomeType(biome, DRY);
        }

        if (biome.getTemperature() > 0.85f)
        {
            BiomeDictionary.registerBiomeType(biome, HOT);
        }

        if (biome.getTemperature() < 0.15f)
        {
            BiomeDictionary.registerBiomeType(biome, COLD);
        }

        if (biome.theBiomeDecorator.treesPerChunk > 0 && biome.theBiomeDecorator.treesPerChunk < 3)
        {
            BiomeDictionary.registerBiomeType(biome, SPARSE);
        }
        else if (biome.theBiomeDecorator.treesPerChunk >= 10)
        {
            BiomeDictionary.registerBiomeType(biome, DENSE);
        }

        if (biome.isHighHumidity() && biome.getBaseHeight() < 0.0F && (biome.getHeightVariation() <= 0.3F && biome.getHeightVariation() >= 0.0F))
        {
            BiomeDictionary.registerBiomeType(biome, SWAMP);
        }

        if (biome.getBaseHeight() <= -0.5F)
        {
            if (biome.getHeightVariation() == 0.0F)
            {
                BiomeDictionary.registerBiomeType(biome, RIVER);
            }
            else
            {
                BiomeDictionary.registerBiomeType(biome, OCEAN);
            }
        }

        if (biome.getHeightVariation() >= 0.4F && biome.getHeightVariation() < 1.5F)
        {
            BiomeDictionary.registerBiomeType(biome, HILLS);
        }

        if (biome.getHeightVariation() >= 1.5F)
        {
            BiomeDictionary.registerBiomeType(biome, MOUNTAIN);
        }

        if (biome.getEnableSnow())
        {
            BiomeDictionary.registerBiomeType(biome, SNOWY);
        }

        if (biome.topBlock != Blocks.SAND && biome.getTemperature() >= 1.0f && biome.getRainfall() < 0.2f)
        {
            BiomeDictionary.registerBiomeType(biome, SAVANNA);
        }

        if (biome.topBlock == Blocks.SAND)
        {
            BiomeDictionary.registerBiomeType(biome, SANDY);
        }
        else if (biome.topBlock == Blocks.MYCELIUM)
        {
            BiomeDictionary.registerBiomeType(biome, MUSHROOM);
        }
        if (biome.fillerBlock == Blocks.HARDENED_CLAY)
        {
            BiomeDictionary.registerBiomeType(biome, MESA);
        }
    }

    //Internal implementation
    private static BiomeInfo getBiomeInfo(Biome biome)
    {
        return biomeInfoMap.get(ForgeRegistries.BIOMES.getKey(biome));
    }

    private static void checkRegistration(Biome biome)
    {
        if(!isBiomeRegistered(biome))
        {
            makeBestGuess(biome);
        }
    }

    private static boolean containsType(BiomeInfo info, Type type)
    {
        if (type.hasSubTags())
        {
            for (Type remappedType : listSubTags(type))
            {
                if (info.typeList.contains(remappedType)) return true;
            }

            return false;
        }

        return info.typeList.contains(type);
    }

    private static Type[] listSubTags(Type... types)
    {
        List<Type> subTags = new ArrayList<Type>();

        for (Type type : types)
        {
            if (type.hasSubTags()) subTags.addAll(type.subTags);
            else subTags.add(type);
        }

        return subTags.toArray(new Type[subTags.size()]);
    }

    private static void registerVanillaBiomes()
    {
        registerBiomeType(Biomes.OCEAN,                    OCEAN                                        );
        registerBiomeType(Biomes.PLAINS,                   PLAINS                                       );
        registerBiomeType(Biomes.DESERT,                   HOT,      DRY,        SANDY                  );
        registerBiomeType(Biomes.EXTREME_HILLS,            MOUNTAIN, HILLS                              );
        registerBiomeType(Biomes.FOREST,                   FOREST                                       );
        registerBiomeType(Biomes.TAIGA,                    COLD,     CONIFEROUS, FOREST                 );
        registerBiomeType(Biomes.TAIGA_HILLS,              COLD,     CONIFEROUS, FOREST,   HILLS        );
        registerBiomeType(Biomes.SWAMPLAND,                WET,      SWAMP                              );
        registerBiomeType(Biomes.RIVER,                    RIVER                                        );
        registerBiomeType(Biomes.FROZEN_OCEAN,             COLD,     OCEAN,      SNOWY                  );
        registerBiomeType(Biomes.FROZEN_RIVER,             COLD,     RIVER,      SNOWY                  );
        registerBiomeType(Biomes.ICE_PLAINS,               COLD,     SNOWY,      WASTELAND              );
        registerBiomeType(Biomes.ICE_MOUNTAINS,            COLD,     SNOWY,      MOUNTAIN               );
        registerBiomeType(Biomes.BEACH,                    BEACH                                        );
        registerBiomeType(Biomes.DESERT_HILLS,             HOT,      DRY,        SANDY,    HILLS        );
        registerBiomeType(Biomes.JUNGLE,                   HOT,      WET,        DENSE,    JUNGLE       );
        registerBiomeType(Biomes.JUNGLE_HILLS,             HOT,      WET,        DENSE,    JUNGLE, HILLS);
        registerBiomeType(Biomes.FOREST_HILLS,             FOREST,   HILLS                              );
        registerBiomeType(Biomes.SKY,                      COLD,     DRY,        END                    );
        registerBiomeType(Biomes.HELL,                     HOT,      DRY,        NETHER                 );
        registerBiomeType(Biomes.MUSHROOM_ISLAND,          MUSHROOM                                     );
        registerBiomeType(Biomes.EXTREME_HILLS_EDGE,       MOUNTAIN                                     );
        registerBiomeType(Biomes.MUSHROOM_ISLAND_SHORE,    MUSHROOM, BEACH                              );
        registerBiomeType(Biomes.JUNGLE_EDGE,              HOT,      WET,        JUNGLE,   FOREST       );
        registerBiomeType(Biomes.DEEP_OCEAN,               OCEAN                                        );
        registerBiomeType(Biomes.STONE_BEACH,              BEACH                                        );
        registerBiomeType(Biomes.COLD_BEACH,               COLD,     BEACH,      SNOWY                  );
        registerBiomeType(Biomes.BIRCH_FOREST,             FOREST                                       );
        registerBiomeType(Biomes.BIRCH_FOREST_HILLS,       FOREST,   HILLS                              );
        registerBiomeType(Biomes.ROOFED_FOREST,            SPOOKY,   DENSE,      FOREST                 );
        registerBiomeType(Biomes.COLD_TAIGA,               COLD,     CONIFEROUS, FOREST,   SNOWY        );
        registerBiomeType(Biomes.COLD_TAIGA_HILLS,         COLD,     CONIFEROUS, FOREST,   SNOWY,  HILLS);
        registerBiomeType(Biomes.REDWOOD_TAIGA,            COLD,     CONIFEROUS, FOREST                 );
        registerBiomeType(Biomes.REDWOOD_TAIGA_HILLS,      COLD,     CONIFEROUS, FOREST,   HILLS        );
        registerBiomeType(Biomes.EXTREME_HILLS_WITH_TREES, MOUNTAIN, FOREST,     SPARSE                 );
        registerBiomeType(Biomes.SAVANNA,                  HOT,      SAVANNA,    PLAINS,   SPARSE       );
        registerBiomeType(Biomes.SAVANNA_PLATEAU,          HOT,      SAVANNA,    PLAINS,   SPARSE       );
        registerBiomeType(Biomes.MESA,                     MESA,     SANDY                              );
        registerBiomeType(Biomes.MESA_ROCK,                MESA,     SPARSE,     SANDY                  );
        registerBiomeType(Biomes.MESA_CLEAR_ROCK,          MESA,     SANDY                              );
    }
}
