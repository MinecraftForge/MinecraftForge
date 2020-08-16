package net.minecraftforge.common.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider.Noise;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public abstract class DimensionProvider extends CodecBackedProvider<Dimension> {
    protected final DataGenerator generator;
    protected final String modid;
    protected final Map<ResourceLocation, Dimension> map = new HashMap<>();
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected DimensionProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid) {
        super(Dimension.field_236052_a_, fileHelper);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache) {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/dimension/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation name, Dimension inst) {
        this.map.put(name, inst);
    }

    @Override
    public String getName() {
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
    public class Builder {
        private Supplier<DimensionType> dimType;
        private ChunkGenerator generator;

        public Builder setDimType(DimensionType type) {
            this.dimType = () -> type;
            return this;
        }

        public Builder setDimType(Supplier<DimensionType> type) {
            this.dimType = type;
            return this;
        }

        public Builder setDimType(ResourceLocation location) {
            return setDimType(DimensionProvider.this.getFromFile(DimensionType.field_236002_f_, location, Registry.field_239698_ad_));
        }

        public Builder setChunkGenerator(ChunkGenerator generator) {
            this.generator = generator;
            return this;
        }

        public NoiseChunkGeneratorBuilder buildNoise() {
            return new NoiseChunkGeneratorBuilder();
        }


        public Dimension build() {
            return new Dimension(dimType, generator);
        }

        public class NoiseChunkGeneratorBuilder {
            private BiomeProvider provider;
            private long seed = new Random().nextLong();
            private Supplier<DimensionSettings> settings = DimensionSettings::func_242746_i;

            public Builder finish(){
                return Builder.this.setChunkGenerator(new NoiseChunkGenerator(provider, seed,settings));
            }

            public NoiseChunkGeneratorBuilder setSettings(DimensionSettings settings) {
                this.settings = () -> settings;
                return this;
            }

            public NoiseChunkGeneratorBuilder setSettings(ResourceLocation location) {
                this.settings = DimensionProvider.this.getFromFile(DimensionSettings.field_236098_b_, location, Registry.field_243549_ar);
                return this;
            }

            public NoiseChunkGeneratorBuilder setSeed(long seed) {
                this.seed = seed;
                return this;
            }

            public NoiseChunkGeneratorBuilder setBiomeProvider(BiomeProvider provider) {
                this.provider = provider;
                return this;
            }

            public MultiNoiseBiomeProviderBuilder buildNoiseBiome() {
                return new MultiNoiseBiomeProviderBuilder();
            }

            /**
             * {@link NetherBiomeProvider} is called MultiNoise in the wiki.
             * It is the main way of manipulating the biomes of a dimension as the
             * {@link OverworldBiomeProvider} hardcodes its list of biomes.
             */
            public class MultiNoiseBiomeProviderBuilder {
                private final Noise defaultNoise = new Noise(-7, ImmutableList.of(1.0D, 1.0D));
                private long seed = NoiseChunkGeneratorBuilder.this.seed;
                private final List<Pair<Biome.Attributes, Supplier<Biome>>> biomes = new ArrayList<>();
                private Noise temperature = defaultNoise;
                private Noise humidity = defaultNoise;
                private Noise altitude = defaultNoise;
                private Noise weirdness = defaultNoise;

                public MultiNoiseBiomeProviderBuilder changeSeed(long seed) {
                    this.seed = seed;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeTemperatureNoise(Noise temperature) {
                    this.temperature = temperature;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeHumidityNoise(Noise humidity) {
                    this.humidity = humidity;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeAltitudeNoise(Noise altitude) {
                    this.altitude = altitude;
                    return this;
                }

                public MultiNoiseBiomeProviderBuilder changeWeirdnessNoise(Noise weirdness) {
                    this.weirdness = weirdness;
                    return this;
                }

                public AttributeBiomePairBuilder addBiome() {
                    return new AttributeBiomePairBuilder();
                }

                public MultiNoiseBiomeProviderBuilder addPair(Biome.Attributes attribute, Supplier<Biome> biome){
                    biomes.add(Pair.of(attribute, biome));
                    return this;
                }

                public NoiseChunkGeneratorBuilder finish () {
                    return NoiseChunkGeneratorBuilder.this.setBiomeProvider(new NetherBiomeProvider(seed, biomes, temperature, humidity, altitude, weirdness));
                }

                public class AttributeBiomePairBuilder {
                    private float temperature = 0;
                    private float humidity = 0;
                    private float altitude = 0;
                    private float weirdness = 0;
                    private float offset = 0;
                    private Biome biome;

                    public AttributeBiomePairBuilder setBiome(ResourceLocation biome) {
                        return setBiome(DimensionProvider.this.getFromFile(Biome.field_235051_b_, biome, Registry.field_239720_u_).get());
                    }

                    public AttributeBiomePairBuilder setBiome(Biome biome) {
                        this.biome = biome;
                        return this;
                    }

                    public AttributeBiomePairBuilder setTemperature(float temperature) {
                        checkRange(temperature);
                        this.temperature = temperature;
                        return this;
                    }

                    public AttributeBiomePairBuilder setHumidity(float humidity) {
                        checkRange(humidity);
                        this.humidity = humidity;
                        return this;
                    }

                    public AttributeBiomePairBuilder setAltitude(float altitude) {
                        checkRange(altitude);
                        this.altitude = altitude;
                        return this;
                    }

                    public AttributeBiomePairBuilder setWeirdness(float weirdness) {
                        checkRange(weirdness);
                        this.weirdness = weirdness;
                        return this;
                    }

                    public AttributeBiomePairBuilder setOffset(float offset) {
                        if(offset < 0 || offset > 1)
                            throw new IllegalStateException("Biome Attribute offset value was out of bounds");
                        this.offset = offset;
                        return this;
                    }

                    public MultiNoiseBiomeProviderBuilder finish() {
                        Objects.requireNonNull(biome);
                        return MultiNoiseBiomeProviderBuilder.this.addPair(new Biome.Attributes(temperature, humidity, altitude, weirdness, offset), ()->biome);
                    }

                    public AttributeBiomePairBuilder buildNext() {
                        return finish().addBiome();
                    }

                    private void checkRange(float f) {
                        if(f < -2 || f > 2)
                            throw new IllegalStateException("Biome Attribute value was out of bounds");
                    }
                }
            }
        }
    }
}
