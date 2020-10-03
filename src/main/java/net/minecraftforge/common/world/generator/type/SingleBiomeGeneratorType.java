package net.minecraftforge.common.world.generator.type;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.CreateBuffetWorldScreen;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.generator.GeneratorSettingsHelper;

public class SingleBiomeGeneratorType extends NoiseGeneratorType {

    private final RegistryKey<Biome> biome;

    public SingleBiomeGeneratorType(String name, RegistryKey<DimensionType> type, RegistryKey<DimensionSettings> settings, RegistryKey<Biome> biome) {
        super(name, type, settings);
        this.biome = biome;
    }

    public final RegistryKey<Biome> getBiomeKey() {
        return biome;
    }

    @Override
    public BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes) {
        return new SingleBiomeProvider(biomes.func_243576_d(biome));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BiomeGeneratorTypeScreens.IFactory getEditScreen() {
        return (parent, settings) -> new CreateBuffetWorldScreen(
                parent,
                parent.field_238934_c_.func_239055_b_(),
                biome -> parent.field_238934_c_.func_239043_a_(setSingleBiome(settings, parent.field_238934_c_.func_239055_b_(), biome)),
                getCurrentBiome(parent.field_238934_c_.func_239055_b_(), settings)
        );
    }

    protected Biome getCurrentBiome(DynamicRegistries registries, DimensionGeneratorSettings settings) {
        Registry<Biome> biomes = registries.func_243612_b(Registry.field_239720_u_);
        return settings.func_236225_f_().getBiomeProvider().func_235203_c_().stream()
                .findFirst()
                .orElse(biomes.func_230516_a_(getBiomeKey()));
    }

    protected DimensionGeneratorSettings setSingleBiome(DimensionGeneratorSettings settings, DynamicRegistries registries, Biome biome) {
        long seed = settings.func_236221_b_();
        Registry<DimensionSettings> dimensionSettings = registries.func_243612_b(Registry.field_243549_ar);
        SingleBiomeProvider biomeProvider = new SingleBiomeProvider(biome);
        ChunkGenerator generator = new NoiseChunkGenerator(biomeProvider, seed, () -> dimensionSettings.func_230516_a_(getSettingsKey()));
        return GeneratorSettingsHelper.setChunkGenerator(settings, registries, getDimensionTypeKey(), generator);
    }
}
