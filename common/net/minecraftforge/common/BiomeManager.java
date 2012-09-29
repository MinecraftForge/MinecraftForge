package net.minecraftforge.common;

import java.util.ArrayList;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.MapGenVillage;

/**
 * BiomeManager to manage most of the biome generation related things
 *
 * @author battlefield
 *
 */

public class BiomeManager
{
    private static ArrayList<BiomeGenBase> spawnBiomes = new ArrayList<BiomeGenBase>();
    private static ArrayList<BiomeGenBase> strongholdBiomes = new ArrayList<BiomeGenBase>();
    private static ArrayList<BiomeGenBase> villageBiomes = new ArrayList<BiomeGenBase>();

    /**
     * Equivalent to GameRegistry.addBiome
     * @param biome
     */
    public static void addBiome(BiomeGenBase biome)
    {
        GameRegistry.addBiome(biome);
    }

    /**
     * Equivalent to GameRegistry.removeBiome
     * @param biome
     */
    public static void removeBiome(BiomeGenBase biome)
    {
        GameRegistry.removeBiome(biome);
    }

    /**
     * Adds the possibility to generate villages in your custom biomes
     * @param biome		Your biome
     * @param canSpawn	True if you want to add it to the list, false if you want to remove it
     */

    public static void setVillageCanSpawnInBiome(BiomeGenBase biome, boolean canSpawn)
    {
        if (canSpawn)
        {
            if (!villageBiomes.contains(biome))
            {
                villageBiomes.add(biome);
            }

            ArrayList list = new ArrayList();
            list.addAll(MapGenVillage.villageSpawnBiomes);
            list.add(biome);
            MapGenVillage.villageSpawnBiomes = list.subList(0, list.size());
        }
        else
        {
            if (villageBiomes.contains(biome))
            {
                villageBiomes.remove(biome);
            }

            ArrayList list = new ArrayList();
            list.addAll(MapGenVillage.villageSpawnBiomes);
            list.remove(biome);
            MapGenVillage.villageSpawnBiomes = list.subList(0, list.size());
        }
    }

    /**
     * Adds the possibility to generate strongholds in your custom biomes
     * @param biome		Your biome
     * @param canSpawn	True if you want to add it to the list, false if you want to remove it
     */

    public static void setStrongholdCanSpawnInBiome(BiomeGenBase biome, boolean canSpawn)
    {
        if (canSpawn)
        {
            if (!strongholdBiomes.contains(biome))
            {
                strongholdBiomes.add(biome);
            }
        }
        else
        {
            if (strongholdBiomes.contains(biome))
            {
                strongholdBiomes.remove(biome);
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
        if (canSpawn)
        {
            if (!spawnBiomes.contains(biome))
            {
                spawnBiomes.add(biome);
            }
        }
        else
        {
            if (spawnBiomes.contains(biome))
            {
                spawnBiomes.remove(biome);
            }
        }
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