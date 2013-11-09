package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.biome.WorldChunkManager;

import com.google.common.collect.Lists;

public class BiomeManager
{
    public static void addVillageBiome(BiomeGenBase biome, boolean canSpawn)
    {
        if (!MapGenVillage.villageSpawnBiomes.contains(biome))
        {
            ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(MapGenVillage.villageSpawnBiomes);
            biomes.add(biome);
            MapGenVillage.villageSpawnBiomes = biomes;
        }
    }

    public static void removeVillageBiome(BiomeGenBase biome)
    {
        if (MapGenVillage.villageSpawnBiomes.contains(biome))
        {
            ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(MapGenVillage.villageSpawnBiomes);
            biomes.remove(biome);
            MapGenVillage.villageSpawnBiomes = biomes;
        }
    }

    public static void addStrongholdBiome(BiomeGenBase biome)
    {
        if (!MapGenStronghold.allowedBiomes.contains(biome))
        {
            MapGenStronghold.allowedBiomes.add(biome);
        }
    }

    public static void removeStrongholdBiome(BiomeGenBase biome)
    {
        if (MapGenStronghold.allowedBiomes.contains(biome))
        {
            MapGenStronghold.allowedBiomes.remove(biome);
        }
    }

    public static void addSpawnBiome(BiomeGenBase biome)
    {
        if (!WorldChunkManager.allowedBiomes.contains(biome))
        {
            WorldChunkManager.allowedBiomes.add(biome);
        }
    }

    public static void removeSpawnBiome(BiomeGenBase biome)
    {
        if (WorldChunkManager.allowedBiomes.contains(biome))
        {
            WorldChunkManager.allowedBiomes.remove(biome);
        }
    }
}