package net.minecraftforge.common;

import java.util.*;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.DeferredBiomeDecorator;
import static net.minecraft.world.biome.BiomeGenBase.*;
import static net.minecraftforge.common.BiomeDictionary.Type.*;

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
        BEACH,

        /*Deprecated tags, kept for compatibility*/
        @Deprecated
        /**Replaced by SANDY**/
        DESERT(SANDY),
        @Deprecated
        /**Replaced by SNOWY**/
        FROZEN(SNOWY);

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
         * This can be used as interm measure for modders to
         * add there own category of Biome.
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
            for(BiomeInfo bInfo:biomeList)
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

    private static final int BIOME_LIST_SIZE = BiomeGenBase.getBiomeGenArray().length;
    private static BiomeInfo[] biomeList = new BiomeInfo[BIOME_LIST_SIZE];
    @SuppressWarnings("unchecked")
    private static ArrayList<BiomeGenBase>[] typeInfoList = new ArrayList[Type.values().length];

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
     * @param type the type to register the biome as
     * @return returns true if the biome was registered successfully
     */
    public static boolean registerBiomeType(BiomeGenBase biome, Type ... types)
    {
        types = listSubTags(types);

        if(BiomeGenBase.getBiomeGenArray()[biome.biomeID] != null)
        {
            for(Type type : types)
            {
                if(typeInfoList[type.ordinal()] == null)
                {
                    typeInfoList[type.ordinal()] = new ArrayList<BiomeGenBase>();
                }

                typeInfoList[type.ordinal()].add(biome);
            }

            if(biomeList[biome.biomeID] == null)
            {
                biomeList[biome.biomeID] = new BiomeInfo(types);
            }
            else
            {
                for(Type type : types)
                {
                    biomeList[biome.biomeID].typeList.add(type);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Returns a list of biomes registered with a specific type
     *
     * @param type the Type to look for
     * @return a list of biomes of the specified type, null if there are none
     */
    public static BiomeGenBase[] getBiomesForType(Type type)
    {
        if(typeInfoList[type.ordinal()] != null)
        {
            return (BiomeGenBase[])typeInfoList[type.ordinal()].toArray(new BiomeGenBase[0]);
        }

        return new BiomeGenBase[0];
    }

    /**
     * Gets a list of Types that a specific biome is registered with
     *
     * @param biome the biome to check
     * @return the list of types, null if there are none
     */
    public static Type[] getTypesForBiome(BiomeGenBase biome)
    {
        checkRegistration(biome);

        if(biomeList[biome.biomeID] != null)
        {
            return (Type[])biomeList[biome.biomeID].typeList.toArray(new Type[0]);
        }

        return new Type[0];
    }

    /**
     * Checks to see if two biomes are registered as having the same type
     *
     * @param biomeA
     * @param biomeB
     * @return returns true if a common type is found, false otherwise
     */
    public static boolean areBiomesEquivalent(BiomeGenBase biomeA, BiomeGenBase biomeB)
    {
        int a = biomeA.biomeID;
        int b = biomeB.biomeID;

        checkRegistration(biomeA);
        checkRegistration(biomeB);

        if(biomeList[a] != null && biomeList[b] != null)
        {
            for(Type type : biomeList[a].typeList)
            {
                if(containsType(biomeList[b], type))
                {
                    return true;
                }
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
    public static boolean isBiomeOfType(BiomeGenBase biome, Type type)
    {
        checkRegistration(biome);

        if(biomeList[biome.biomeID] != null)
        {
            return containsType(biomeList[biome.biomeID], type);
        }

        return false;
    }

    /**
     * Checks to see if the given biome has been registered as being of any type
     * @param biome the biome to consider
     * @return returns true if the biome has been registered, false otherwise
     */
    public static boolean isBiomeRegistered(BiomeGenBase biome)
    {
        return biomeList[biome.biomeID] != null;
    }

    public static boolean isBiomeRegistered(int biomeID)
    {
        return biomeList[biomeID] != null;
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
        for(int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++)
        {
            BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[i];

            if(biome == null)
            {
                continue;
            }

            if (biome.theBiomeDecorator instanceof DeferredBiomeDecorator)
            {
                DeferredBiomeDecorator decorator = (DeferredBiomeDecorator) biome.theBiomeDecorator;
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
    public static void makeBestGuess(BiomeGenBase biome)
    {
        if (biome.theBiomeDecorator.treesPerChunk >= 3)
        {
            if (biome.isHighHumidity() && biome.temperature >= 0.9F)
            {
                BiomeDictionary.registerBiomeType(biome, JUNGLE);
            }
            else if (!biome.isHighHumidity())
            {
                BiomeDictionary.registerBiomeType(biome, FOREST);

                if (biome.temperature <= 0.2f)
                {
                    BiomeDictionary.registerBiomeType(biome, CONIFEROUS);
                }
            }
        }
        else if(biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F)
        {
            if(!biome.isHighHumidity() || biome.minHeight >= 0.0F)
            {
                BiomeDictionary.registerBiomeType(biome, PLAINS);
            }
        }

        if (biome.rainfall > 0.85f)
        {
            BiomeDictionary.registerBiomeType(biome, WET);
        }

        if (biome.rainfall < 0.15f)
        {
            BiomeDictionary.registerBiomeType(biome, DRY);
        }

        if (biome.temperature > 0.85f)
        {
            BiomeDictionary.registerBiomeType(biome, HOT);
        }

        if (biome.temperature < 0.15f)
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

        if (biome.isHighHumidity() && biome.minHeight < 0.0F && (biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F))
        {
            BiomeDictionary.registerBiomeType(biome, SWAMP);
        }

        if (biome.minHeight <= -0.5F)
        {
            if (biome.maxHeight == 0.0F)
            {
                BiomeDictionary.registerBiomeType(biome, RIVER);
            }
            else
            {
                BiomeDictionary.registerBiomeType(biome, OCEAN);
            }
        }

        if (biome.maxHeight >= 0.4F && biome.maxHeight < 1.5F)
        {
            BiomeDictionary.registerBiomeType(biome, HILLS);
        }

        if (biome.maxHeight >= 1.5F)
        {
            BiomeDictionary.registerBiomeType(biome, MOUNTAIN);
        }

        if (biome.getEnableSnow())
        {
            BiomeDictionary.registerBiomeType(biome, SNOWY);
        }

        if (biome.topBlock != Blocks.sand && biome.temperature >= 1.0f && biome.rainfall < 0.2f)
        {
            BiomeDictionary.registerBiomeType(biome, SAVANNA);
        }

        if (biome.topBlock == Blocks.sand )
        {
            BiomeDictionary.registerBiomeType(biome, SANDY);
        }
        else if (biome.topBlock == Blocks.mycelium)
        {
            BiomeDictionary.registerBiomeType(biome, MUSHROOM);
        }
        if (biome.fillerBlock == Blocks.hardened_clay)
        {
            BiomeDictionary.registerBiomeType(biome, MESA);
        }
    }

    //Internal implementation
    private static void checkRegistration(BiomeGenBase biome)
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
        registerBiomeType(ocean,               OCEAN                                        );
        registerBiomeType(plains,              PLAINS                                       );
        registerBiomeType(desert,              HOT,      DRY,        SANDY                  );
        registerBiomeType(extremeHills,        MOUNTAIN, HILLS                              );
        registerBiomeType(forest,              FOREST                                       );
        registerBiomeType(taiga,               COLD,     CONIFEROUS, FOREST                 );
        registerBiomeType(taigaHills,          COLD,     CONIFEROUS, FOREST,   HILLS        );
        registerBiomeType(swampland,           WET,      SWAMP                              );
        registerBiomeType(river,               RIVER                                        );
        registerBiomeType(frozenOcean,         COLD,     OCEAN,      SNOWY                  );
        registerBiomeType(frozenRiver,         COLD,     RIVER,      SNOWY                  );
        registerBiomeType(icePlains,           COLD,     SNOWY,      WASTELAND              );
        registerBiomeType(iceMountains,        COLD,     SNOWY,      MOUNTAIN               );
        registerBiomeType(beach,               BEACH                                        );
        registerBiomeType(desertHills,         HOT,      DRY,        SANDY,    HILLS        );
        registerBiomeType(jungle,              HOT,      WET,        DENSE,    JUNGLE       );
        registerBiomeType(jungleHills,         HOT,      WET,        DENSE,    JUNGLE, HILLS);
        registerBiomeType(forestHills,         FOREST,   HILLS                              );
        registerBiomeType(sky,                 COLD,     DRY,        END                    );
        registerBiomeType(hell,                HOT,      DRY,        NETHER                 );
        registerBiomeType(mushroomIsland,      MUSHROOM                                     );
        registerBiomeType(extremeHillsEdge,    MOUNTAIN                                     );
        registerBiomeType(mushroomIslandShore, MUSHROOM, BEACH                              );
        registerBiomeType(jungleEdge,          HOT,      WET,        JUNGLE,   FOREST       );
        registerBiomeType(deepOcean,           OCEAN                                        );
        registerBiomeType(stoneBeach,          BEACH                                        );
        registerBiomeType(coldBeach,           COLD,     BEACH,      SNOWY                  );
        registerBiomeType(birchForest,         FOREST                                       );
        registerBiomeType(birchForestHills,    FOREST,   HILLS                              );
        registerBiomeType(roofedForest,        SPOOKY,   DENSE,      FOREST                 );
        registerBiomeType(coldTaiga,           COLD,     CONIFEROUS, FOREST,   SNOWY        );
        registerBiomeType(coldTaigaHills,      COLD,     CONIFEROUS, FOREST,   SNOWY,  HILLS);
        registerBiomeType(megaTaiga,           COLD,     CONIFEROUS, FOREST                 );
        registerBiomeType(megaTaigaHills,      COLD,     CONIFEROUS, FOREST,   HILLS        );
        registerBiomeType(extremeHillsPlus,    MOUNTAIN, FOREST,     SPARSE                 );
        registerBiomeType(savanna,             HOT,      SAVANNA,    PLAINS,   SPARSE       );
        registerBiomeType(savannaPlateau,      HOT,      SAVANNA,    PLAINS,   SPARSE       );
        registerBiomeType(mesa,                MESA,     SANDY                              );
        registerBiomeType(mesaPlateau_F,       MESA,     SPARSE,     SANDY                  );
        registerBiomeType(mesaPlateau,         MESA,     SANDY                              );
    }
}
