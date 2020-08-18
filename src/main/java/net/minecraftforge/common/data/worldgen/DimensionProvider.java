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

package net.minecraftforge.common.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider.Noise;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/**
 * The builder class is designed for Dimensions using a {@link NoiseChunkGenerator}
 * and a {@link NetherBiomeProvider}, see {@link Builder} and {@link Builder.NoiseChunkGeneratorBuilder.MultiNoiseBiomeProviderBuilder}
 *
 * Dimensions do not have a registry in {@link DynamicRegistries} so it is not a {@link RegistryBackedProvider}.
 * This does not matter because dimensions are the final objects and do not need to be referenced afterwards.
 */
public abstract class DimensionProvider extends CodecBackedProvider<Dimension>
{
    protected final DataGenerator generator;
    protected final String modid;
    protected final Map<ResourceLocation, Dimension> map = new HashMap<>();

    protected DimensionProvider(DataGenerator generator, RegistryOpsHelper regOps, String modid)
    {
        super(Dimension.field_236052_a_, regOps);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache)
    {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/dimension/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation name, Dimension inst)
    {
        this.map.put(name, inst);
    }

    @Override
    public String getName()
    {
        return "Dimensions : " + modid;
    }

    /**
     * ChunkGenerators can have multiple implementation, the only builder here is for
     * {@link NoiseChunkGenerator}, the standard world ChunkGenerator.
     * For NoiseChunkGenerator's, multiple {@link BiomeProvider} can be used (called biome_source on the wiki), but the most useful
     * being {@link NetherBiomeProvider} (called multi_noise on the wiki) and a Builder is provided for it.
     *
     * More info can be found on <a href=https://minecraft.gamepedia.com/Custom_dimension#Dimension_syntax>the wiki</a>.
     */
    public class Builder
    {
        private DimensionType dimType;
        private ChunkGenerator generator;

        public Builder setDimType(DimensionType type)
        {
            this.dimType = type;
            return this;
        }

        public Builder setDimType(ResourceLocation location)
        {
            return setDimType(regOps.getObject(Registry.field_239698_ad_, location));
        }

        public Builder setDimType(RegistryKey<DimensionType> location)
        {
            return setDimType(location.func_240901_a_());
        }

        public Builder setChunkGenerator(ChunkGenerator generator)
        {
            this.generator = generator;
            return this;
        }

        public NoiseChunkGeneratorBuilder buildNoise()
        {
            return new NoiseChunkGeneratorBuilder();
        }


        public Dimension build()
        {
            return new Dimension(() -> dimType, generator);
        }

        public class NoiseChunkGeneratorBuilder
        {
            private BiomeProvider provider;
            private long seed = new Random().nextLong();
            private Supplier<DimensionSettings> settings = DimensionSettings::func_242746_i;

            public Builder finish()
            {
                return Builder.this.setChunkGenerator(new NoiseChunkGenerator(provider, seed,settings));
            }

            public NoiseChunkGeneratorBuilder setSettings(DimensionSettings settings)
            {
                this.settings = () -> settings;
                return this;
            }

            public NoiseChunkGeneratorBuilder setSettings(ResourceLocation location)
            {
                this.settings = () -> DimensionProvider.this.regOps.getObject(Registry.field_243549_ar, location);
                return this;
            }

            public NoiseChunkGeneratorBuilder setSettings(RegistryKey<DimensionSettings> key)
            {
                return setSettings(key.func_240901_a_());
            }

            public NoiseChunkGeneratorBuilder setSeed(long seed)
            {
                this.seed = seed;
                return this;
            }

            public NoiseChunkGeneratorBuilder setBiomeProvider(BiomeProvider provider)
            {
                this.provider = provider;
                return this;
            }

            public MultiNoiseBiomeProviderBuilder buildNoiseBiome()
            {
                return new MultiNoiseBiomeProviderBuilder();
            }

            /**
             * {@link NetherBiomeProvider} is called MultiNoise in the wiki.
             * It is the main way of manipulating the biomes of a dimension.
             */
            public class MultiNoiseBiomeProviderBuilder
            {
                /**
                 * Setting the seed to be by default that of the NoiseChunkGenerator.
                 */
                private long seed = NoiseChunkGeneratorBuilder.this.seed;
                private final Noise defaultNoise = new Noise(-7, ImmutableList.of(1.0D, 1.0D));
                private Noise temperature = defaultNoise;
                private Noise humidity = defaultNoise;
                private Noise altitude = defaultNoise;
                private Noise weirdness = defaultNoise;
                private final List<Pair<Biome.Attributes, Supplier<Biome>>> biomes = new ArrayList<>();

                public MultiNoiseBiomeProviderBuilder changeSeed(long seed)
                {
                    this.seed = seed;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeTemperatureNoise(Noise temperature)
                {
                    this.temperature = temperature;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeHumidityNoise(Noise humidity)
                {
                    this.humidity = humidity;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeAltitudeNoise(Noise altitude)
                {
                    this.altitude = altitude;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeWeirdnessNoise(Noise weirdness)
                {
                    this.weirdness = weirdness;
                    return this;
                }

                public AttributeBiomePairBuilder addBiome()
                {
                    return new AttributeBiomePairBuilder();
                }

                public MultiNoiseBiomeProviderBuilder addPair(Biome.Attributes attribute, Supplier<Biome> biome)
                {
                    biomes.add(Pair.of(attribute, biome));
                    return this;
                }

                public NoiseChunkGeneratorBuilder finish ()
                {
                    return NoiseChunkGeneratorBuilder.this.setBiomeProvider(new NetherBiomeProvider(seed, biomes, temperature, humidity, altitude, weirdness));
                }

                public class AttributeBiomePairBuilder
                {
                    private float temperature = 0;
                    private float humidity = 0;
                    private float altitude = 0;
                    private float weirdness = 0;
                    private float offset = 0;
                    private Biome biome;

                    public AttributeBiomePairBuilder setBiome(ResourceLocation biome)
                    {
                        return setBiome(regOps.getObject(Registry.field_239720_u_, biome));
                    }

                    public AttributeBiomePairBuilder setBiome(RegistryKey<Biome> biome)
                    {
                        return setBiome(biome.func_240901_a_());
                    }

                    public AttributeBiomePairBuilder setBiome(Biome biome)
                    {
                        this.biome = biome;
                        return this;
                    }

                    public AttributeBiomePairBuilder setTemperature(float temperature)
                    {
                        checkRange(temperature);
                        this.temperature = temperature;
                        return this;
                    }

                    public AttributeBiomePairBuilder setHumidity(float humidity)
                    {
                        checkRange(humidity);
                        this.humidity = humidity;
                        return this;
                    }

                    public AttributeBiomePairBuilder setAltitude(float altitude)
                    {
                        checkRange(altitude);
                        this.altitude = altitude;
                        return this;
                    }

                    public AttributeBiomePairBuilder setWeirdness(float weirdness)
                    {
                        checkRange(weirdness);
                        this.weirdness = weirdness;
                        return this;
                    }

                    public AttributeBiomePairBuilder setOffset(float offset)
                    {
                        if(offset < 0 || offset > 1)
                            throw new IllegalStateException("Biome Attribute offset value was out of bounds");
                        this.offset = offset;
                        return this;
                    }

                    public MultiNoiseBiomeProviderBuilder finish()
                    {
                        Objects.requireNonNull(biome);
                        return MultiNoiseBiomeProviderBuilder.this.addPair(new Biome.Attributes(temperature, humidity, altitude, weirdness, offset), ()->biome);
                    }

                    public AttributeBiomePairBuilder buildNext()
                    {
                        return finish().addBiome();
                    }

                    private void checkRange(float f)
                    {
                        if(f < -2 || f > 2)
                            throw new IllegalStateException("Biome Attribute value was out of bounds");
                    }
                }
            }
        }
    }
}
