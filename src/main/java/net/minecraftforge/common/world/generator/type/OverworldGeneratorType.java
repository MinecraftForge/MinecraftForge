package net.minecraftforge.common.world.generator.type;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;

public class OverworldGeneratorType extends NoiseGeneratorType {

    private final boolean largeBiomes;
    private final boolean legacyInitLayer;

    public OverworldGeneratorType(String name, RegistryKey<DimensionSettings> settings, boolean legacy, boolean largeBiomes) {
        super(name, DimensionType.field_235999_c_, settings);
        this.legacyInitLayer = legacy;
        this.largeBiomes = largeBiomes;
    }

    public final boolean hasLargeBiomes() {
        return largeBiomes;
    }

    public final boolean hasLegacyInitLayer() {
        return legacyInitLayer;
    }

    @Override
    public BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes) {
        return new OverworldBiomeProvider(seed, legacyInitLayer, largeBiomes, biomes);
    }
}
