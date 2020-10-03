package net.minecraftforge.common.world.generator.type;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraftforge.common.world.generator.GeneratorType;

import javax.annotation.Nullable;
import java.util.Optional;

public class FlatGeneratorType extends GeneratorType {

    public FlatGeneratorType(String name, RegistryKey<DimensionType> dimensionType) {
        super(name, dimensionType);
    }

    @Override
    public ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> options) {
        FlatGenerationSettings settings = parseOptions(biomes, options);
        return new FlatChunkGenerator(settings);
    }

    protected FlatGenerationSettings parseOptions(Registry<Biome> biomes, @Nullable Dynamic<?> options) {
        return Optional.ofNullable(options)
                .map(FlatGenerationSettings.field_236932_a_::parse)
                .flatMap(result -> result.resultOrPartial(GeneratorType.LOGGER::warn))
                .orElseGet(() -> FlatGenerationSettings.func_242869_a(biomes));
    }
}
