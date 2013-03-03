package net.minecraftforge.common;

import java.util.*;

import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class BiomeWorldGenRegistry
{
  private static ArrayList<ArrayList<IWorldGenerator>> generators = new ArrayList<ArrayList<IWorldGenerator>>();
	
	static
	{
		for(int i = 0; i <= BiomeType.NULL.ordinal(); i++)
		{
			generators.add(new ArrayList<IWorldGenerator>());
		}
		
		GameRegistry.registerWorldGenerator(new WorldGenBiomeSpecific());
	}
	
	public static void registerWorldGeneratorForType(IWorldGenerator generator, BiomeType biomeType)
	{
		generators.get(biomeType.ordinal()).add(generator);
	}
	
	public static void registerWorldGenForBiome(IWorldGenerator gen, BiomeGenBase biome)
	{
		BiomeDictionary.registerWorldGenForBiome(gen, biome);
	}
	
	protected static ArrayList<IWorldGenerator> getGeneratorsForType(BiomeType biomeType)
	{
		return generators.get(biomeType.ordinal());
	}
	
	
}
