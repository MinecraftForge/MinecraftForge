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

package net.minecraftforge.common.world.level.impl;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.common.world.level.LevelType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class NoiseLevelType extends LevelType
{
    private final RegistryKey<DimensionSettings> settings;

    protected NoiseLevelType(RegistryKey<DimensionSettings> settings)
    {
        this.settings = settings;
    }

    public final RegistryKey<DimensionSettings> getSettingsKey()
    {
        return settings;
    }

    @Override
    public ChunkGenerator createOverworldChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> settings, @Nullable Dynamic<?> generatorOptions)
    {
        BiomeProvider biomeProvider = createBiomeProvider(seed, biomes);
        return createChunkGenerator(seed, biomeProvider, settings);
    }

    public ChunkGenerator createChunkGenerator(long seed, BiomeProvider biomeProvider, Registry<DimensionSettings> settings)
    {
        Supplier<DimensionSettings> dimensionSettings = () -> settings.func_230516_a_(getSettingsKey());
        return new NoiseChunkGenerator(biomeProvider, seed, dimensionSettings);
    }

    public abstract BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes);
}
