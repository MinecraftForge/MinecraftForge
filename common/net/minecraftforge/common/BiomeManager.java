package net.minecraftforge.common;

import java.util.LinkedList;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.WorldChunkManager;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * BiomeManager to manage most of the biome generation related things
 * 
 * @author battlefield
 *
 */

public class BiomeManager {

	private static LinkedList<BiomeGenBase> registeredBiomes = new LinkedList<BiomeGenBase>();
	
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
	 * @param biome	Your biome
	 * @param b		True if you want to add it to the list, false if you want to remove it
	 */
	
	public static void setVillageCanSpawnInBiome(BiomeGenBase biome, boolean b)
	{
		if(registeredBiomes.contains(biome))
		{
			if(b)
			{
				if(!MapGenVillage.villageSpawnBiomes.contains(biome))
				{
					MapGenVillage.villageSpawnBiomes.add(biome);
				}
			}else{
				if(MapGenVillage.villageSpawnBiomes.contains(biome))
				{
					MapGenVillage.villageSpawnBiomes.remove(biome);
				}
			}
		}
	}
	
	/**
	 * Adds the possibility to generate strongholds in your custom biomes
	 * @param biome	Your biome
	 * @param b		True if you want to add it to the list, false if you want to remove it
	 */
	
	public static void setStrongholdCanSpawnInBiome(BiomeGenBase biome, boolean b)
	{
		if(registeredBiomes.contains(biome))
		{
			if(b)
			{
				MapGenStronghold.addBiome(biome);
			}else{
				MapGenStronghold.removeBiome(biome);
			}
		}
	}
	
	/**
	 * Adds the possibility to spawn players in your custom biomes
	 * @param biome	Your biome
	 * @param b		True if you want to add it to the list, false if you want to remove it
	 */
	
	public static void setPlayerCanSpawnInBiome(BiomeGenBase biome, boolean b)
	{
		if(registeredBiomes.contains(biome))
		{
			if(b)
			{
				WorldChunkManager.addBiome(biome);
			}else{
				WorldChunkManager.removeBiome(biome);
			}
		}
	}
	
}
