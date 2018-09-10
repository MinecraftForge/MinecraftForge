/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Biomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nullable;

public class BiomeManager
{
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();

    public static List<Biome> oceanBiomes = new ArrayList<Biome>();

    public static ArrayList<Biome> strongHoldBiomes = new ArrayList<Biome>();
    public static ArrayList<Biome> strongHoldBiomesBlackList = new ArrayList<Biome>();

    static
    {
        oceanBiomes.add(Biomes.OCEAN);
        oceanBiomes.add(Biomes.DEEP_OCEAN);
        oceanBiomes.add(Biomes.FROZEN_OCEAN);
    }

    private static TrackedList<BiomeEntry>[] setupBiomes()
    {
        @SuppressWarnings("unchecked")
        TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[BiomeType.values().length];
        List<BiomeEntry> list = new ArrayList<BiomeEntry>();

        list.add(new BiomeEntry(Biomes.FOREST, 10));
        list.add(new BiomeEntry(Biomes.ROOFED_FOREST, 10));
        list.add(new BiomeEntry(Biomes.EXTREME_HILLS, 10));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));
        list.add(new BiomeEntry(Biomes.BIRCH_FOREST, 10));
        list.add(new BiomeEntry(Biomes.SWAMPLAND, 10));

        currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.FOREST, 10));
        list.add(new BiomeEntry(Biomes.EXTREME_HILLS, 10));
        list.add(new BiomeEntry(Biomes.TAIGA, 10));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));

        currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.ICE_PLAINS, 30));
        list.add(new BiomeEntry(Biomes.COLD_TAIGA, 10));

        currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList<BiomeEntry>(list);
        list.clear();

        currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList<BiomeEntry>(list);

        return currentBiomes;
    }

    public static void addVillageBiome(Biome biome, boolean canSpawn)
    {
        if (!MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome))
        {
            ArrayList<Biome> biomes = new ArrayList<Biome>(MapGenVillage.VILLAGE_SPAWN_BIOMES);
            biomes.add(biome);
            MapGenVillage.VILLAGE_SPAWN_BIOMES = biomes;
        }
    }

    public static void removeVillageBiome(Biome biome)
    {
        if (MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome))
        {
            ArrayList<Biome> biomes = new ArrayList<Biome>(MapGenVillage.VILLAGE_SPAWN_BIOMES);
            biomes.remove(biome);
            MapGenVillage.VILLAGE_SPAWN_BIOMES = biomes;
        }
    }

    public static void addStrongholdBiome(Biome biome)
    {
        if (!strongHoldBiomes.contains(biome))
        {
            strongHoldBiomes.add(biome);
        }
    }

    public static void removeStrongholdBiome(Biome biome)
    {
        if (!strongHoldBiomesBlackList.contains(biome))
        {
            strongHoldBiomesBlackList.add(biome);
        }
    }

    public static void addSpawnBiome(Biome biome)
    {
        if (!BiomeProvider.allowedBiomes.contains(biome))
        {
            BiomeProvider.allowedBiomes.add(biome);
        }
    }

    public static void removeSpawnBiome(Biome biome)
    {
        if (BiomeProvider.allowedBiomes.contains(biome))
        {
            BiomeProvider.allowedBiomes.remove(biome);
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

    @Nullable
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
        public final Biome biome;

        public BiomeEntry(Biome biome, int weight)
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
