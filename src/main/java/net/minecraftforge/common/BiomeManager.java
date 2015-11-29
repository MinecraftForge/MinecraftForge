package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.util.EnumHelper;

public class BiomeManager
{
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();

    public static List<BiomeGenBase> oceanBiomes = new ArrayList<BiomeGenBase>();

    public static ArrayList<BiomeGenBase> strongHoldBiomes = new ArrayList<BiomeGenBase>();
    public static ArrayList<BiomeGenBase> strongHoldBiomesBlackList = new ArrayList<BiomeGenBase>();

    static
    {
        oceanBiomes.add(BiomeGenBase.ocean);
        oceanBiomes.add(BiomeGenBase.deepOcean);
        oceanBiomes.add(BiomeGenBase.frozenOcean);
    }

    private static TrackedList<BiomeEntry>[] setupBiomes()
    {
        @SuppressWarnings("unchecked")
        TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[BiomeType.values().length];
        List<BiomeEntry> list = new ArrayList<BiomeEntry>();

        list.add(new BiomeEntry(BiomeGenBase.forest, 10));
        list.add(new BiomeEntry(BiomeGenBase.roofedForest, 10));
        list.add(new BiomeEntry(BiomeGenBase.extremeHills, 10));
        list.add(new BiomeEntry(BiomeGenBase.plains, 10));
        list.add(new BiomeEntry(BiomeGenBase.birchForest, 10));
        list.add(new BiomeEntry(BiomeGenBase.swampland, 10));

        currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        list.add(new BiomeEntry(BiomeGenBase.forest, 10));
        list.add(new BiomeEntry(BiomeGenBase.extremeHills, 10));
        list.add(new BiomeEntry(BiomeGenBase.taiga, 10));
        list.add(new BiomeEntry(BiomeGenBase.plains, 10));

        currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        list.add(new BiomeEntry(BiomeGenBase.icePlains, 30));
        list.add(new BiomeEntry(BiomeGenBase.coldTaiga, 10));

        currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList<BiomeEntry>(list);

        return currentBiomes;
    }

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

    public static void addBiome(BiomeType type, BiomeEntry entry)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        if (list != null) list.add(entry);
    }

    public static void removeBiome(BiomeType type, BiomeEntry entry)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];

        if (list != null && list.contains(entry))
        {
            list.remove(entry);
        }
    }

    public static ImmutableList<BiomeEntry> getBiomes(BiomeType type)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];

        return list != null ? ImmutableList.copyOf(list) : null;
    }

    public static boolean isTypeListModded(BiomeType type)
    {
        int idx = type.ordinal();
        TrackedList<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];

        if (list != null) return list.isModded();

        return false;
    }

    public static enum BiomeType
    {
        DESERT, WARM, COOL, ICY;

        public static BiomeType getType(String name)
        {
            name = name.toUpperCase();

            for (BiomeType t : values())
            {
                if (t.name().equals(name)) return t;
            }

            BiomeType ret = EnumHelper.addEnum(BiomeType.class, name, new Class[0], new Object[0]);

            if (ret.ordinal() >= biomes.length)
            {
                biomes = Arrays.copyOf(biomes, ret.ordinal() + 1);
            }

            return ret;
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

    private static class TrackedList<E> extends ArrayList<E>
    {
        private static final long serialVersionUID = 1L;
        private boolean isModded = false;

        public TrackedList(Collection<? extends E> c)
        {
            super(c);
        }

        @Override
        public E set(int index, E element)
        {
            isModded = true;
            return super.set(index, element);
        }

        @Override
        public boolean add(E e)
        {
            isModded = true;
            return super.add(e);
        }

        @Override
        public void add(int index, E element)
        {
            isModded = true;
            super.add(index, element);
        }

        @Override
        public E remove(int index)
        {
            isModded = true;
            return super.remove(index);
        }

        @Override
        public boolean remove(Object o)
        {
            isModded = true;
            return super.remove(o);
        }

        @Override
        public void clear()
        {
            isModded = true;
            super.clear();
        }

        @Override
        public boolean addAll(Collection<? extends E> c)
        {
            isModded = true;
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c)
        {
            isModded = true;
            return super.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
            isModded = true;
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c)
        {
            isModded = true;
            return super.retainAll(c);
        }

        public boolean isModded()
        {
            return isModded;
        }
    }
}
