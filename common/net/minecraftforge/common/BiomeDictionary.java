package net.minecraftforge.common;

import java.util.*;
import net.minecraft.world.biome.*;
import static net.minecraftforge.common.BiomeDictionary.Type.*;

public class BiomeDictionary
{
    private static final int BIOME_LIST_SIZE = 256;
    
    private static BiomeInfo[] biomeList = new BiomeInfo[BIOME_LIST_SIZE];
    private static TypeInfo[] typeInfoList = new TypeInfo[Type.values().length];
    
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
    
    private static class TypeInfo
    {
        public ArrayList<BiomeGenBase> biomes;
        
        public TypeInfo()
        {
            biomes = new ArrayList<BiomeGenBase>();
        }
    }
    
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
        MAGICAL,
        NULL;
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
                    typeInfoList[type.ordinal()] = new TypeInfo();
                }
                
                typeInfoList[type.ordinal()].biomes.add(biome);
            }
        	
            if(biomeList[biome.biomeID] == null)
            {
                biomeList[biome.biomeID] = new BiomeInfo(types);
                return true;
            }
            else
            {
                for(Type type : types)
                {
                    biomeList[biome.biomeID].typeList.add(type);
                }
                return true;
            }
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
            return (BiomeGenBase[])typeInfoList[type.ordinal()].biomes.toArray();
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
            return (Type[])biomeList[biome.biomeID].typeList.toArray();
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
     *     Checks to see if the given biome has been registered as being of any type
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
    
    /**
     * Loops through the biome list and automatically adds tags to any biome that does not have any
     * This should be called in postInit to make sure all biomes have been registered
     * 
     * DO NOT call this during world generation
     */
    public static void registerAllBiomes()
    {
        for(int i = 0; i < BIOME_LIST_SIZE; i++)
        {
            if(BiomeGenBase.biomeList[i] != null)
            {
                checkRegistration(BiomeGenBase.biomeList[i]);
            }
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
            //Register as JUNGLE
            if(biome.isHighHumidity() && biome.temperature >= 1.0F)
            {
                BiomeDictionary.registerBiomeType(biome, JUNGLE);
            }
            //Register as FOREST
            else if(! biome.isHighHumidity())
            {
                BiomeDictionary.registerBiomeType(biome, FOREST);
            }
        }
        else if(biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F)
        {
            //Register as PLAINS
            if(! biome.isHighHumidity() || ! (biome.minHeight < 0.0F))
            {
                BiomeDictionary.registerBiomeType(biome, PLAINS);
            }
            
        }
        
        //Register as SWAMP
        if(biome.isHighHumidity() && biome.minHeight < 0.0F && (biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F))
        {
            BiomeDictionary.registerBiomeType(biome, SWAMP);
        }
        
        //Register as WATER
        if(biome.minHeight <= -0.5F)
        {
            BiomeDictionary.registerBiomeType(biome, WATER);
        }
        
        //Register as MOUNTAIN
        if(biome.maxHeight >= 1.5F)
        {
            BiomeDictionary.registerBiomeType(biome, MOUNTAIN);
        }
        
        //Register as FROZEN
        if(biome.getEnableSnow() || biome.temperature < 0.2F)
        {
            BiomeDictionary.registerBiomeType(biome, FROZEN);
        }
        
        //Register as DESERT
        if(! biome.isHighHumidity() && biome.temperature >= 1.0F)
        {
            BiomeDictionary.registerBiomeType(biome, DESERT);
        }
    }
    
    //Internal implementation
    
    private static void checkRegistration(BiomeGenBase biome)
    {
        if(! isBiomeRegistered(biome))
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
        registerBiomeType(BiomeGenBase.ocean, WATER);
        registerBiomeType(BiomeGenBase.plains, PLAINS);
        registerBiomeType(BiomeGenBase.desert, DESERT);
        registerBiomeType(BiomeGenBase.extremeHills, MOUNTAIN);
        registerBiomeType(BiomeGenBase.forest, FOREST);
        registerBiomeType(BiomeGenBase.taiga, FOREST, FROZEN);
        registerBiomeType(BiomeGenBase.taigaHills, FOREST, FROZEN);
        registerBiomeType(BiomeGenBase.swampland, SWAMP);
        registerBiomeType(BiomeGenBase.river, WATER);
        registerBiomeType(BiomeGenBase.frozenOcean, WATER, FROZEN);
        registerBiomeType(BiomeGenBase.frozenRiver, WATER, FROZEN);
        registerBiomeType(BiomeGenBase.icePlains, FROZEN);
        registerBiomeType(BiomeGenBase.iceMountains, FROZEN);
        registerBiomeType(BiomeGenBase.mushroomIsland, MUSHROOM);
        registerBiomeType(BiomeGenBase.mushroomIslandShore, MUSHROOM, BEACH);
        registerBiomeType(BiomeGenBase.beach, BEACH);
        registerBiomeType(BiomeGenBase.desertHills, DESERT);
        registerBiomeType(BiomeGenBase.jungle, JUNGLE);
        registerBiomeType(BiomeGenBase.jungleHills, JUNGLE);
        registerBiomeType(BiomeGenBase.forestHills, FOREST);
        registerBiomeType(BiomeGenBase.extremeHillsEdge, MOUNTAIN);
        registerBiomeType(BiomeGenBase.sky, END);
        registerBiomeType(BiomeGenBase.hell, NETHER);
    }
}
