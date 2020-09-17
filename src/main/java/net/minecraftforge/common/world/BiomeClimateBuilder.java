/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.world.biome.Biome;

public class BiomeClimateBuilder {

    private Biome.RainType precipitation;
    private float temperature;
    private Biome.TemperatureModifier temperatureModifier;
    private float downfall;

    public BiomeClimateBuilder(Biome.RainType precipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall) {
        this.setPrecipitation(precipitation);
        this.setTemperature(temperature);
        this.setTemperatureModifier(temperatureModifier);
        this.setDownfall(downfall);
    }

    public Biome.RainType getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Biome.RainType precipitation) {
        this.precipitation = precipitation;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public Biome.TemperatureModifier getTemperatureModifier() {
        return temperatureModifier;
    }

    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
        this.temperatureModifier = temperatureModifier;
    }

    public float getDownfall() {
        return downfall;
    }

    public void setDownfall(float downfall) {
        this.downfall = downfall;
    }

    public Biome.Climate build() {
        return new Biome.Climate(precipitation, temperature, temperatureModifier, downfall);
    }
}