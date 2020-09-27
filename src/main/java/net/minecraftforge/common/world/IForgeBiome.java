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

import javax.annotation.Nullable;

/**
 * This exists as a bouncer interface from biomes to the respective biome behavior
 */
public interface IForgeBiome {

    default Biome getBiome()
    {
        return (Biome) this;
    }

    /**
     * A replacement for {@link Biome#getPrecipitation()} which takes into account the world and position
     */
    default Biome.RainType getPrecipitation(IWorldReader world, BlockPos pos)
    {
        IBiomeBehavior behavior = getBiome().getBehavior();
        if (behavior != null)
        {
            return behavior.getPrecipitation(getBiome(), world, pos);
        }
        return getBiome().getPrecipitation();
    }

    /**
     * A replacement for {@link Biome#getTemperature(BlockPos)} which takes into account the world
     */
    default float getTemperature(@Nullable IWorldReader world, BlockPos pos)
    {
        IBiomeBehavior behavior = getBiome().getBehavior();
        if (behavior != null)
        {
            return behavior.getTemperature(getBiome(), world, pos);
        }
        return getBiome().getTemperature(pos);
    }
}
