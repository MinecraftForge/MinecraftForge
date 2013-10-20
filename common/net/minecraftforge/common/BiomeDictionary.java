package net.minecraftforge.common;

import java.util.*;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.biome.*;
import net.minecraftforge.event.terraingen.DeferredBiomeDecorator;
import static net.minecraft.world.biome.BiomeGenBase.*;
import static net.minecraftforge.common.BiomeDictionary.Type.*;

public class BiomeDictionary
{
    public enum Type
    {
        FOREST,
        PLAINS,
        MOUNTAIN,
        HILLS,
        SWAMP,
        WATER,
        DESERT,
        FROZEN,
        JUNGLE,
        WASTELAND,
        BEACH,
        NETHER,
        END,
        MUSHROOM,
        MAGICAL;
    }

    private static final int BIOME_LIST_SIZE = 256;
    private static BiomeInfo[] biomeList = new BiomeInfo[BIOME_LIST_SIZE];
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
        if(BiomeGenBase.biomeList[biome.biomeID] != null)
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
        for(int i = 0; i < BiomeGenBase.biomeList.length; i++)
        {
            BiomeGenBase biome = BiomeGenBase.biomeList[i];

            if(biome == null)
            {
                continue;
            }

            if (biome.theBiomeDecorator instanceof DeferredBiomeDecorator)
            {
                DeferredBiomeDecorator decorator = (DeferredBiomeDecorator) biome.theBiomeDecorator;
                decorator.fireCreateEventAndReplace();
            }

            checkRegistration(biome);
        }
    }

    /**
     * Automatically looks for and registers a given biome with appropriate tags
     * This method is called automatically if a biome has not been registered with any tags,
     * And another method requests information about it
     * 
     * NOTE: You can opt out of having your biome registered by registering it as type NULL
     * 
     * @param biome the biome to be considered
     */
    public static void makeBestGuess(BiomeGenBase biome)
    {    
        if(biome.theBiomeDecorator.treesPerChunk >= 3)
        {
            if(biome.isHighHumidity() && biome.temperature >= 1.0F)
            {
                BiomeDictionary.registerBiomeType(biome, JUNGLE);
            }
            else if(!biome.isHighHumidity())
            {
                BiomeDictionary.registerBiomeType(biome, FOREST);
            }
        }
        else if(biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F)
        {
            if(!biome.isHighHumidity() || biome.minHeight >= 0.0F)
            {
                BiomeDictionary.registerBiomeType(biome, PLAINS);
            }
        }

        if(biome.isHighHumidity() && biome.minHeight < 0.0F && (biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F))
        {
            BiomeDictionary.registerBiomeType(biome, SWAMP);
        }

        if(biome.minHeight <= -0.5F)
        {
            BiomeDictionary.registerBiomeType(biome, WATER);
        }

        if(biome.maxHeight >= 1.5F)
        {
            BiomeDictionary.registerBiomeType(biome, MOUNTAIN);
        }
        
        if(biome.getEnableSnow() || biome.temperature < 0.2F)
        {
            BiomeDictionary.registerBiomeType(biome, FROZEN);
        }
        
        if(!biome.isHighHumidity() && biome.temperature >= 1.0F)
        {
            BiomeDictionary.registerBiomeType(biome, DESERT);
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
        return info.typeList.contains(type);
    }

    private static void registerVanillaBiomes()
    {
        registerBiomeType(ocean,               WATER          );
        registerBiomeType(plains,              PLAINS         );
        registerBiomeType(desert,              DESERT         );
        registerBiomeType(extremeHills,        MOUNTAIN       );
        registerBiomeType(forest,              FOREST         );
        registerBiomeType(taiga,               FOREST,  FROZEN);
        registerBiomeType(taigaHills,          FOREST,  FROZEN);
        registerBiomeType(swampland,           SWAMP          );
        registerBiomeType(river,               WATER          );
        registerBiomeType(frozenOcean,         WATER,   FROZEN);
        registerBiomeType(frozenRiver,         WATER,   FROZEN);
        registerBiomeType(icePlains,           FROZEN         );
        registerBiomeType(iceMountains,        FROZEN         );
        registerBiomeType(beach,               BEACH          );
        registerBiomeType(desertHills,         DESERT         );
        registerBiomeType(jungle,              JUNGLE         );
        registerBiomeType(jungleHills,         JUNGLE         );
        registerBiomeType(forestHills,         FOREST         );
        registerBiomeType(sky,                 END            );
        registerBiomeType(hell,                NETHER         );
        registerBiomeType(mushroomIsland,      MUSHROOM       );
        registerBiomeType(extremeHillsEdge,    MOUNTAIN       );
        registerBiomeType(mushroomIslandShore, MUSHROOM, BEACH);
    }
}
