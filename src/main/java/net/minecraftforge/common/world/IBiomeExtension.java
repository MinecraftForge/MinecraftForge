/*
 * Minecraft Forge
 * Copyright (c) 2020.
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

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * This is an extension object that can be attached to biomes, either through the "forge:extensions" field on a biome json, or throuh BiomeLoadingEvent
 * These are registered through BiomeExtensionTypes.
 *
 * This can be used either to
 * a. attach additional custom data or functionality to biomes, or
 * b. intercept vanilla functionality for biomes by overriding the extension methods
 */
public interface IBiomeExtension {

    BiomeExtensionType getType();

    /**
     * Get the current rain type, dependent on the world and position
     * @param biome The biome being queried
     * @param world The world
     * @param pos The position being queried
     * @return The rain type for the current position
     */
    default Biome.RainType getPrecipitation(Biome biome, IWorldReader world, BlockPos pos)
    {
        return biome.getPrecipitation();
    }

    /**
     * @return true if this extension should be used to modify the precipitation via {@link IBiomeExtension#getPrecipitation(Biome, IWorldReader, BlockPos)}
     */
    default boolean modifiesPrecipitation()
    {
        return false;
    }

    /**
     * Get the current temperature, dependent on the world and position
     * @param biome The biome being queried
     * @param world The world. May be null during world generation.
     * @param pos The position being queried
     * @return The temperature for the current position
     */
    default float getTemperature(Biome biome, @Nullable IWorldReader world, BlockPos pos)
    {
        return biome.getTemperature(pos);
    }

    /**
     * @return true if this extension should be used to modify the temperature via {@link IBiomeExtension#getTemperature(Biome, IWorldReader, BlockPos)}
     */
    default boolean modifiesTemperature()
    {
        return false;
    }
}
