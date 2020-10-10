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

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.CreateBuffetWorldScreen;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class SingleBiomeLevelType extends NoiseLevelType
{
    private final RegistryKey<Biome> biome;

    public SingleBiomeLevelType(RegistryKey<DimensionSettings> settings, RegistryKey<Biome> biome)
    {
        super(settings);
        this.biome = biome;
    }

    public final RegistryKey<Biome> getBiomeKey()
    {
        return biome;
    }

    @Override
    public BiomeProvider createBiomeProvider(long seed, Registry<Biome> biomes)
    {
        return new SingleBiomeProvider(biomes.func_243576_d(biome));
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public BiomeGeneratorTypeScreens.IFactory getEditScreen()
    {
        return (parent, settings) -> {
            DynamicRegistries registries = parent.field_238934_c_.func_239055_b_();
            return new CreateBuffetWorldScreen(
                    parent,
                    registries,
                    result -> parent.field_238934_c_.func_239043_a_(updateSettings(settings, registries, result)),
                    getCurrentBiome(registries, settings)
            );
        };
    }

    protected Biome getCurrentBiome(DynamicRegistries registries, DimensionGeneratorSettings settings)
    {
        Registry<Biome> biomes = registries.func_243612_b(Registry.field_239720_u_);
        return settings.func_236225_f_().getBiomeProvider().func_235203_c_().stream()
                .findFirst()
                .orElse(biomes.func_230516_a_(getBiomeKey()));
    }

    protected DimensionGeneratorSettings updateSettings(DimensionGeneratorSettings settings, DynamicRegistries registries, Biome biome)
    {
        long seed = settings.func_236221_b_();
        Registry<DimensionSettings> dimensionSettings = registries.func_243612_b(Registry.field_243549_ar);
        ChunkGenerator chunkGenerator = createChunkGenerator(seed, new SingleBiomeProvider(biome), dimensionSettings);
        return setOverworldGenerator(settings, registries, chunkGenerator);
    }
}
