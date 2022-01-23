/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.collect.ImmutableList;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.SimpleMapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

import javax.annotation.Nullable;
import java.util.*;

public class BiomeManager
{
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomes = new ArrayList<>();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomesView = Collections.unmodifiableList(additionalOverworldBiomes);
    private static final List<ClimateParameterExtension> climateParameterExtensions = new ArrayList<>();
    private static final List<Climate.Parameter> defaultClimateParameterExtensionValues = new ArrayList<>();

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
     * Adds a new {@link net.minecraft.world.level.biome.Climate.ParameterPoint} parameter that can be used for additional customization of biome selection in multi-noise biome sources.
     * <p><b>Only call this method during mod loading!</b></p>
     *
     * @param name A {@link ResourceLocation} to use as the name for the {@link ClimateParameterExtension}.
     * @param defaultValue A {@link Climate.Parameter} instance to use as the default value for the new parameter.
     * @param samplerFactory A factory for creating a {@link ClimateParameterSampler} instance that will get used for computing the parameter coordinate value in {@link TargetPointExtensions}.
     * @return A new {@link ClimateParameterExtension} containing the name, default value, factory, and ID for the new parameter.
     * @see ClimateParameterSampler
     * @see ClimateParameterExtension
     */
    public static synchronized ClimateParameterExtension addClimateParameter(ResourceLocation name, Climate.Parameter defaultValue, Function3<NoiseSettings, Registry<NoiseParameters>, PositionalRandomFactory, ClimateParameterSampler> samplerFactory)
    {
        if (getClimateParameterExtension(name) != null)
            throw new IllegalArgumentException("A ClimateParameterExtension with the name '" + name + "' already exists!");
        ClimateParameterExtension climateParameterExtension = new ClimateParameterExtension(name, defaultValue, samplerFactory, climateParameterExtensions.size());
        climateParameterExtensions.add(climateParameterExtension);
        defaultClimateParameterExtensionValues.add(defaultValue);
        ParameterPointExtensions.DEFAULT = new ParameterPointExtensions();
        TargetPointExtensions.ZERO = new TargetPointExtensions(new long[climateParameterExtensions.size()]);
        return climateParameterExtension;
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

    /**
     * Tries to get a {@link ClimateParameterExtension} by its name.
     * <p><b>Only call this method after mod loading!</b></p>
     *
     * @param name The name of the {@link ClimateParameterExtension} to look up.
     * @return A {@link ClimateParameterExtension} looked up by its name, or null if no {@link ClimateParameterExtension} existed with the given name.
     * @see ClimateParameterExtension
     */
    @Nullable
    public static ClimateParameterExtension getClimateParameterExtension(ResourceLocation name)
    {
        for (ClimateParameterExtension climateParameterExtension : climateParameterExtensions)
        {
            if (climateParameterExtension.name.equals(name))
            {
                return climateParameterExtension;
            }
        }
        return null;
    }

    /**
     * Gets an array of all the default climate parameter extension values.
     * <p><b>Only call this method after mod loading!</b></p>
     * 
     * @return An array of all the default climate parameter extension values.
     * @see #addClimateParameter(ResourceLocation, Climate.Parameter, Function3)
     */
    public static Climate.Parameter[] getDefaultClimateParameterExtensionValues()
    {
        return defaultClimateParameterExtensionValues.toArray(new Climate.Parameter[0]);
    }

    /**
     * Gets the number of added climate parameter extensions.
     * <p><b>Only call this method after mod loading!</b></p>
     * 
     * @return The number of added climate parameter extensions.
     * @see #addClimateParameter(ResourceLocation, Climate.Parameter, Function3) 
     */
    public static int getClimateParameterExtensionsCount()
    {
        return climateParameterExtensions.size();
    }

    /**
     * Creates an array of {@link ClimateParameterSampler} instances with each element corresponding to an added {@link ClimateParameterExtension}.
     * <p><b>Only call this method after mod loading!</b></p>
     *
     * @param settings A {@link NoiseSettings} instance to use for configuration.
     * @param registry A {@link Registry} for {@link NoiseParameters} for getting registered {@link NoiseParameters} instances.
     * @param positionalRandomFactory A {@link PositionalRandomFactory} instance for creating {@link net.minecraft.world.level.levelgen.RandomSource} instances.
     * @see #addClimateParameter(ResourceLocation, Climate.Parameter, Function3)
     * @see ClimateParameterExtension
     * @see ClimateParameterSampler
     * @return An array of {@link ClimateParameterSampler} instances with each element corresponding to an added {@link ClimateParameterExtension}.
     */
    public static ClimateParameterSampler[] setupClimateParameterExtensionFunctions(NoiseSettings settings, Registry<NoiseParameters> registry, PositionalRandomFactory positionalRandomFactory)
    {
        int count = getClimateParameterExtensionsCount();
        ClimateParameterSampler[] climateParameterSamplers = new ClimateParameterSampler[count];
        for (int i = 0; i < count; i++)
        {
            climateParameterSamplers[i] = climateParameterExtensions.get(i).samplerFactory.apply(settings, registry, positionalRandomFactory);
        }
        return climateParameterSamplers;
    }

    /**
     * Gets a view of all the added {@link ClimateParameterExtension} instances.
     * <p><b>Only call this method after mod loading!</b></p>
     *
     * @return A view of all the added {@link ClimateParameterExtension} instances.
     */
    public static List<ClimateParameterExtension> getClimateParameterExtensions()
    {
        return ImmutableList.copyOf(climateParameterExtensions);
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

    /**
     * Represents a new {@link net.minecraft.world.level.biome.Climate.ParameterPoint} parameter that can be used for additional customization of biome selection in multi-noise biome sources.
     * <p>Instances of this class can only be created from the {@link #addClimateParameter(ResourceLocation, Climate.Parameter, Function3)} method.</p>
     *
     * @see #addClimateParameter(ResourceLocation, Climate.Parameter, Function3)
     * @see ClimateParameterSampler
     */
    public static final class ClimateParameterExtension
    {
        public static final Codec<ClimateParameterExtension> CODEC = ResourceLocation.CODEC.flatXmap(location ->
        {
            ClimateParameterExtension climateParameterExtension = getClimateParameterExtension(location);
            return climateParameterExtension == null ? DataResult.error("Unknown Climate Parameter Extension: " + location) : DataResult.success(climateParameterExtension);
        }, climateParameterExtension -> DataResult.success(climateParameterExtension.name));
        public final ResourceLocation name;
        public final Climate.Parameter defaultValue;
        public final Function3<NoiseSettings, Registry<NoiseParameters>, PositionalRandomFactory, ClimateParameterSampler> samplerFactory;
        public final int id;

        private ClimateParameterExtension(ResourceLocation name, Climate.Parameter defaultValue, Function3<NoiseSettings, Registry<NoiseParameters>, PositionalRandomFactory, ClimateParameterSampler> samplerFactory, int id)
        {
            this.name = name;
            this.defaultValue = defaultValue;
            this.samplerFactory = samplerFactory;
            this.id = id;
        }
    }

    /**
     * The functional interface used for computing the parameter value of an added {@link ClimateParameterExtension} when sampling a {@link Climate.TargetPoint}.
     *
     * @see TargetPointExtensions#extendedTargetPoint(int, int, int, double, double, double, float, float, float, float, float, float, ClimateParameterSampler[])
     */
    @FunctionalInterface
    public interface ClimateParameterSampler
    {
        /**
         * Samples the parameter value at a position.
         *
         * @param fromBlockX The x pos being sampled at, bit-shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
         * @param fromBlockY The y pos being sampled at, bit-shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
         * @param fromBlockZ The z pos being sampled at, bit-shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
         * @param shiftedX The value of fromBlockX but quadruply offset by the {@link net.minecraft.world.level.levelgen.Noises#SHIFT} noise.
         * @param shiftedY The value of fromBlockY but quadruply offset by the {@link net.minecraft.world.level.levelgen.Noises#SHIFT} noise.
         * @param shiftedZ The value of fromBlockZ but quadruply offset by the {@link net.minecraft.world.level.levelgen.Noises#SHIFT} noise.
         * @see net.minecraft.world.level.levelgen.NoiseSampler#target(int, int, int, NoiseSampler.FlatNoiseData)
         * @return The parameter value at a position.
         */
        float sample(int fromBlockX, int fromBlockY, int fromBlockZ, double shiftedX, double shiftedY, double shiftedZ);
    }

    /**
     * Holds the extended {@link Climate.Parameter} values in a {@link Climate.ParameterPoint} instance for all the added climate parameter extensions.
     * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
     * <p><b>Only use this class after mod loading!</b></p>
     *
     * @see #addClimateParameter(ResourceLocation, Climate.Parameter, Function3)
     */
    public static final class ParameterPointExtensions
    {
        //Default parameter extension values are cached for performance
        private static ParameterPointExtensions DEFAULT = new ParameterPointExtensions();
        public static final Codec<ParameterPointExtensions> CODEC = new SimpleMapCodec<>(ClimateParameterExtension.CODEC, Climate.Parameter.CODEC, Keyable.forStrings(() -> climateParameterExtensions.stream().map(climateParameterExtension -> climateParameterExtension.name.toString()))).codec().xmap(map ->
        {
            if (map.isEmpty()) return ParameterPointExtensions.DEFAULT;
            Climate.Parameter[] parameters = getDefaultClimateParameterExtensionValues();
            map.forEach((climateParameterExtension, parameter) -> parameters[climateParameterExtension.id] = parameter);
            return new ParameterPointExtensions(parameters);
        }, parameters ->
        {
            Climate.Parameter[] internalParameters = parameters.parameters;
            Map<ClimateParameterExtension, Climate.Parameter> map = new HashMap<>();
            for (int i = 0; i < internalParameters.length; i++)
            {
                map.put(climateParameterExtensions.get(i), internalParameters[i]);
            }
            return map;
        });
        private final Climate.Parameter[] parameters;

        private ParameterPointExtensions(Climate.Parameter[] parameters)
        {
            this.parameters = parameters;
        }

        /**
         * Constructs a new {@link ParameterPointExtensions} instance from an array of pairs containing a {@link ClimateParameterExtension} instance and a corresponding {@link Climate.Parameter}.
         *
         * @param entries An array of pairs containing a {@link ClimateParameterExtension} instance and a corresponding {@link Climate.Parameter}.
         */
        @SafeVarargs
        public ParameterPointExtensions(Pair<ClimateParameterExtension, Climate.Parameter>... entries)
        {
            this(getDefaultClimateParameterExtensionValues());
            for (var pair : entries)
            {
                this.parameters[pair.getFirst().id] = pair.getSecond();
            }
        }

        /**
         * Gets the default {@link ParameterPointExtensions} instance that contains the default values for all the added climate parameter extensions.
         * <p><b>Only call this method after mod loading!</b></p>
         *
         * @return The default {@link ParameterPointExtensions} instance that contains the default values for all the added climate parameter extensions.
         */
        public static ParameterPointExtensions getDefault()
        {
            return DEFAULT;
        }

        /**
         * Gets the {@link Climate.Parameter} corresponding to a given {@link ClimateParameterExtension}.
         *
         * @param extension A {@link ClimateParameterExtension} to get its {@link Climate.Parameter}.
         * @return The {@link Climate.Parameter} corresponding to the given {@link ClimateParameterExtension}.
         */
        public Climate.Parameter getParameter(ClimateParameterExtension extension)
        {
            return this.parameters[extension.id];
        }

        /**
         * Merges the parameter extension values with vanilla's parameter values.
         *
         * @param temperature A {@link Climate.Parameter} instance for the temperature parameter.
         * @param humidity A {@link Climate.Parameter} instance for the humidity parameter.
         * @param continentalness A {@link Climate.Parameter} instance for the continentalness parameter.
         * @param erosion A {@link Climate.Parameter} instance for the erosion parameter.
         * @param depth A {@link Climate.Parameter} instance for the depth parameter.
         * @param weirdness A {@link Climate.Parameter} instance for the weirdness parameter.
         * @param offset The offset parameter.
         * @return An immutable list containing the parameter extension values merged with vanilla's parameter values.
         */
        public List<Climate.Parameter> extendedParameterSpace(Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, Climate.Parameter weirdness, long offset)
        {
            ImmutableList.Builder<Climate.Parameter> builder = new ImmutableList.Builder<>();
            builder.add(temperature, humidity, continentalness, erosion, depth, weirdness, new Climate.Parameter(offset, offset));
            builder.add(this.parameters);
            return builder.build();
        }

        /**
         * Gets a copy of the internal {@link #parameters}.
         *
         * @return A copy of the internal {@link #parameters}.
         */
        public Climate.Parameter[] getParameters()
        {
            return Arrays.copyOf(this.parameters, this.parameters.length);
        }

        @Override
        public String toString()
        {
            Climate.Parameter[] parameters = this.parameters;
            int max = parameters.length - 1;
            if (max == -1) return "{}";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('{');
            for (int i = 0; ; i++)
            {
                stringBuilder.append(climateParameterExtensions.get(i).name).append("=").append(parameters[i].toString());
                if (i == max)
                    return stringBuilder.append('}').toString();
                stringBuilder.append(", ");
            }
        }
    }

    /**
     * Holds the extended climate parameter values in a {@link net.minecraft.world.level.biome.Climate.TargetPoint} instance for all the added climate parameter extensions.
     * <p><b>Only use this class after mod loading!</b></p>
     */
    public static final class TargetPointExtensions
    {
        //Default target point extension values are cached for performance
        private static TargetPointExtensions ZERO = new TargetPointExtensions(new long[0]);
        private final long[] parameters;

        private TargetPointExtensions(long[] parameters)
        {
            this.parameters = parameters;
        }

        /**
         * Constructs a new {@link TargetPointExtensions} instance from an array of pairs containing a {@link ClimateParameterExtension} instance and a corresponding parameter value.
         *
         * @param parameters An array of pairs containing a {@link ClimateParameterExtension} instance and a corresponding parameter value.
         */
        @SafeVarargs
        public TargetPointExtensions(Pair<ClimateParameterExtension, Long>... parameters)
        {
            this.parameters = new long[getClimateParameterExtensionsCount()];
            for (var pair : parameters)
            {
                this.parameters[pair.getFirst().id] = pair.getSecond();
            }
        }

        /**
         * Gets a {@link TargetPointExtensions} instance containing all zero values.
         *
         * @return A {@link TargetPointExtensions} instance containing all zero values.
         */
        public static TargetPointExtensions zero()
        {
            return ZERO;
        }

        /**
         * Creates a new {@link Climate.TargetPoint} containing vanilla parameter values and sampled extended climate parameter values.
         *
         * @param fromBlockX The x pos being sampled at, bit-shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
         * @param fromBlockY The y pos being sampled at, bit-shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
         * @param fromBlockZ The z pos being sampled at, bit-shifted by {@link net.minecraft.core.QuartPos#fromBlock(int)}.
         * @param shiftedX The value of fromBlockX but quadruply offset by the {@link net.minecraft.world.level.levelgen.Noises#SHIFT} noise.
         * @param shiftedY The value of fromBlockY but quadruply offset by the {@link net.minecraft.world.level.levelgen.Noises#SHIFT} noise.
         * @param shiftedZ The value of fromBlockZ but quadruply offset by the {@link net.minecraft.world.level.levelgen.Noises#SHIFT} noise.
         * @param temperature The temperature noise value.
         * @param humidity The humidity noise value.
         * @param continentalness The continentalness noise value.
         * @param erosion The erosion noise value.
         * @param depth The depth value.
         * @param weirdness The weirdness noise value.
         * @param climateParameterSamplers An array of {@link ClimateParameterSampler} instances to sample the extended climate parameter values.
         * @return A new {@link Climate.TargetPoint} containing sampled vanilla parameter values and sampled extended climate parameter values.
         * @see ClimateParameterSampler
         */
        public static Climate.TargetPoint extendedTargetPoint(int fromBlockX, int fromBlockY, int fromBlockZ, double shiftedX, double shiftedY, double shiftedZ, float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, ClimateParameterSampler[] climateParameterSamplers)
        {
            int count = getClimateParameterExtensionsCount();
            if (count == 0)
                return Climate.target(Climate.quantizeCoord(temperature), Climate.quantizeCoord(humidity), Climate.quantizeCoord(continentalness), Climate.quantizeCoord(erosion), Climate.quantizeCoord(depth), Climate.quantizeCoord(weirdness));
            long[] quantizedParameters = new long[count];
            for (int i = 0; i < count; i++)
            {
                quantizedParameters[i] = Climate.quantizeCoord(climateParameterSamplers[i].sample(fromBlockX, fromBlockY, fromBlockZ, shiftedX, shiftedY, shiftedZ));
            }
            return new Climate.TargetPoint(Climate.quantizeCoord(temperature), Climate.quantizeCoord(humidity), Climate.quantizeCoord(continentalness), Climate.quantizeCoord(erosion), Climate.quantizeCoord(depth), Climate.quantizeCoord(weirdness), new TargetPointExtensions(quantizedParameters));
        }

        /**
         * Computes the fitness of a given {@link ParameterPointExtensions} instance.
         *
         * @param extensions A {@link ParameterPointExtensions} instance to compare against.
         * @return The fitness of the given {@link ParameterPointExtensions} instance.
         */
        public long fitness(ParameterPointExtensions extensions)
        {
            int count = getClimateParameterExtensionsCount();
            if (count == 0) return 0L;
            long fitness = 0L;
            Climate.Parameter[] extensionsParameters = extensions.parameters;
            long[] parameters = this.parameters;
            for (int i = 0; i < count; i++)
            {
                fitness += Mth.square(extensionsParameters[i].distance(parameters[i]));
            }
            return fitness;
        }

        /**
         * Concatenates vanilla's climate parameter values with the internal {@link #parameters}.
         *
         * @param temperature The temperature parameter value.
         * @param humidity The humidity parameter value.
         * @param continentalness The continentalness parameter value.
         * @param erosion The erosion parameter value.
         * @param depth The depth parameter value.
         * @param weirdness The weirdness parameter value.
         * @return An array containing vanilla climate parameter values concatenated with the internal {@link #parameters}.
         */
        public long[] extendParameterArray(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness)
        {
            int count = getClimateParameterExtensionsCount();
            if (count == 0)
                return new long[] {temperature, humidity, continentalness, erosion, depth, weirdness, 0L};
            long[] parameterArray = new long[7 + count];
            parameterArray[0] = temperature;
            parameterArray[1] = humidity;
            parameterArray[2] = continentalness;
            parameterArray[3] = erosion;
            parameterArray[4] = depth;
            parameterArray[5] = weirdness;
            parameterArray[6] = 0L;
            System.arraycopy(this.parameters, 0, parameterArray, 7, count);
            return parameterArray;
        }

        /**
         * Gets a copy of the internal {@link #parameters}.
         *
         * @return A copy of the internal {@link #parameters}.
         */
        public long[] getParameters()
        {
            return Arrays.copyOf(this.parameters, this.parameters.length);
        }

        @Override
        public String toString()
        {
            long[] parameters = this.parameters;
            int max = parameters.length - 1;
            if (max == -1) return "{}";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('{');
            for (int i = 0; ; i++)
            {
                stringBuilder.append(climateParameterExtensions.get(i).name).append("=").append(parameters[i]);
                if (i == max)
                    return stringBuilder.append('}').toString();
                stringBuilder.append(", ");
            }
        }
    }
}
