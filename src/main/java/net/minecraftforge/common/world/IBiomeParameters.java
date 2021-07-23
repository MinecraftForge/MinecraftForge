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
 * Interface for reading and writing biome parameters.
 */
public interface IBiomeParameters
{
    /**
     * Get the primary biome category; this is used by a few vanilla mechanics (e.g. for preventing things from spawning in rivers). Synced to clients.
     * @return biome category
     */
    public Biome.BiomeCategory getCategory();

    /**
     * Set the primary biome category; this is used by a few vanilla mechanics (e.g. for preventing things from spawning in rivers). Synced to clients.
     * @param category
     */
    public void setCategory(Biome.BiomeCategory category);

    /**
     * Get the biome depth. Synced to clients. Used by chunk generators to generate terrain; affects how much of the biome is above sea level (oceans have negative depth)
     * @return depth
     */
    public float getDepth();

    /**
     * Set the biome depth. Synced to clients. Used by chunk generators to generate terrain; affects how much of the biome is above sea level (oceans have negative depth)
     * @param Biome depth -- can be negative. Don't set depth to -2.0F as it causes divides-by-zeros in the noise chunk generator as of 1.16.5. Vanilla biomes have depths in the range [-1.8, 1.5]
     */
    public void setDepth(float depth);

    /**
     * Gets the biome scale. Synced to clients. Used by chunk generators, vertically stretches the terrain. Lower values => flatter, higher values => more mountainous
     * @return scale
     */
    public float getScale();

    /**
     * Sets the biome scale. Synced to clients. Used by chunk generators, vertically stretches the terrain. Lower values => flatter, higher values => more mountainous 
     * @param Biome scale -- vanilla biomes use the range [0.0, 1.225], most biomes are closer to 0, mountains are 0.5, shattered savannas are >1
     */
    public void setScale(float scale);
    
    /**
     * Gets the biome's weather type. Synced to clients, determines weather effects
     * @return rain type
     */
    public Biome.Precipitation getPrecipitation();

    /**
     * Sets the biome's weather type. Synced to clients, determines weather effects
     * @param precipitation weather type
     */
    public void setPrecipitation(Biome.Precipitation precipitation);

    /**
     * Gets the biome's base temperature. Synced to clients, affects foliage color. Also affects freezing and some weather mechanics
     * @return temperature
     */
    public float getTemperature();

    /**
     * Sets the biome's base temperature. Synced to clients, affects foliage color. Also affects freezing and some weather mechanics
     * @param temperature Biome temperature. Vanilla biomes have temperatures in the range [-0.5, 2.0]
     */
    public void setTemperature(float temperature);

    /**
     * Get the temperature modifier. Synced to clients, applies a positional modifier to temperature. Frozen Oceans use this to generate occasional warmer patches. 
     * @return temperature modifier
     */
    public Biome.TemperatureModifier getTemperatureModifier();

    /**
     * Set the temperature modifier. Synced to clients, applies a positional modifier to temperature. Frozen Oceans use this to generate occasional warmer patches.
     * @param temperatureModifier temperature modifier
     */
    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

    /**
     * Sets the biome's downfall. Synced to clients, affects foliage color. Biomes with downfall > 0.85 count as humid, inhibiting fire spread.
     * @return biome downfall
     */
    public float getDownfall();

    /**
     * Gets the biome's downfall. Synced to clients, affects foliage color. Biomes with downfall > 0.85 count as humid, inhibiting fire spread.
     * @param downfall Generally in the range [0.0, 1.0]
     */
    public void setDownfall(float downfall);

    /**
     * Gets the builder for the clientside effects (fog/water/sky color and sound events); these are synced to clients on login
     * @return effects builder
     */
    public BiomeSpecialEffectsBuilder getEffectsBuilder();

    /**
     * Gets the builder for the biome's worldgen features, structures, carvers, surfacebuilders. Not synced to clients.
     * @return worldgen builder
     */
    public BiomeGenerationSettingsBuilder getGenerationBuilder();

    /**
     * Gets the builder for the biome's mob spawn lists. Not synced to clients.
     * @return mob spawn builder
     */
    public MobSpawnInfoBuilder getSpawnBuilder();
}
