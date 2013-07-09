package net.minecraftforge.common;

import java.util.*;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import net.minecraft.world.biome.*;
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
        
        private Set<BiomeGenBase> biomes = Sets.newHashSet();
    }

    private static <K, V extends Enum<V>> SetMultimap<K, V> newMultimap(final Class<V> valueClass)
    {
        return Multimaps.newSetMultimap(Maps.<K, Collection<V>>newHashMap(),
                new Supplier<Set<V>>()
                {
                    @Override
                    public Set<V> get()
                    {
                        return EnumSet.noneOf(valueClass);
                    }
                });
    }
    
    private static Multimap<BiomeGenBase, Type> biomeTypesMap = newMultimap(Type.class);

    static
    {
        registerVanillaBiomes();
    }

    /**
     * Registers a biome with a set of biome types
     * 
     * @param biome the biome to be registered
     * @param types the set of types to register the biome
     * @return returns true if the biome was registered successfully
     */
    public static boolean registerBiomeType(BiomeGenBase biome, Collection<Type> types)
    {   
        if (biome != null && types != null)
        {
            for (Type type: types)
            {
                type.biomes.add(biome);
            }
            
            biomeTypesMap.putAll(biome, types);

            return true;
        }

        return false;
    }

    /**
     * Registers a biome with a set of biome types. DEPRECATED - use Collection based version
     * 
     * @param biome the biome to be registered
     * @param type the set of types to register the biome
     * @return returns true if the biome was registered successfully
     */
    @Deprecated
    public static boolean registerBiomeType(BiomeGenBase biome, Type ... types)
    {
        return registerBiomeType(biome, Arrays.asList(types));
    }
    
    /**
     * Returns an array of biomes registered with a specific type. DEPRECATED - use Collection based version see {@link getBiomeListForType()}
     * 
     * @param type the Type to look for
     * @return an array of biomes of the specified type
     */
    @Deprecated
    public static BiomeGenBase[] getBiomesForType(Type type)
    {
        return getBiomeListForType(type).toArray(new BiomeGenBase[0]);
    }

    /**
     * Returns a list of biomes registered with a specific type
     * 
     * @param type the Type to look for
     * @return an list of biomes of the specified type
     */
    public static Collection<BiomeGenBase> getBiomeListForType(Type type)
    {
        return ImmutableList.copyOf(type.biomes);
    }

    /**
     * Gets a list of Types that a specific biome is registered with
     * 
     * @param biome the biome to check
     * @return the list of types
     */
    public static Collection<Type> getTypesListForBiome(BiomeGenBase biome)
    {
        checkRegistration(biome);

        if(biome != null)
        {
            return ImmutableList.copyOf(biomeTypesMap.get(biome));
        }

        return Collections.emptyList();
    }

    /**
     * Gets an array of Types that a specific biome is registered with. DEPRECATED - use Collection based version {@link getTypesListForBiome()}
     * 
     * @param biome the biome to check
     * @return the array of types
     */
    @Deprecated
    public static Type[] getTypesForBiome(BiomeGenBase biome)
    {
        return getTypesListForBiome(biome).toArray(new Type[0]);
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
        if (biomeA != null && biomeB != null)
        {
            checkRegistration(biomeA);
            checkRegistration(biomeB);

            Collection<Type> aTypes = getTypesListForBiome(biomeA);
            Collection<Type> bTypes = getTypesListForBiome(biomeB);
            
            for(Type type : aTypes)
            {
                if(bTypes.contains(type))
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
        return biomeTypesMap.containsEntry(biome, type);
    }

    /**
     * Checks to see if the given biome has been registered as being of any type
     * @param biome the biome to consider
     * @return returns true if the biome has been registered, false otherwise
     */
    public static boolean isBiomeRegistered(BiomeGenBase biome)
    {
        return biomeTypesMap.containsKey(biome);
    }

    public static boolean isBiomeRegistered(int biomeID)
    {
        return isBiomeRegistered(BiomeGenBase.biomeList[biomeID]);
    }

    /**
     * Loops through the biome list and automatically adds tags to any biome that does not have any
     * This should be called in postInit to make sure all biomes have been registered
     * 
     * DO NOT call this during world generation
     */
    public static void registerAllBiomes()
    {
        for (BiomeGenBase biome: BiomeGenBase.biomeList)
        {
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
        if (biome == null) return;
        
        final EnumSet<Type> types = EnumSet.noneOf(Type.class);
        
        if(biome.theBiomeDecorator.treesPerChunk >= 3)
        {
            if(biome.isHighHumidity() && biome.temperature >= 1.0F)
            {
                types.add(JUNGLE);
            }
            else if(!biome.isHighHumidity())
            {
                types.add(FOREST);
            }
        }
        else if(biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F)
        {
            if(!biome.isHighHumidity() || biome.minHeight >= 0.0F)
            {
                types.add(PLAINS);
            }
        }

        if(biome.isHighHumidity() && biome.minHeight < 0.0F && (biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F))
        {
            types.add(SWAMP);
        }

        if(biome.minHeight <= -0.5F)
        {
            types.add(WATER);
        }

        if(biome.maxHeight >= 1.5F)
        {
            types.add(MOUNTAIN);
        }
        
        if(biome.getEnableSnow() || biome.temperature < 0.2F)
        {
            types.add(FROZEN);
        }
        
        if(!biome.isHighHumidity() && biome.temperature >= 1.0F)
        {
            types.add(DESERT);
        }
        registerBiomeType(biome, types);
    }

    //Internal implementation    
    private static void checkRegistration(BiomeGenBase biome)
    {
        if(!isBiomeRegistered(biome))
        {
            makeBestGuess(biome);
        }
    }

    private static void registerVanillaBiomes()
    {
        registerBiomeType(ocean,               ImmutableList.of(WATER          ));
        registerBiomeType(plains,              ImmutableList.of(PLAINS         ));
        registerBiomeType(desert,              ImmutableList.of(DESERT         ));
        registerBiomeType(extremeHills,        ImmutableList.of(MOUNTAIN       ));
        registerBiomeType(forest,              ImmutableList.of(FOREST         ));
        registerBiomeType(taiga,               ImmutableList.of(FOREST,  FROZEN));
        registerBiomeType(taigaHills,          ImmutableList.of(FOREST,  FROZEN));
        registerBiomeType(swampland,           ImmutableList.of(SWAMP          ));
        registerBiomeType(river,               ImmutableList.of(WATER          ));
        registerBiomeType(frozenOcean,         ImmutableList.of(WATER,   FROZEN));
        registerBiomeType(frozenRiver,         ImmutableList.of(WATER,   FROZEN));
        registerBiomeType(icePlains,           ImmutableList.of(FROZEN         ));
        registerBiomeType(iceMountains,        ImmutableList.of(FROZEN         ));
        registerBiomeType(beach,               ImmutableList.of(BEACH          ));
        registerBiomeType(desertHills,         ImmutableList.of(DESERT         ));
        registerBiomeType(jungle,              ImmutableList.of(JUNGLE         ));
        registerBiomeType(jungleHills,         ImmutableList.of(JUNGLE         ));
        registerBiomeType(forestHills,         ImmutableList.of(FOREST         ));
        registerBiomeType(sky,                 ImmutableList.of(END            ));
        registerBiomeType(hell,                ImmutableList.of(NETHER         ));
        registerBiomeType(mushroomIsland,      ImmutableList.of(MUSHROOM       ));
        registerBiomeType(extremeHillsEdge,    ImmutableList.of(MOUNTAIN       ));
        registerBiomeType(mushroomIslandShore, ImmutableList.of(MUSHROOM, BEACH));
    }
}
