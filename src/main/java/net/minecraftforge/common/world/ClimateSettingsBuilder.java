/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import net.minecraft.world.level.biome.Biome.ClimateSettings;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;

/**
 * Builder for {@link ClimateSettings}.
 */
public class ClimateSettingsBuilder
{
    private Precipitation precipitation;
    private float temperature;
    private TemperatureModifier temperatureModifier;
    private float downfall;

    /**
     * @param settings Existing ClimateSettings.
     * @return A new builder with a copy of that ClimateSettings's values.
     */
    public static ClimateSettingsBuilder copyOf(ClimateSettings settings)
    {
        return create(settings.precipitation(), settings.temperature(), settings.temperatureModifier(), settings.downfall());
    }

    /**
     * @param precipitation Synced to clients, determines weather effects
     * @param temperature Synced to clients, affects foliage color, freezing, and weather effects.
     * Vanilla values are in the range [-0.5, 2.0]
     * @param temperatureModifier Synced to clients, applies a positional modifier to temperature.
     * Frozen Oceans use this to have occasional warm patches.
     * @param downfall Synced to clients, affects foliage color.
     * Biomes with downfall > 0.85 count as humid, inhibiting fire spread.
     * @return a new builder with the given values
     */
    public static ClimateSettingsBuilder create(Precipitation precipitation, float temperature, TemperatureModifier temperatureModifier, float downfall)
    {
        return new ClimateSettingsBuilder(precipitation, temperature, temperatureModifier, downfall);
    }

    private ClimateSettingsBuilder(Precipitation precipitation, float temperature, TemperatureModifier temperatureModifier, float downfall)
    {
        this.precipitation = precipitation;
        this.temperature = temperature;
        this.temperatureModifier = temperatureModifier;
        this.downfall = downfall;
    }

    /**
     * @return A new ClimateSettings with the finalized values.
     */
    public ClimateSettings build()
    {
        return new ClimateSettings(this.precipitation, this.temperature, this.temperatureModifier, this.downfall);
    }

    /**
     * @return Synced to clients, determines weather effects.
     */
    public Precipitation getPrecipitation()
    {
        return precipitation;
    }

    /**
     * @param precipitation Synced to clients, determines weather effects.
     */
    public void setPrecipitation(Precipitation precipitation)
    {
        this.precipitation = precipitation;
    }

    /**
     * {@return Synced to clients, affects foliage color, freezing, and weather effects}
     * Vanilla values are in the range [-0.5, 2.0].
     */
    public float getTemperature()
    {
        return temperature;
    }

    /**
     * @param temperature Synced to clients, affects foliage color, freezing, and weather effects.
     * Vanilla values are in the range [-0.5, 2.0].
     */
    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }

    /**
     * {@return temperatureModifier Synced to clients, applies a positional modifier to temperature.}
     * Frozen Oceans use this to have occasional warm patches.
     */
    public TemperatureModifier getTemperatureModifier()
    {
        return temperatureModifier;
    }

    /**
     * @param temperatureModifier Synced to clients, applies a positional modifier to temperature.
     * Frozen Oceans use this to have occasional warm patches.
     */
    public void setTemperatureModifier(TemperatureModifier temperatureModifier)
    {
        this.temperatureModifier = temperatureModifier;
    }

    /**
     * {@return Synced to clients, affects foliage color.}
     * Biomes with downfall > 0.85 count as humid, inhibiting fire spread.
     */
    public float getDownfall()
    {
        return downfall;
    }

    /**
     * @param downfall Synced to clients, affects foliage color.
     * Biomes with downfall > 0.85 count as humid, inhibiting fire spread.
     */
    public void setDownfall(float downfall)
    {
        this.downfall = downfall;
    }
}
