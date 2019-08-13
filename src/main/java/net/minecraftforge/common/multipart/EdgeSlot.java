/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.multipart;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * A slot on the edge of a block.
 *
 * @see IBlockSlot
 */
public enum EdgeSlot implements IBlockSlot
{
    DOWN_NORTH(Direction.DOWN, Direction.NORTH, Direction.Axis.X),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH, Direction.Axis.X),
    DOWN_WEST(Direction.DOWN, Direction.WEST, Direction.Axis.Z),
    DOWN_EAST(Direction.DOWN, Direction.EAST, Direction.Axis.Z),
    UP_NORTH(Direction.UP, Direction.NORTH, Direction.Axis.X),
    UP_SOUTH(Direction.UP, Direction.SOUTH, Direction.Axis.X),
    UP_WEST(Direction.UP, Direction.WEST, Direction.Axis.Z),
    UP_EAST(Direction.UP, Direction.EAST, Direction.Axis.Z),
    NORTH_WEST(Direction.NORTH, Direction.WEST, Direction.Axis.Y),
    NORTH_EAST(Direction.NORTH, Direction.EAST, Direction.Axis.Y),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST, Direction.Axis.Y),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST, Direction.Axis.Y);

    private final Direction direction1, direction2;
    private final Direction.Axis axis;
    private final ResourceLocation name;

    EdgeSlot(Direction direction1, Direction direction2, Direction.Axis axis)
    {
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.axis = axis;
        this.name = new ResourceLocation("forge", "edge_" + direction1.getName() + "_" + direction2.getName());
    }

    public Direction getFirstDirection()
    {
        return direction1;
    }

    public Direction getSecondDirection()
    {
        return direction2;
    }

    @Override
    public IBlockSlot setRegistryName(ResourceLocation name)
    {
        throw new IllegalStateException("Attempted to override registry name of: " + getRegistryName());
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return name;
    }

    @Override
    public Class<IBlockSlot> getRegistryType()
    {
        return IBlockSlot.class;
    }

    private static final EdgeSlot[][] SLOTS = {
        { null, null, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, DOWN_EAST },
        { null, null, UP_NORTH, UP_SOUTH, UP_WEST, DOWN_EAST },
        { DOWN_NORTH, UP_NORTH, null, null, NORTH_WEST, NORTH_EAST },
        { DOWN_SOUTH, UP_SOUTH, null, null, SOUTH_WEST, SOUTH_EAST },
        { DOWN_WEST, UP_WEST, NORTH_WEST, SOUTH_WEST, null, null },
        { DOWN_EAST, UP_EAST, NORTH_EAST, SOUTH_EAST, null, null }
    };

    @Nullable
    public static EdgeSlot between(Direction first, Direction second)
    {
        return SLOTS[first.ordinal()][second.ordinal()];
    }
}
