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
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.CreateFlatWorldScreen;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.generator.GeneratorSettingsHelper;
import net.minecraftforge.common.world.generator.GeneratorType;

import javax.annotation.Nullable;
import java.util.Optional;

public class FlatGeneratorType extends GeneratorType {

    public FlatGeneratorType(String name, RegistryKey<DimensionType> dimensionType) {
        super(name, dimensionType);
    }

    @Override
    public ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings, @Nullable Dynamic<?> options) {
        FlatGenerationSettings settings = parseOptions(biomes, options);
        return new FlatChunkGenerator(settings);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public BiomeGeneratorTypeScreens.IFactory getEditScreen() {
        return (screen, settings) -> {
            DynamicRegistries registries = screen.field_238934_c_.func_239055_b_();
            return new CreateFlatWorldScreen(
                    screen,
                    result -> screen.field_238934_c_.func_239043_a_(updateSettings(settings, registries, result)),
                    getCurrentSettings(registries, settings.func_236225_f_())
            );
        };
    }

    private FlatGenerationSettings parseOptions(Registry<Biome> biomes, @Nullable Dynamic<?> options) {
        return Optional.ofNullable(options)
                .map(FlatGenerationSettings.field_236932_a_::parse)
                .flatMap(result -> result.resultOrPartial(GeneratorType.LOGGER::warn))
                .orElseGet(() -> FlatGenerationSettings.func_242869_a(biomes));
    }

    private DimensionGeneratorSettings updateSettings(DimensionGeneratorSettings settings, DynamicRegistries registries, FlatGenerationSettings flatSettings) {
        ChunkGenerator generator = new FlatChunkGenerator(flatSettings);
        return GeneratorSettingsHelper.setChunkGenerator(settings, registries, getDimensionTypeKey(), generator);
    }

    private FlatGenerationSettings getCurrentSettings(DynamicRegistries registries, ChunkGenerator generator) {
        if (generator instanceof FlatChunkGenerator) {
            return ((FlatChunkGenerator) generator).func_236073_g_();
        }
        return FlatGenerationSettings.func_242869_a(registries.func_243612_b(Registry.field_239720_u_));
    }
}
