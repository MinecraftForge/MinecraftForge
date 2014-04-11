package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.biome.WorldChunkManager;

public class BiomeManager
{
    public static List<BiomeEntry> desertBiomes = new ArrayList<BiomeEntry>();
    public static List<BiomeEntry> warmBiomes = new ArrayList<BiomeEntry>();
    public static List<BiomeEntry> coolBiomes = new ArrayList<BiomeEntry>();
    public static List<BiomeEntry> icyBiomes = new ArrayList<BiomeEntry>();
    
    public static List<BiomeGenBase> oceanBiomes = new ArrayList<BiomeGenBase>();
    
    public static ArrayList<BiomeGenBase> strongHoldBiomes = new ArrayList<BiomeGenBase>();
    public static ArrayList<BiomeGenBase> strongHoldBiomesBlackList = new ArrayList<BiomeGenBase>();

    static
    {
        warmBiomes.add(new BiomeEntry(BiomeGenBase.forest, 10));
        warmBiomes.add(new BiomeEntry(BiomeGenBase.roofedForest, 10));
        warmBiomes.add(new BiomeEntry(BiomeGenBase.extremeHills, 10));
        warmBiomes.add(new BiomeEntry(BiomeGenBase.plains, 10));
        warmBiomes.add(new BiomeEntry(BiomeGenBase.birchForest, 10));
        warmBiomes.add(new BiomeEntry(BiomeGenBase.swampland, 10));

        coolBiomes.add(new BiomeEntry(BiomeGenBase.forest, 10));
        coolBiomes.add(new BiomeEntry(BiomeGenBase.extremeHills, 10));
        coolBiomes.add(new BiomeEntry(BiomeGenBase.taiga, 10));
        coolBiomes.add(new BiomeEntry(BiomeGenBase.plains, 10));

        icyBiomes.add(new BiomeEntry(BiomeGenBase.icePlains, 30));
        icyBiomes.add(new BiomeEntry(BiomeGenBase.coldTaiga, 10));

        oceanBiomes.add(BiomeGenBase.ocean);
        oceanBiomes.add(BiomeGenBase.deepOcean);
        oceanBiomes.add(BiomeGenBase.frozenOcean);
    }

    @SuppressWarnings("unchecked")
    public static void addVillageBiome(BiomeGenBase biome, boolean canSpawn)
    {
        if (!MapGenVillage.villageSpawnBiomes.contains(biome))
        {
            ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(MapGenVillage.villageSpawnBiomes);
            biomes.add(biome);
            MapGenVillage.villageSpawnBiomes = biomes;
        }
    }

    @SuppressWarnings("unchecked")
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
        if (!strongHoldBiomes.contains(biome))
        {
            strongHoldBiomes.add(biome);
        }
    }

    public static void removeStrongholdBiome(BiomeGenBase biome)
    {
        if (!strongHoldBiomesBlackList.contains(biome))
        {
            strongHoldBiomesBlackList.add(biome);
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
    
    public static class BiomeEntry extends WeightedRandom.Item
    {
        public final BiomeGenBase biome;
        
        public BiomeEntry(BiomeGenBase biome, int weight)
        {
            super(weight);
            
            this.biome = biome;
        }
    }
}