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
 * A slot on the corner of a block.
 *
 * @see IMultipartSlot
 */
public enum CornerSlot implements IMultipartSlot
{
    DOWN_NORTH_WEST(Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.NEGATIVE),
    DOWN_NORTH_EAST(Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.POSITIVE),
    DOWN_SOUTH_WEST(Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.POSITIVE, Direction.AxisDirection.NEGATIVE),
    DOWN_SOUTH_EAST(Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.POSITIVE, Direction.AxisDirection.POSITIVE),
    UP_NORTH_WEST(Direction.AxisDirection.POSITIVE, Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.NEGATIVE),
    UP_NORTH_EAST(Direction.AxisDirection.POSITIVE, Direction.AxisDirection.NEGATIVE, Direction.AxisDirection.POSITIVE),
    UP_SOUTH_WEST(Direction.AxisDirection.POSITIVE, Direction.AxisDirection.POSITIVE, Direction.AxisDirection.NEGATIVE),
    UP_SOUTH_EAST(Direction.AxisDirection.POSITIVE, Direction.AxisDirection.POSITIVE, Direction.AxisDirection.POSITIVE);

    private final Direction.AxisDirection dirX, dirY, dirZ;
    private final ResourceLocation name;

    // The permutations are ordered based on Direction, and so are the arguments (DOWN/UP, NORTH/SOUTH, WEST/EAST)
    // Read: these arguments are correct and not shuffled. Please don't try to "fix" them.
    CornerSlot(Direction.AxisDirection dirY, Direction.AxisDirection dirZ, Direction.AxisDirection dirX)
    {
        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;
        this.name = new ResourceLocation("forge", "corner_" + getDirectionString(dirY) + getDirectionString(dirZ) + getDirectionString(dirX));
    }

    public Direction.AxisDirection getXDirection()
    {
        return dirX;
    }

    public Direction.AxisDirection getYDirection()
    {
        return dirY;
    }

    public Direction.AxisDirection getZDirection()
    {
        return dirZ;
    }

    @Override
    public IMultipartSlot setRegistryName(ResourceLocation name)
    {
        throw new IllegalStateException("Attempted to override registry name of: " + getRegistryName());
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return name;
    }

    @Override
    public Class<IMultipartSlot> getRegistryType()
    {
        return IMultipartSlot.class;
    }

    private static final CornerSlot[] SLOTS = values();

    @Nullable
    public static CornerSlot on(Direction dir1, Direction dir2, Direction dir3)
    {
        Direction.Axis axis1 = dir1.getAxis(), axis2 = dir2.getAxis(), axis3 = dir3.getAxis();
        if (axis1 == axis2 || axis1 == axis3 || axis2 == axis3)
            return null;

        Direction.AxisDirection dirX = axis1 == Direction.Axis.X ? dir1.getAxisDirection() : axis2 == Direction.Axis.X ? dir2.getAxisDirection() : dir3.getAxisDirection();
        Direction.AxisDirection dirY = axis1 == Direction.Axis.Y ? dir1.getAxisDirection() : axis2 == Direction.Axis.Y ? dir2.getAxisDirection() : dir3.getAxisDirection();
        Direction.AxisDirection dirZ = axis1 == Direction.Axis.Z ? dir1.getAxisDirection() : axis2 == Direction.Axis.Z ? dir2.getAxisDirection() : dir3.getAxisDirection();
        return on(dirX, dirY, dirZ);
    }

    public static CornerSlot on(Direction.AxisDirection dirX, Direction.AxisDirection dirY, Direction.AxisDirection dirZ)
    {
        return SLOTS[(dirY.ordinal() << 2) | (dirZ.ordinal() << 1) | dirX.ordinal()];
    }

    private static String getDirectionString(Direction.AxisDirection direction)
    {
        return direction == Direction.AxisDirection.POSITIVE ? "p" : "n";
    }
}
