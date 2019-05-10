/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.event.world;

import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Base piston event, use {@link PistonEvent.Post} and {@link PistonEvent.Pre}
 */
public class PistonEvent extends BlockEvent
{

    private final EnumFacing facing;
    private final PistonMoveDirection direction;

    /**
     * @param world
     * @param pos - The position of the piston
     * @param facing - The facing of the piston
     * @param direction - The move direction of the piston
     */
    public PistonEvent(World world, BlockPos pos, EnumFacing facing, PistonMoveDirection direction)
    {
        super(world, pos, world.getBlockState(pos));
        this.facing = facing;
        this.direction = direction;
    }

    /**
     * @return The facing of the piston block
     */
    public EnumFacing getFacing()
    {
        return this.facing;
    }

    /**
     * Helper method that gets the piston position offset by its facing
     */
    public BlockPos getFaceOffsetPos()
    {
        return this.getPos().offset(facing);
    }

    /**
     * @return The movement direction of the piston
     */
    public PistonMoveDirection getDirection()
    {
        return direction;
    }

    /**
     * @return A piston structure helper for this movement. This is equivalent to <code>new BlockPistonStructureHelper(this.getWorld(), this.getPos(), this.getFacing(), this.getDirection().isExtend)</code>
     */
    public BlockPistonStructureHelper getStructureHelper()
    {
        return new BlockPistonStructureHelper(this.getWorld(), this.getPos(), this.getFacing(), this.getDirection().isExtend);
    }

    /**
     * Fires after the piston has moved and set surrounding states. This will not fire if {@link PistonEvent.Pre} is cancelled.
     */
    public static class Post extends PistonEvent
    {

        public Post(World world, BlockPos pos, EnumFacing facing, PistonMoveDirection direction)
        {
            super(world, pos, facing, direction);
        }

    }

    /**
     * Fires before the piston has updated block states. Cancellation prevents movement.
     */
    @Cancelable
    public static class Pre extends PistonEvent
    {

        public Pre(World world, BlockPos pos, EnumFacing facing, PistonMoveDirection direction)
        {
            super(world, pos, facing, direction);
        }

    }

    public static enum PistonMoveDirection
    {
        EXTEND(true),
        RETRACT(false);

        public final boolean isExtend;

        PistonMoveDirection(boolean isExtend) {
            this.isExtend = isExtend;
        }
    }

}