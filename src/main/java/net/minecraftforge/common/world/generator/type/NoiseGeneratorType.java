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

package net.minecraftforge.common.world.generator.type;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.common.world.generator.GeneratorType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class NoiseGeneratorType extends GeneratorType {

    private final RegistryKey<DimensionSettings> settings;

    protected NoiseGeneratorType(String name, RegistryKey<DimensionType> dimensionType, RegistryKey<DimensionSettings> settings) {
        super(name, dimensionType);
        this.settings = settings;
    }

    public final RegistryKey<DimensionSettings> getSettingsKey() {
        return settings;
    }

    @Override
    public ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> generatorOptions) {
        BiomeProvider biomeProvider = createBiomeProvider(seed, biomes);
        Supplier<DimensionSettings> settings = () -> dimensionSettings.func_230516_a_(getSettingsKey());
        return new NoiseChunkGenerator(biomeProvider, seed, settings);
    }

    public abstract BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes);
}
