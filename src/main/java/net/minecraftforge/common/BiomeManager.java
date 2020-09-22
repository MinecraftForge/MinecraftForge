/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

/**
 * Manage modded biome generation for {@link net.minecraft.world.biome.provider.OverworldBiomeProvider}
 */
public class BiomeManager
{
    private static final TrackedList<BiomeEntry>[] biomes = setupBiomes();
    private static final ArrayList<RegistryKey<Biome>> allModdedBiomes = new ArrayList<>();

    private static TrackedList<BiomeEntry>[] setupBiomes()
    {
        @SuppressWarnings("unchecked")
        TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[BiomeType.values().length];
        List<BiomeEntry> list = new ArrayList<>();

        list.add(new BiomeEntry(Biomes.FOREST, 10));
        list.add(new BiomeEntry(Biomes.DARK_FOREST, 10));
        list.add(new BiomeEntry(Biomes.MOUNTAINS, 10));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));
        list.add(new BiomeEntry(Biomes.BIRCH_FOREST, 10));
        list.add(new BiomeEntry(Biomes.SWAMP, 10));

        currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList<>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.FOREST, 10));
        list.add(new BiomeEntry(Biomes.MOUNTAINS, 10));
        list.add(new BiomeEntry(Biomes.TAIGA, 10));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));

        currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList<>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.SNOWY_TUNDRA, 30));
        list.add(new BiomeEntry(Biomes.SNOWY_TAIGA, 10));

        currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList<>(list);
        list.clear();

        list.add(new BiomeEntry(Biomes.DESERT, 30));
        list.add(new BiomeEntry(Biomes.SAVANNA, 20));
        list.add(new BiomeEntry(Biomes.PLAINS, 10));
        currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList<>(list);

        return currentBiomes;
    }

    /**
     * Register all your biomes that might be generated in the overworld (either via {@link BiomeManager#addBiomeEntry(BiomeType, BiomeEntry)} or as a biome variant) here.
     */
    public static void addBiomes(RegistryKey<Biome>... biomes){
        allModdedBiomes.addAll(Arrays.asList(biomes));
    }

    /**
     * Add a biome entry for the overworld {@link net.minecraft.world.gen.layer.BiomeLayer}
     * Also add it to {@link BiomeManager#addBiomes(RegistryKey[])}
     */
    public static void addBiomeEntry(BiomeType type, BiomeEntry entry)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        if (list != null) list.add(entry);
    }

    public static void removeBiomeEntry(BiomeType type, BiomeEntry entry)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];

        if (list != null && list.contains(entry))
        {
            list.remove(entry);
        }
    }

    /**
     * @return Immutable list of weighted biome entries that should be considered for overworld biome selection in {@link net.minecraft.world.gen.layer.BiomeLayer}
     */
    @Nullable
    public static ImmutableList<BiomeEntry> getBiomeEntries(BiomeType type)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];

        return list != null ? ImmutableList.copyOf(list) : null;
    }

    /**
     * @return Immutable list of all modded biomes that might be generated in the overworld
     */
    public static List<RegistryKey<Biome>> getAllBiomes(){
        return ImmutableList.copyOf(allModdedBiomes);
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

    public static class BiomeEntry extends WeightedRandom.Item
    {
        public final RegistryKey<Biome> biome;

        public BiomeEntry(RegistryKey<Biome> biome, int weight)
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
