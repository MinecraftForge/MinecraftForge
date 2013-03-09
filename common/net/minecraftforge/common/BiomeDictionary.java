package net.minecraftforge.common;

import java.util.*;
import net.minecraft.world.biome.*;

public class BiomeDictionary
{
	private static final int BIOME_LIST_SIZE = 256;
	
	private static BiomeInfo[] biomeList = new BiomeInfo[BIOME_LIST_SIZE];
	private static TypeInfo[] typeInfoList = new TypeInfo[BiomeType.NULL.ordinal()];
	
	private static class BiomeInfo
	{
		public EnumSet<BiomeType> typeList;
		
		public BiomeInfo(BiomeType[] types)
		{
			typeList = EnumSet.noneOf(BiomeType.class);
			for(BiomeType t : types)
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
	public static boolean registerBiomeType(BiomeGenBase biome, BiomeType ... types)
	{
		boolean result = false;
		
		if(BiomeGenBase.biomeList[biome.biomeID] != null)
		{
			if(biomeList[biome.biomeID] == null)
			{
				biomeList[biome.biomeID] = new BiomeInfo(types);
				result = true;
			}
			else
			{
				for(BiomeType type : types)
				{
					biomeList[biome.biomeID].typeList.add(type);
				}
				result = true;
			}
			
			for(BiomeType type : types)
			{
				if(typeInfoList[type.ordinal()] == null)
				{
					typeInfoList[type.ordinal()] = new TypeInfo();
				}
				
				typeInfoList[type.ordinal()].biomes.add(biome);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns a list of biomes registered with a specific type
	 * 
	 * @param type the BiomeType to look for
	 * @return a list of biomes of the specified type, null if there are none
	 */
	public static BiomeGenBase[] getBiomesForType(BiomeType type)
	{
		BiomeGenBase[] result = null;
		
		if(typeInfoList[type.ordinal()] != null)
		{
			result = (BiomeGenBase[])typeInfoList[type.ordinal()].biomes.toArray();
		}
		
		return result;
	}
	
	/**
	 * Gets a list of BiomeTypes that a specific biome is registered with
	 * 
	 * @param biome the biome to check
	 * @return the list of types, null if there are none
	 */
	
	public static BiomeType[] getTypesForBiome(BiomeGenBase biome)
	{
		BiomeType[] result = null;
		
		checkRegistration(biome);
		
		if(biomeList[biome.biomeID] != null)
		{
			result = (BiomeType[])biomeList[biome.biomeID].typeList.toArray();
		}
		
		return result;
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
		boolean result = false;
		
		int a = biomeA.biomeID;
		int b = biomeB.biomeID;
		
		checkRegistration(biomeA);
		checkRegistration(biomeB);
		
		if(biomeList[a] != null && biomeList[b] != null)
		{
		
			for(BiomeType type : biomeList[a].typeList)
			{
				if(containsType(biomeList[b], type))
				{
					result = true;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Checks to see if the given biome is registered as being a specific type
	 * 
	 * @param biome the biome to be considered
	 * @param type the type to check for
	 * @return returns true if the biome is registered as being of type type, false otherwise
	 */
	
	public static boolean isBiomeOfType(BiomeGenBase biome, BiomeType type)
	{
		boolean result = false;
		
		checkRegistration(biome);
		
		if(biomeList[biome.biomeID] != null)
		{
			result = containsType(biomeList[biome.biomeID], type);
		}
		
		return result;
	}
	
	/**
	 * 	Checks to see if the given biome has been registered as being of any type
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
				BiomeDictionary.registerBiomeType(biome, BiomeType.JUNGLE);
			}
			//Register as FOREST
			else if(! biome.isHighHumidity())
			{
				BiomeDictionary.registerBiomeType(biome, BiomeType.FOREST);
			}
		}
		else if(biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F)
		{
			//Register as PLAINS
			if(! biome.isHighHumidity() || ! (biome.minHeight < 0.0F))
			{
				BiomeDictionary.registerBiomeType(biome, BiomeType.PLAINS);
			}
			
		}
		
		//Register as SWAMP
		if(biome.isHighHumidity() && biome.minHeight < 0.0F && (biome.maxHeight <= 0.3F && biome.maxHeight >= 0.0F))
		{
			BiomeDictionary.registerBiomeType(biome, BiomeType.SWAMP);
		}
		
		//Register as WATER
		if(biome.minHeight <= -0.5F)
		{
			BiomeDictionary.registerBiomeType(biome, BiomeType.WATER);
		}
		
		//Register as MOUNTAIN
		if(biome.maxHeight >= 1.5F)
		{
			BiomeDictionary.registerBiomeType(biome, BiomeType.MOUNTAIN);
		}
		
		//Register as FROZEN
		if(biome.getEnableSnow() || biome.temperature < 0.2F)
		{
			BiomeDictionary.registerBiomeType(biome, BiomeType.FROZEN);
		}
		
		//Register as DESERT
		if(! biome.isHighHumidity() && biome.temperature >= 1.0F)
		{
			BiomeDictionary.registerBiomeType(biome, BiomeType.DESERT);
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
	
	private static boolean containsType(BiomeInfo info, BiomeType type)
	{
		return info.typeList.contains(type);
	}
	
	private static void registerVanillaBiomes()
	{
		registerBiomeType(BiomeGenBase.ocean, BiomeType.WATER);
		registerBiomeType(BiomeGenBase.plains, BiomeType.PLAINS);
		registerBiomeType(BiomeGenBase.desert, BiomeType.DESERT);
		registerBiomeType(BiomeGenBase.extremeHills, BiomeType.MOUNTAIN);
		registerBiomeType(BiomeGenBase.forest, BiomeType.FOREST);
		registerBiomeType(BiomeGenBase.taiga, BiomeType.FOREST, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.taigaHills, BiomeType.FOREST, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.swampland, BiomeType.SWAMP);
		registerBiomeType(BiomeGenBase.river, BiomeType.WATER);
		registerBiomeType(BiomeGenBase.frozenOcean, BiomeType.WATER, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.frozenRiver, BiomeType.WATER, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.icePlains, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.iceMountains, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.mushroomIsland, BiomeType.MUSHROOM);
		registerBiomeType(BiomeGenBase.mushroomIslandShore, BiomeType.MUSHROOM, BiomeType.BEACH);
		registerBiomeType(BiomeGenBase.beach, BiomeType.BEACH);
		registerBiomeType(BiomeGenBase.desertHills, BiomeType.DESERT);
		registerBiomeType(BiomeGenBase.jungle, BiomeType.JUNGLE);
		registerBiomeType(BiomeGenBase.jungleHills, BiomeType.JUNGLE);
		registerBiomeType(BiomeGenBase.forestHills, BiomeType.FOREST);
		registerBiomeType(BiomeGenBase.extremeHillsEdge, BiomeType.MOUNTAIN);
	}
}
