package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.src.BiomeGenBase;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * BiomeManager to manage most of the biome generation related things
 * 
 * @author battlefield
 *
 */

public class BiomeManager {

	private static LinkedList<BiomeGenBase> registeredBiomes = new LinkedList<BiomeGenBase>();
	private static ArrayList<BiomeGenBase> spawnBiomes = new ArrayList<BiomeGenBase>();
	private static ArrayList<BiomeGenBase> strongholdBiomes = new ArrayList<BiomeGenBase>();
	private static ArrayList<BiomeGenBase> villageBiomes = new ArrayList<BiomeGenBase>();
	
	/**
	 * Equivalent to Modloader.addBiome
	 * @param biome
	 */
	public static void addBiome(BiomeGenBase biome)
	{
		if(!registeredBiomes.contains(biome))
		{
			GameRegistry.addBiome(biome);
			registeredBiomes.add(biome);
		}
	}
	
	/**
	 * Equivalent to Modloader.removeBiome
	 * @param biome
	 */
	public static void removeBiome(BiomeGenBase biome)
	{
		GameRegistry.removeBiome(biome);
		registeredBiomes.remove(biome);
	}
	
	/**
	 * Adds the possibility to generate villages in your custom biomes
	 * @param biome		Your biome
	 * @param canSpawn	True if you want to add it to the list, false if you want to remove it
	 */
	
	public static void setVillageCanSpawnInBiome(BiomeGenBase biome, boolean canSpawn)
	{
		if(registeredBiomes.contains(biome))
		{
			if(canSpawn)
			{
				if(!villageBiomes.contains(biome))
				{
					villageBiomes.add(biome);
				}
			}else{
				if(villageBiomes.contains(biome))
				{
					villageBiomes.remove(biome);
				}
			}
		}
	}
	
	/**
	 * Adds the possibility to generate strongholds in your custom biomes
	 * @param biome		Your biome
	 * @param canSpawn	True if you want to add it to the list, false if you want to remove it
	 */
	
	public static void setStrongholdCanSpawnInBiome(BiomeGenBase biome, boolean canSpawn)
	{
		if(registeredBiomes.contains(biome))
		{
			if(canSpawn)
			{
				if(!strongholdBiomes.contains(biome))
				{
					strongholdBiomes.add(biome);
				}
			}else{
				if(strongholdBiomes.contains(biome))
				{
					strongholdBiomes.remove(biome);
				}
			}
		}
	}
	
	/**
	 * Adds the possibility to spawn players in your custom biomes
	 * @param biome		Your biome
	 * @param canSpawn	True if you want to add it to the list, false if you want to remove it
	 */
	
	public static void setPlayerCanSpawnInBiome(BiomeGenBase biome, boolean canSpawn)
	{
		if(registeredBiomes.contains(biome))
		{
			if(canSpawn)
			{
				if(!spawnBiomes.contains(biome))
				{
					spawnBiomes.add(biome);
				}
			}else{
				if(spawnBiomes.contains(biome))
				{
					spawnBiomes.remove(biome);
				}
			}
		}
	}
	
	public static ArrayList<BiomeGenBase> getVillageBiomes()
	{
		return villageBiomes;
	}
	
	public static ArrayList<BiomeGenBase> getStrongholdBiomes()
	{
		return strongholdBiomes;
	}
	
	public static ArrayList<BiomeGenBase> getSpawnBiomes()
	{
		return spawnBiomes;
	}
	
}
