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
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * This is a purely behavioral registry. It exists so mods can choose to control specific aspects of their own biomes if they please, and also use additional context provided to them in order to do so.
 *
 * Since biomes are dynamic and are created by vanilla internals, individual Biome objects may be copied or created on demand.
 * However, each biome object will look for an IModBiome sharing the same ID, and delegate to that.
 *
 * ONLY REGISTER ONE OF THESE FOR YOUR OWN BIOMES!
 */
public interface IBiomeBehavior extends IForgeRegistryEntry<IBiomeBehavior> {

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
     * Override if you want your
     * @param biome The biome being queried
     * @param world The world. May be null during world generation.
     * @param pos The position being queried
     * @return The temperature for the current position
     */
    default float getTemperature(Biome biome, @Nullable IWorldReader world, BlockPos pos)
    {
        return biome.getTemperature(pos);
    }
}
