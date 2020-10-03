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

package net.minecraftforge.common.world.generator;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

import java.util.function.Supplier;

public class GeneratorSettingsHelper {

    public static DimensionGeneratorSettings setChunkGenerator(DimensionGeneratorSettings settings, DynamicRegistries registries, RegistryKey<DimensionType> dimensionType, ChunkGenerator generator) {
        long seed = settings.func_236221_b_();
        boolean hasStructures = settings.func_236222_c_();
        boolean hasBonusChest = settings.func_236223_d_();
        SimpleRegistry<Dimension> dimensions = setChunkGenerator(registries, settings.func_236224_e_(), dimensionType, generator);
        return new DimensionGeneratorSettings(seed, hasStructures, hasBonusChest, dimensions);
    }

    public static SimpleRegistry<Dimension> setChunkGenerator(DynamicRegistries registries, SimpleRegistry<Dimension> dimensions, RegistryKey<DimensionType> dimensionType, ChunkGenerator generator) {
        Registry<DimensionType> dimensionTypes = registries.func_243612_b(Registry.field_239698_ad_);
        Supplier<DimensionType> dimensionTypeSupplier = () -> dimensionTypes.func_230516_a_(dimensionType);
        return DimensionGeneratorSettings.func_241520_a_(dimensions, dimensionTypeSupplier, generator);
    }
}
