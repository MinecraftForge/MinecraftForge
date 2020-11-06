package net.minecraftforge.common.world.biomes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;

import javax.annotation.Nullable;

public class BiomeExposer
{
    private final ResourceLocation name;
    private Biome.Climate climate;
    private Biome.Category category;
    private float depth;
    private float scale;
    private BiomeAmbience effects;
    private final BiomeGenerationSettingsBuilder gen;
    private final MobSpawnInfoBuilder spawns;

    public static BiomeExposer fromBiome(final Biome biome)
    {
        return new BiomeExposer(biome.getRegistryName(), new Biome.Climate(biome.getPrecipitation(), biome.getTemperature(), biome.getTemperatureModifier(), biome.getDownfall()), biome.getCategory(), biome.getDepth(), biome.getScale(), biome.getAmbience(), new BiomeGenerationSettingsBuilder(biome.getGenerationSettings()), new MobSpawnInfoBuilder(biome.getMobSpawnInfo()));
    }

    public BiomeExposer(@Nullable final ResourceLocation name, final Biome.Climate climate, final Biome.Category category, final float depth, final float scale, final BiomeAmbience effects, final BiomeGenerationSettingsBuilder gen, final MobSpawnInfoBuilder spawns)
    {
        this.name = name;
        this.climate = climate;
        this.category = category;
        this.depth = depth;
        this.scale = scale;
        this.effects = effects;
        this.gen = gen;
        this.spawns = spawns;
    }

    /**
     * This will get the registry name of the biome.
     * It generally SHOULD NOT be null, but due to vanilla's biome handling and codec weirdness, there may be cases where it is.
     * Do check for this possibility!
     */
    @Nullable
    public ResourceLocation getName()
    {
        return name;
    }

    public Biome.Climate getClimate()
    {
        return climate;
    }

    public void setClimate(final Biome.Climate value)
    {
        this.climate = value;
    }

    public Biome.Category getCategory()
    {
        return category;
    }

    public void setCategory(final Biome.Category value)
    {
        this.category = value;
    }

    public float getDepth()
    {
        return depth;
    }

    public void setDepth(final float value)
    {
        this.depth = value;
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(final float value)
    {
        this.scale = value;
    }

    public BiomeAmbience getEffects()
    {
        return effects;
    }

    public void setEffects(final BiomeAmbience value)
    {
        this.effects = value;
    }

    public BiomeGenerationSettingsBuilder getGeneration()
    {
        return gen;
    }

    public MobSpawnInfoBuilder getSpawns()
    {
        return spawns;
    }

    public Biome createBiome()
    {
        return new Biome.Builder()
                .withMobSpawnSettings(spawns.copy())
                .withGenerationSettings(gen.build())
                .withTemperatureModifier(climate.temperatureModifier)
                .setEffects(effects)
                .depth(depth)
                .scale(scale)
                .downfall(climate.downfall)
                .precipitation(climate.precipitation)
                .category(category)
                .temperature(climate.temperature)
                .build();
    }
}
