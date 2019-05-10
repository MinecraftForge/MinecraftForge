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
 * Base piston event, use {@link PistonEvent.Extend} and {@link PistonEvent.Retract} for specific movement types
 */
public class PistonEvent extends BlockEvent
{

    private final EnumFacing facing;
    
    /**
     * @param world
     * @param pos - The position of the piston
     * @param facing - The facing of the piston
     */
    public PistonEvent(World world, BlockPos pos, EnumFacing facing)
    {
        super(world, pos, world.getBlockState(pos));

        this.facing = facing;
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
     * Fired when a piston extends at any point, only once per extension. (This includes indirect extension movement, it still fires once)
     * Canceling prevents extension.
     */
    @Cancelable
    public static class Extend extends PistonEvent
    {
        public Extend(World world, BlockPos pos, EnumFacing facing)
        {
            super(world, pos, facing);
        }
        
        /**
         * @return A piston structure helper for this extension. This is equivalent to <code>new BlockPistonStructureHelper(this.getWorld(), this.getPos(), this.getFacing(), true)</code>
         */
        public BlockPistonStructureHelper getStructureHelper()
        {
            return new BlockPistonStructureHelper(this.getWorld(), this.getPos(), this.getFacing(), true);
        }
        
    }

    /**
     * Fired when a piston retracts at any point, only once per retraction. (This includes indirect retraction movement, it still fires once)
     * Canceling prevents retraction.
     */
    @Cancelable
    public static class Retract extends PistonEvent
    {
        public Retract(World world, BlockPos pos, EnumFacing facing)
        {
            super(world, pos, facing);
        }
        
        /**
         * @return A piston structure helper for this retraction. This is equivalent to <code>new BlockPistonStructureHelper(this.getWorld(), this.getPos(), this.getFacing(), false)</code>
         */
        public BlockPistonStructureHelper getStructureHelper()
        {
            return new BlockPistonStructureHelper(this.getWorld(), this.getPos(), this.getFacing(), false);
        }
    }
}