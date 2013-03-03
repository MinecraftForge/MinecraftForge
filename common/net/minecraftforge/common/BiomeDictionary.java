package net.minecraftforge.common;

import java.util.*;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.biome.*;

public class BiomeDictionary
{
  private static BNode[] biomeList = new BNode[256];
	
	//Registration of vanilla biomes
	static
	{
		registerBiomeType(BiomeGenBase.ocean, BiomeType.WATER);
		registerBiomeType(BiomeGenBase.plains, BiomeType.PLAINS);
		registerBiomeType(BiomeGenBase.desert, BiomeType.DESERT);
		registerBiomeType(BiomeGenBase.extremeHills, BiomeType.MOUNTAIN);
		registerBiomeType(BiomeGenBase.forest, BiomeType.FOREST);
		registerBiomeType(BiomeGenBase.taiga, BiomeType.FOREST);
		registerBiomeType(BiomeGenBase.taiga, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.taigaHills, BiomeType.FOREST);
		registerBiomeType(BiomeGenBase.taigaHills, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.swampland, BiomeType.SWAMP);
		registerBiomeType(BiomeGenBase.river, BiomeType.WATER);
		registerBiomeType(BiomeGenBase.frozenOcean, BiomeType.WATER);
		registerBiomeType(BiomeGenBase.frozenOcean, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.frozenRiver, BiomeType.WATER);
		registerBiomeType(BiomeGenBase.frozenRiver, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.hell, BiomeType.NETHER);
		registerBiomeType(BiomeGenBase.sky, BiomeType.END);
		registerBiomeType(BiomeGenBase.icePlains, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.iceMountains, BiomeType.FROZEN);
		registerBiomeType(BiomeGenBase.mushroomIsland, BiomeType.MUSHROOM);
		registerBiomeType(BiomeGenBase.mushroomIslandShore, BiomeType.MUSHROOM);
		registerBiomeType(BiomeGenBase.mushroomIslandShore, BiomeType.BEACH);
		registerBiomeType(BiomeGenBase.beach, BiomeType.BEACH);
		registerBiomeType(BiomeGenBase.desertHills, BiomeType.DESERT);
		registerBiomeType(BiomeGenBase.jungle, BiomeType.JUNGLE);
		registerBiomeType(BiomeGenBase.jungleHills, BiomeType.JUNGLE);
		registerBiomeType(BiomeGenBase.forestHills, BiomeType.FOREST);
		registerBiomeType(BiomeGenBase.extremeHillsEdge, BiomeType.MOUNTAIN);
	}
	
	
	/**
	 * Registers a biome with a specific biome type
	 * 
	 * @param biome the biome to be registered
	 * @param type the type to register the biome as
	 * @return returns true if the biome was registered successfully
	 */
	public static boolean registerBiomeType(BiomeGenBase biome, BiomeType type)
	{
		boolean result = false;
		
		if(biomeList[biome.biomeID] == null)
		{
			biomeList[biome.biomeID] = new BNode(type);
			result = true;
		}
		else
		{
			biomeList[biome.biomeID].typeList.add(type);
			result = true;
		}
		
		if(result)
		{
			System.out.println("[BiomeDictionary] Registered Biome " + biome.biomeName + " as type " + type);
		}
		
		return result;
	}
	
	/**
	 * Returns a list of biomes registered with a specific type
	 * 
	 * @param type the BiomeType to look for
	 * @return a list of biomes of the specified type
	 */
	public static ArrayList<BiomeGenBase> getBiomesForType(BiomeType type)
	{
		ArrayList<BiomeGenBase> list = new ArrayList<BiomeGenBase>();
		
		for(int i = 0; i < 256; i++)
		{
			if(biomeList[i] != null)
			{
				if(containsType(biomeList[i], type))
				{
					checkRegistration(BiomeGenBase.biomeList[i]);
					list.add(BiomeGenBase.biomeList[i]);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Gets a list of BiomeTypes that a specific biome is registered with
	 * 
	 * @param biome the biome to check
	 * @return the list of types
	 */
	
	public static ArrayList<BiomeType> getTypesForBiome(BiomeGenBase biome)
	{
		ArrayList<BiomeType> result = new ArrayList<BiomeType>();
		
		checkRegistration(biome);
		
		if(biomeList[biome.biomeID] != null)
		{
			result = biomeList[biome.biomeID].typeList;
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
	
	public static boolean isOfType(BiomeGenBase biome, BiomeType type)
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
	 * Automatically looks for and registers a given biome with appropriate tags
	 * This method is called automatically if a biome has not been registered with any tags,
	 * And another method inquires about how it is registered
	 * This can be disabled by registering the biome as type NULL
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
	
	protected static void registerWorldGenForBiome(IWorldGenerator gen, BiomeGenBase biome)
	{
		checkRegistration(biome);
		
		biomeList[biome.biomeID].genList.add(gen);
	}
	
	protected static ArrayList<IWorldGenerator> getWorldGenForBiome(BiomeGenBase biome)
	{
		checkRegistration(biome);
		
		return biomeList[biome.biomeID].genList;
	}
	
	private static void checkRegistration(BiomeGenBase biome)
	{
		if(! isBiomeRegistered(biome))
		{
			makeBestGuess(biome);
		}
	}
	
	private static boolean containsType(BNode node, BiomeType type)
	{
		boolean result = false;
		
		for(BiomeType theType : node.typeList)
		{
			if(theType == type)
			{
				result = true;
			}
		}
		
		return result;
	}
	
	private static class BNode
	{
		public BNode(BiomeType type)
		{
			this.typeList =  new ArrayList<BiomeType>();
			typeList.add(type);
			genList = new ArrayList<IWorldGenerator>();
		}
		
		public ArrayList<BiomeType> typeList;
		public ArrayList<IWorldGenerator> genList;
	}
}
