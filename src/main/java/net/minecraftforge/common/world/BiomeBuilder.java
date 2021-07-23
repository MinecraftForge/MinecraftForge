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

package net.minecraftforge.common.world;

import net.minecraft.world.level.biome.Biome;

/**
 * Helper for building a biome using a deep copy of another biome as a starting point; also provides read access to current parameters.
 * Also enforces existence of non-optional parameters on construction.
 * (doesn't extend Biome.Builder as this uses builders for the effects/generation/spawns and the vanilla biome builder doesn't)
 **/
public class BiomeBuilder implements IBiomeParameters
{
    private Biome.BiomeCategory category = Biome.BiomeCategory.NONE;
    private float depth;
    private float scale;
    private Biome.Precipitation precipitation = Biome.Precipitation.NONE;
    private float temperature;
    private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
    private float downfall;
    private final BiomeSpecialEffectsBuilder effectsBuilder;
    private final BiomeGenerationSettingsBuilder generationBuilder;
    private final MobSpawnInfoBuilder spawnBuilder;
    
    public static BiomeBuilder copyFrom(Biome biome)
    {
        BiomeSpecialEffectsBuilder effects = BiomeSpecialEffectsBuilder.copyFrom(biome.getSpecialEffects());
        BiomeGenerationSettingsBuilder gen = new BiomeGenerationSettingsBuilder(biome.getGenerationSettings());
        MobSpawnInfoBuilder spawns = new MobSpawnInfoBuilder(biome.getMobSettings());
        return new BiomeBuilder(biome.getBiomeCategory(), biome.getDepth(), biome.getScale(), biome.getPrecipitation(), biome.getBaseTemperature(), biome.climateSettings.temperatureModifier, biome.getDownfall(), effects, gen, spawns);
    }
    
    protected BiomeBuilder(Biome.BiomeCategory category, float depth, float scale, Biome.Precipitation precipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall, BiomeSpecialEffectsBuilder effects, BiomeGenerationSettingsBuilder gen, MobSpawnInfoBuilder spawns)
    {
        this.precipitation = precipitation;
        this.temperature = temperature;
        this.temperatureModifier = temperatureModifier;
        this.downfall = downfall;
        this.category = category;
        this.depth = depth;
        this.scale = scale;
        this.effectsBuilder = effects;
        this.generationBuilder = gen;
        this.spawnBuilder = spawns;
    }

    @Override
    public Biome.BiomeCategory getCategory()
    {
        return this.category;
    }

    @Override
    public void setCategory(Biome.BiomeCategory category)
    {
        this.category = category;
    }

    @Override
    public float getDepth()
    {
        return this.depth;
    }

    @Override
    public void setDepth(float depth)
    {
        this.depth = depth;
    }

    @Override
    public float getScale()
    {
        return this.scale;
    }

    @Override
    public void setScale(float scale)
    {
        this.scale = scale;
    }
    
    @Override
    public Biome.Precipitation getPrecipitation()
    {
        return this.precipitation;
    }

    @Override
    public void setPrecipitation(Biome.Precipitation precipitation)
    {
        this.precipitation = precipitation;
    }

    @Override
    public float getTemperature()
    {
        return this.temperature;
    }

    @Override
    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }

    @Override
    public Biome.TemperatureModifier getTemperatureModifier()
    {
        return this.temperatureModifier;
    }

    @Override
    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier)
    {
        this.temperatureModifier = temperatureModifier;
    }

    @Override
    public float getDownfall()
    {
        return this.downfall;
    }

    @Override
    public void setDownfall(float downfall)
    {
        this.downfall = downfall;
    }

    @Override
    public BiomeSpecialEffectsBuilder getEffectsBuilder()
    {
        return this.effectsBuilder;
    }

    @Override
    public BiomeGenerationSettingsBuilder getGenerationBuilder()
    {
        return this.generationBuilder;
    }

    @Override
    public MobSpawnInfoBuilder getSpawnBuilder()
    {
        return this.spawnBuilder;
    }
    
    public Biome build()
    {
        return new Biome.BiomeBuilder()
            .biomeCategory(this.getCategory())
            .depth(this.getDepth())
            .scale(this.getScale())
            .precipitation(this.getPrecipitation())
            .temperature(this.getTemperature())
            .downfall(this.getDownfall())
            .specialEffects(this.getEffectsBuilder().build())
            .generationSettings(this.generationBuilder.build())
            .mobSpawnSettings(this.getSpawnBuilder().build())
            .build();
    }
}
