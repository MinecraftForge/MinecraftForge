package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.WorldChunkManager;

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
     * Adds the possibility to generate villages in your custom biomes
     * @param biome		Your biome
     * @param canSpawn	True if you want to add it to the list, false if you want to remove it
     */
    public static void setVillageCanSpawnInBiome(BiomeGenBase biome, boolean canSpawn)
    {
        ArrayList<BiomeGenBase> biomesForVillage = new ArrayList<BiomeGenBase>(MapGenVillage.villageSpawnBiomes);

        if (canSpawn)
        {
            if (!villageBiomes.contains(biome))
            {
                villageBiomes.add(biome);
                biomesForVillage.add(biome);
            }
        }
        else
        {
            villageBiomes.remove(biome);
            biomesForVillage.remove(biome);
        }

        MapGenVillage.villageSpawnBiomes = biomesForVillage;
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
            strongholdBiomes.remove(biome);
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
            spawnBiomes.remove(biome);
        }
    }

    public static ArrayList<BiomeGenBase> getSpawnBiomes()
    {
        return spawnBiomes;
    }

    public static void initializeStrongholdGen(IChunkProvider ichunkprovider)
    {
        if (!(ichunkprovider instanceof ChunkProviderGenerate))
        {
            return;
        }

        ChunkProviderGenerate chunkproviderenerate = (ChunkProviderGenerate)ichunkprovider;
        ArrayList biomes = new ArrayList(Arrays.asList(chunkproviderenerate.strongholdGenerator.allowedBiomeGenBases));
        biomes.addAll(strongholdBiomes);
        chunkproviderenerate.strongholdGenerator.allowedBiomeGenBases = (BiomeGenBase[]) biomes.toArray(new BiomeGenBase[0]);
    }
}
