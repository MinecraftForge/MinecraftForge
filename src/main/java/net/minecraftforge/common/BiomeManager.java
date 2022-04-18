/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeManager
{
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomes = new ArrayList<>();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomesView = Collections.unmodifiableList(additionalOverworldBiomes);

    private static TrackedList<BiomeEntry>[] setupBiomes()
    {
        @SuppressWarnings("unchecked")
        TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[BiomeType.values().length];

        currentBiomes[BiomeType.DESERT_LEGACY.ordinal()] = new TrackedList<>(
            new BiomeEntry(Biomes.DESERT, 10),
            new BiomeEntry(Biomes.FOREST, 10),
            new BiomeEntry(Biomes.SWAMP, 10),
            new BiomeEntry(Biomes.PLAINS, 10),
            new BiomeEntry(Biomes.TAIGA, 10)
        );

        currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList<>(
            new BiomeEntry(Biomes.DESERT, 30),
            new BiomeEntry(Biomes.SAVANNA, 20),
            new BiomeEntry(Biomes.PLAINS, 10)
        );

        currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList<>(
            new BiomeEntry(Biomes.FOREST, 10),
            new BiomeEntry(Biomes.DARK_FOREST, 10),
            new BiomeEntry(Biomes.PLAINS, 10),
            new BiomeEntry(Biomes.BIRCH_FOREST, 10),
            new BiomeEntry(Biomes.SWAMP, 10)
        );

        currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList<>(
            new BiomeEntry(Biomes.FOREST, 10),
            new BiomeEntry(Biomes.TAIGA, 10),
            new BiomeEntry(Biomes.PLAINS, 10)
        );

        currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList<>(
            new BiomeEntry(Biomes.SNOWY_TAIGA, 10)
        );

        return currentBiomes;
    }

    /**
     * Add biomes that you add to the overworld without using {@link BiomeManager#addBiome(BiomeType, BiomeEntry)}
     */
    public static void addAdditionalOverworldBiomes(ResourceKey<Biome> biome)
    {
        if (!"minecraft".equals(biome.location().getNamespace()) && additionalOverworldBiomes.stream().noneMatch(entry -> entry.location().equals(biome.location())))
        {
            additionalOverworldBiomes.add(biome);
        }
    }

    public static boolean addBiome(BiomeType type, BiomeEntry entry)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        if (list != null)
        {
            additionalOverworldBiomes.add(entry.key);
            return list.add(entry);
        }
        return false;
    }

    public static boolean removeBiome(BiomeType type, BiomeEntry entry)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        return list == null ? false : list.remove(entry);
    }

    /**
     * @return list of biomes that might be generated in the overworld in addition to the vanilla biomes
     */
    public static List<ResourceKey<Biome>> getAdditionalOverworldBiomes()
    {
        return additionalOverworldBiomesView;
    }

    public static ImmutableList<BiomeEntry> getBiomes(BiomeType type)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];
        return list != null ? ImmutableList.copyOf(list) : ImmutableList.of();
    }

    public static boolean isTypeListModded(BiomeType type)
    {
        int idx = type.ordinal();
        TrackedList<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        return list == null ? false : list.isModded();
    }

    public static enum BiomeType
    {
        DESERT, DESERT_LEGACY, WARM, COOL, ICY;
    }

    public static class BiomeEntry
    {
        private final ResourceKey<Biome> key;

        public BiomeEntry(ResourceKey<Biome> key, int weight)
        {
            this.key = key;
        }

        public ResourceKey<Biome> getKey()
        {
            return this.key;
        }
    }

    private static class TrackedList<E> extends ArrayList<E>
    {
        private static final long serialVersionUID = 1L;
        private boolean isModded = false;

        @SafeVarargs
        private <T extends E> TrackedList(T... c)
        {
            super(Arrays.asList(c));
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
