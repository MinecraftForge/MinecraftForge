package net.minecraftforge.common.world.generator.type;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.world.generator.GeneratorType;

import javax.annotation.Nullable;

public class DebugGeneratorType extends GeneratorType {

    public DebugGeneratorType(String name, RegistryKey<DimensionType> dimensionType) {
        super(name, dimensionType);
    }

    @Override
    public ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> options) {
        return new DebugChunkGenerator(biomes);
    }
}
