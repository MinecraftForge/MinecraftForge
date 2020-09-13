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

package net.minecraftforge.event;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.eventbus.api.Event;

public class BiomeLoadingEvent extends Event {
    private final ResourceLocation resourceLocation;
    private Biome.Climate climate;
    private Biome.Category category;
    private Float depth;
    private Float scale;
    private BiomeAmbience biomeAmbience;
    private final BiomeGenerationSettings.Builder settingsBuilder;
    private final MobSpawnInfo.Builder mobSpawnInfoBuilder;

    public BiomeLoadingEvent(final ResourceLocation resourceLocation, final Biome.Climate climate, final Biome.Category category, final Float depth, final Float scale, final BiomeAmbience biomeAmbience, final BiomeGenerationSettings.Builder settingsBuilder, final MobSpawnInfo.Builder mobSpawnInfoBuilder) {
        this.resourceLocation = resourceLocation;
        this.climate = climate;
        this.category = category;
        this.depth = depth;
        this.scale = scale;
        this.biomeAmbience = biomeAmbience;
        this.settingsBuilder = settingsBuilder;
        this.mobSpawnInfoBuilder = mobSpawnInfoBuilder;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public Biome.Climate getClimate() {
        return climate;
    }

    public Biome.Category getCategory() {
        return category;
    }

    public Float getDepth() {
        return depth;
    }

    public Float getScale() {
        return scale;
    }

    public BiomeAmbience getBiomeAmbience() {
        return biomeAmbience;
    }

    public BiomeGenerationSettings.Builder getSettingsBuilder() {
        return settingsBuilder;
    }

    public MobSpawnInfo.Builder getMobSpawnInfoBuilder() {
        return mobSpawnInfoBuilder;
    }

    public void setClimate(final Biome.Climate climate) {
        this.climate = climate;
    }

    public void setCategory(final Biome.Category category) {
        this.category = category;
    }

    public void setDepth(final Float depth) {
        this.depth = depth;
    }

    public void setScale(final Float scale) {
        this.scale = scale;
    }

    public void setBiomeAmbience(final BiomeAmbience biomeAmbience) {
        this.biomeAmbience = biomeAmbience;
    }
}
