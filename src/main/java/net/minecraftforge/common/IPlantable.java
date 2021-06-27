/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPlantable
{
    default PlantType getPlantType(IBlockReader world, BlockPos pos) {
        if (this instanceof AttachedStemBlock) return PlantType.FARMLAND;
        if (this instanceof CropsBlock) return PlantType.FARMLAND;
        if (this instanceof DeadBushBlock) return PlantType.DEAD_BUSH;
        if (this instanceof FungusBlock) return PlantType.FUNGUS;
        if (this instanceof LilyPadBlock) return PlantType.LILY_PAD;
        if (this instanceof MushroomBlock) return PlantType.MUSHROOM;
        if (this instanceof NetherRootsBlock) return PlantType.NYLIUM_PLANTS;
        if (this instanceof NetherSproutsBlock) return PlantType.NYLIUM_PLANTS;
        if (this instanceof NetherWartBlock) return PlantType.NETHER_WART;
        if (this instanceof SeaGrassBlock) return PlantType.SEA_GRASS;
        if (this instanceof SeaPickleBlock) return PlantType.SEA_PICKLE;
        if (this instanceof StemBlock) return PlantType.FARMLAND;
        if (this instanceof TallSeaGrassBlock) return PlantType.SEA_GRASS;
        if (this instanceof WitherRoseBlock) return PlantType.WITHER_ROSE;

        if (this instanceof BushBlock) return PlantType.PLAINS;
        throw new IllegalStateException("The class implementing IPlantable has no PlantType!");
    }

    BlockState getPlant(IBlockReader world, BlockPos pos);
}