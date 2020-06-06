/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

import javax.annotation.Nullable;

public class BiomeManager
{
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();

    private static List<Biome> deepOceanBiomes = new ArrayList<Biome>();
    private static List<OceanBiomeEntry>[] oceanBiomes = setupOceanBiomes();

    private static TrackedList<BiomeEntry>[] setupBiomes()
    {
        @SuppressWarnings("unchecked")
        TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[BiomeType.values().length];
        List<BiomeEntry> list = new ArrayList<BiomeEntry>();

        list.add(new BiomeEntry(Biomes.FOREST, 10));
        list.add(new BiomeEntry(Biomes.DARK_FOREST, 10));
        list.add(new BiomeEntry(Biomes.MOUNTAINS, 10));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));
        list.add(new BiomeEntry(Biomes.BIRCH_FOREST, 10));
        list.add(new BiomeEntry(Biomes.SWAMP, 10));

        currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.FOREST, 10));
        list.add(new BiomeEntry(Biomes.MOUNTAINS, 10));
        list.add(new BiomeEntry(Biomes.TAIGA, 10));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));

        currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.SNOWY_TUNDRA, 30));
        list.add(new BiomeEntry(Biomes.SNOWY_TAIGA, 10));

        currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList<BiomeEntry>(list);

        return currentBiomes;
    }

    private static List<OceanBiomeEntry>[] setupOceanBiomes()
    {
        List<OceanBiomeEntry>[] currentBiomes = new List[OceanType.values().length];
        currentBiomes[OceanType.WARM.ordinal()] = Lists.newArrayList(new OceanBiomeEntry(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.LUKEWARM_OCEAN, 10));
        currentBiomes[OceanType.LUKEWARM.ordinal()] = Lists.newArrayList(new OceanBiomeEntry(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, 10));
        currentBiomes[OceanType.FROZEN.ordinal()] = Lists.newArrayList(new OceanBiomeEntry(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.COLD_OCEAN, 10));
        currentBiomes[OceanType.COLD.ordinal()] = Lists.newArrayList(new OceanBiomeEntry(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, 10));
        currentBiomes[OceanType.NORMAL.ordinal()] = Lists.newArrayList(new OceanBiomeEntry(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_OCEAN, 10));
        return currentBiomes;
    }

    public static void addSpawnBiome(Biome biome)
    {
        if (!BiomeProvider.BIOMES_TO_SPAWN_IN.contains(biome))
        {
            BiomeProvider.BIOMES_TO_SPAWN_IN.add(biome);
        }
    }

    public static void removeSpawnBiome(Biome biome)
    {
        if (BiomeProvider.BIOMES_TO_SPAWN_IN.contains(biome))
        {
            BiomeProvider.BIOMES_TO_SPAWN_IN.remove(biome);
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

    public static void addOceanBiome(OceanType type, OceanBiomeEntry entry)
    {
        int idx = type.ordinal();
        List<OceanBiomeEntry> list = idx > oceanBiomes.length ? null : oceanBiomes[idx];
        if (list != null) list.add(entry);
    }

    public static void removeOceanBiome(OceanType type, OceanBiomeEntry entry)
    {
        int idx = type.ordinal();
        List<OceanBiomeEntry> list = idx > oceanBiomes.length ? null : oceanBiomes[idx];

        if (list != null && list.contains(entry))
        {
            list.remove(entry);
        }
    }

    @Nullable
    public static ImmutableList<BiomeEntry> getBiomes(BiomeType type)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];

        return list != null ? ImmutableList.copyOf(list) : null;
    }

    @Nullable
    public static ImmutableList<OceanBiomeEntry> getOceanBiomes(OceanType type)
    {
        int idx = type.ordinal();
        List<OceanBiomeEntry> list = idx >= oceanBiomes.length ? null : oceanBiomes[idx];

        return list != null ? ImmutableList.copyOf(list) : null;
    }

    @Nullable
    public static ImmutableList<OceanBiomeEntry> getOceanBiomesForBiome(Biome biome, @Nullable OceanType type, boolean excludeWarm)
    {
        OceanType[] values = type != null ? new OceanType[] {type} : OceanType.values();
        List<OceanBiomeEntry> list = new ArrayList<>();
        for(OceanType types : values)
        {
            for(OceanBiomeEntry entry : getOceanBiomes(types))
            {
                if(entry.biome == biome && (excludeWarm ? type != OceanType.WARM : true))
                {
                    list.add(entry);
                }
            }
        }
        return list.isEmpty() ? null : ImmutableList.copyOf(list);
    }

    public static List<Biome> getDeepOceanBiomes()
    {
        return deepOceanBiomes;
    }

    public static boolean isOceanBiome(Biome biome, boolean shallow)
    {
        for(OceanType type : OceanType.values())
        {
            for(OceanBiomeEntry entry : getOceanBiomes(type))
            {
                if((shallow && entry.biome == biome) || (!shallow && (entry.biome == biome || entry.deepOcean == biome)))
                {
                    return true;
                }
            }
        }
        return false;
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

        public static BiomeType create(String name) {
            return null;
        }
    }

    public static enum OceanType
    {
        WARM, LUKEWARM, FROZEN, COLD, NORMAL;
    }

    public static class BiomeEntry extends WeightedRandom.Item
    {
        public final Biome biome;

        public BiomeEntry(Biome biome, int weight)
        {
            super(weight);

            this.biome = biome;
        }
    }

    public static class OceanBiomeEntry extends BiomeEntry
    {
        public final Biome deepOcean;
        public final Biome mixOcean;

        public OceanBiomeEntry(Biome ocean, Biome deepOcean, Biome mixOcean, int weight)
        {
            super(ocean, weight);
            if(!deepOceanBiomes.contains(deepOcean))
            {
                deepOceanBiomes.add(deepOcean);
            }
            this.deepOcean = deepOcean;
            this.mixOcean = mixOcean;
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
