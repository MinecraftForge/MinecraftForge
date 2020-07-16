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

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

import javax.annotation.Nullable;

public class BiomeManager
{
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();
    @Deprecated //Don't call this field, use the proper methods in this class to add and get ocean biomes
    public static List<Biome> oceanBiomes = new ArrayList<Biome>();
    private static EnumMap<OceanType, List<OceanBiomeEntry>> forgeOceanBiomes = setupOceanBiomes();
    private static Map<Biome, EnumMap<OceanFinderType, List<OceanBiomeEntry>>> cachedContainedOceans = Maps.newHashMap();

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

    private static EnumMap<OceanType, List<OceanBiomeEntry>> setupOceanBiomes()
    {
        return Util.make(Maps.newEnumMap(OceanType.class), (map) ->
        {
            map.put(OceanType.WARM, Lists.newArrayList(new OceanBiomeEntry(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.LUKEWARM_OCEAN, 10)));
            map.put(OceanType.LUKEWARM, Lists.newArrayList(new OceanBiomeEntry(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, 10)));
            map.put(OceanType.FROZEN, Lists.newArrayList(new OceanBiomeEntry(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.COLD_OCEAN, 10)));
            map.put(OceanType.COLD, Lists.newArrayList(new OceanBiomeEntry(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, 10)));
            map.put(OceanType.NORMAL, Lists.newArrayList(new OceanBiomeEntry(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_OCEAN, 10)));
        });
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

    /**
     * Adds an OceanBiomeEntry to a type of ocean.
     * This method should be called during mod loading to add a new OceanBiomeEntry to a type of ocean (e.g. a new Frozen Ocean biome).
     * This method is threadsafe.
     *
     * @param type The type of ocean for this OceanBiomeEntry.
     * @param entry The OceanBiomeEntry to add.
     */
    public static void addOceanBiome(OceanType type, OceanBiomeEntry entry)
    {
        synchronized (BiomeManager.class)
        {
            forgeOceanBiomes.get(type).add(entry);
        }
    }

    /**
     * Removes an {@link OceanBiomeEntry} for a type of ocean.
     * This method should be called to remove a OceanBiomeEntry from a type of ocean.
     * This method is threadsafe.
     *
     * @param type The type of ocean for this OceanBiomeEntry.
     * @param entry The OceanBiomeEntry to remove.
     */
    public static void removeOceanBiome(OceanType type, OceanBiomeEntry entry)
    {
        synchronized (BiomeManager.class)
        {
            if (forgeOceanBiomes.get(type).remove(entry)) {
                if (type == OceanType.NORMAL && forgeOceanBiomes.get(type).isEmpty())
                {
                    throw new IllegalStateException("At least one NORMAL type ocean biome entry must be present");
                }
            }
        }
    }

    /**
     * Removes all the ocean biome entries with a biome as their 'main' biome.
     * This method should be called to remove all the ocean biome entries with a biome as their 'main' biome.
     * This method is threadsafe.
     *
     * @param type The type of ocean for the biome.
     * @param biome The biome to remove.
     */
    public static void removeOceanBiome(OceanType type, Biome biome)
    {
        synchronized (BiomeManager.class)
        {
            forgeOceanBiomes.get(type).stream().filter(entry -> entry.biome == biome).collect(Collectors.toList()).forEach(entry -> removeOceanBiome(type, entry));
        }
    }

    @Nullable
    public static ImmutableList<BiomeEntry> getBiomes(BiomeType type)
    {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];

        return list != null ? ImmutableList.copyOf(list) : null;
    }

    /**
     * Gets the ocean biome entries for a type of ocean.
     * This method should be called to get the ocean biome entries for a type of ocean.
     * This method is threadsafe.
     *
     * @param type The type of ocean to get the entries for.
     * @return An ImmutableList of the ocean biome entries for the ocean type.
     */
    public static ImmutableList<OceanBiomeEntry> getOceanBiomes(OceanType type)
    {
        synchronized (BiomeManager.class)
        {
            return ImmutableList.copyOf(forgeOceanBiomes.get(type));
        }
    }

    /**
     * Gets the ocean biome entries that have a biome as their main biome ('main' meaning the biome that is not its deep or mix variant).
     * This method should be called to get the ocean biome entries that have a biome as their main biome.
     * This method is threadsafe.
     *
     * @param biome The biome to check for.
     * @param finderType The type of {@link OceanFinderType} to use for getting specific contained biomes
     *
     * @return An ImmutableList of ocean biome entries that meet the conditions specified in the params.
     */
    public static ImmutableList<OceanBiomeEntry> getOceanBiomesContainingBiome(Biome biome, OceanFinderType finderType)
    {
        synchronized (BiomeManager.class)
        {
            EnumMap<OceanFinderType, List<OceanBiomeEntry>> cachedEntries = cachedContainedOceans.get(biome);
            if (cachedEntries != null)
            {
                List<OceanBiomeEntry> entries = cachedEntries.get(finderType);
                if (entries != null)
                {
                    return ImmutableList.copyOf(entries);
                }
            }

            OceanType[] oceanTypes = finderType.type != null ? new OceanType[] {finderType.type} : OceanType.values();
            List<OceanBiomeEntry> entries = Lists.newArrayList();
            for (OceanType type : oceanTypes)
            {
                for (OceanBiomeEntry entry : getOceanBiomes(type))
                {
                    if (entry.biome == biome && (!finderType.excludeWarm || type != OceanType.WARM))
                    {
                        entries.add(entry);
                    }
                }
            }
            cachedContainedOceans.put(biome, Util.make(cachedEntries != null ? Maps.newEnumMap(cachedEntries) : Maps.newEnumMap(OceanFinderType.class), map -> map.put(finderType, entries)));
            return ImmutableList.copyOf(entries);
        }
    }

    /**
     * Checks if a biome is present in the ocean biome entries for any OceanType.
     * This method should be called to check if a biome is present in the ocean biome entries for every OceanType (i.e. if a biome is considered an ocean).
     * This method is threadsafe.
     *
     * @param biome The biome to check
     * @param shallow If the biome is shallow (i.e. not a deep ocean)
     * @return True if the biome is an ocean or is a shallow ocean if shallow is true
     */
    public static boolean isOceanBiome(Biome biome, boolean shallow)
    {
        synchronized (BiomeManager.class)
        {
            for (OceanType type : OceanType.values())
            {
                for (OceanBiomeEntry entry : getOceanBiomes(type))
                {
                    if ((shallow && entry.biome == biome) || (!shallow && (entry.biome == biome || entry.deepOcean == biome)))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
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

    /**
     * Types of Oceans to be used for adding new ocean biomes.
     * Each type corresponds to a vanilla ocean (e.g. WARM for warm oceans).
     */
    public static enum OceanType
    {
        WARM, LUKEWARM, FROZEN, COLD, NORMAL;
    }

    /**
     * Used to find ocean entries containing a biome with certain conditions.
     * This is used internally for ocean biome layers.
     * @see BiomeManager#getOceanBiomesContainingBiome(Biome, OceanFinderType)
     */
    public static enum OceanFinderType
    {
        ANY(null, false),
        WARM(OceanType.WARM, false),
        FROZEN(OceanType.FROZEN, false),
        NON_WARM(null, true);

        @Nullable
        public final OceanType type;
        public final boolean excludeWarm;

        /**
         * @param type Type of ocean to specifically look for, or null for any ocean type.
         * @param excludeWarm If this finder should exclude warm ocean types.
         */
        OceanFinderType(@Nullable OceanType type, boolean excludeWarm)
        {
            this.type = type;
            this.excludeWarm = excludeWarm;
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

    public static class OceanBiomeEntry extends BiomeEntry
    {
        public final Biome deepOcean;
        public final Biome mixOcean;

        /**
         * Creates an entry that's used for new Ocean Biomes.
         *
         * @param ocean The main ocean for this entry (e.g. Biomes.COLD_OCEAN).
         * @param deepOcean The deep variant of the main ocean (e.g. Biomes.DEEP_COLD_OCEAN).
         * @param mixOcean The mix variant of the main ocean, this typically matches the deep variant for vanilla's oceans (e.g. Biomes.DEEP_COLD_OCEAN).
         * @param weight The weight of this entry, the higher the weight the larger the probability.
         */
        public OceanBiomeEntry(Biome ocean, Biome deepOcean, Biome mixOcean, int weight)
        {
            super(ocean, weight);
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
