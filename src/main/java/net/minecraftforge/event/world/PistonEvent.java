package net.minecraftforge.event.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PistonEvent extends BlockEvent
{
    /** Stores the direction of the piston */
    private final EnumFacing facing;

    public PistonEvent(World world, BlockPos pos, EnumFacing facing)
    {
        super(world, pos, world.getBlockState(pos));
        
        this.facing = facing;
    }
    
    public EnumFacing getFacing() {
        return this.facing;
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
     * Posts when a sticky piston tries to retract
     * If canceled the piston will not pull the block it is attached to, this will not stop retraction
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