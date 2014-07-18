package net.minecraftforge.event.world;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PistonEvent extends BlockEvent
{
    /** Stores the direction of the piston */
    public final ForgeDirection forgeDirection;
    
    public PistonEvent(World world, int x, int y, int z, ForgeDirection forgeDirection, Block piston)
    {
        super(x, y, z, world, piston, world.getBlockMetadata(x, y, z));
        
        this.forgeDirection = forgeDirection;
    }
    
    /***
     * Posts when a piston tries to extend
     * If canceled the piston will not extend
     ***/
    @Cancelable
    public static class PistonExtendEvent extends PistonEvent
    {
        public PistonExtendEvent(World world, int x, int y, int z, ForgeDirection forgeDirection, Block piston)
        {
            super(world, x, y, z, forgeDirection, piston);
        }
    }
    
    /***
     * Posts when a piston tries to retract
     * If canceled the piston will not pull the block it is attached to
     ***/
    @Cancelable
    public static class PistonRetractEvent extends PistonEvent
    {
        public PistonRetractEvent(World world, int x, int y, int z, ForgeDirection forgeDirection, Block piston)
        {
            super(world, x, y, z, forgeDirection, piston);
        }
    }
}
