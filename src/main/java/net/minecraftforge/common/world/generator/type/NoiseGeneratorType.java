package net.minecraftforge.common.world.generator.type;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.common.world.generator.GeneratorType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class NoiseGeneratorType extends GeneratorType {

    private final RegistryKey<DimensionSettings> settings;

    protected NoiseGeneratorType(String name, RegistryKey<DimensionType> type, RegistryKey<DimensionSettings> settings) {
        super(name, type);
        this.settings = settings;
    }

    public final RegistryKey<DimensionSettings> getSettingsKey() {
        return settings;
    }

    @Override
    public ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions) {
        BiomeProvider biomeProvider = createBiomeProvider(seed, biomes);
        Supplier<DimensionSettings> settings = () -> dimensionSettings.func_230516_a_(getSettingsKey());
        return new NoiseChunkGenerator(biomeProvider, seed, settings);
    }

    public abstract BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes);
}
