package net.minecraftforge.event.world;

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
     * Fired when a piston extends at any point, only once per extension.
     * Canceling prevents extension.
     */
    @Cancelable
    public static class Extend extends PistonEvent
    {
        public Extend(World world, BlockPos pos, EnumFacing facing)
        {
            super(world, pos, facing);
        }
    }

    /**
     * Fired when a piston retracts at any point, only once retraction.
     * Canceling prevents retraction.
     */
    @Cancelable
    public static class Retract extends PistonEvent
    {
        public Retract(World world, BlockPos pos, EnumFacing facing)
        {
            super(world, pos, facing);
        }
    }
}