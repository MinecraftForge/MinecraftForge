package net.minecraftforge.event.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PistonEvent extends BlockEvent
{
    /** Stores the direction of the piston */
    public final EnumFacing facing;

    public PistonEvent(World world, BlockPos pos, EnumFacing facing)
    {
        super(world, pos, world.getBlockState(pos));
        
        this.facing = facing;
    }
    
    /***
     * Posts when a piston tries to extend
     * If canceled the piston will not extend
     ***/
    @Cancelable
    public static class PistonExtendEvent extends PistonEvent
    {
        public PistonExtendEvent(World world, BlockPos pos, EnumFacing facing)
        {
            super(world, pos, facing);
        }
    }
    
    /***
     * Posts when a piston tries to retract
     * If canceled the piston will not pull the block it is attached to
     ***/
    @Cancelable
    public static class PistonRetractEvent extends PistonEvent
    {
        public PistonRetractEvent(World world, BlockPos pos, EnumFacing facing)
        {
            super(world, pos, facing);
        }
    }
}
