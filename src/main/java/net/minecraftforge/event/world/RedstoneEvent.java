package net.minecraftforge.event.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event;


public class RedstoneEvent extends Event
{

    private final IBlockState state;
    private final IBlockAccess world;
    private final BlockPos pos;
    private final EnumFacing side;
    private final int power;
    private int newPower;

    public RedstoneEvent(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, int power)
    {
        this.state = state;
        this.world = world;
        this.pos = pos;
        this.power = power;
        this.side = side;
        setNewPower(power);
    }

    public IBlockState getState()
    {
        return state;
    }

    public IBlockAccess getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }
    
    public EnumFacing getSide()
    {
        return side;
    }
    
    public int getPower()
    {
        return power;
    }

    public int getNewPower()
    {
        return newPower;
    }

    public void setNewPower(int newPower)
    {
        this.newPower = newPower;
    }
    
    public void increaseNewPowerTo(int newPower)
    {
        if (newPower > this.newPower)
            this.newPower = newPower;
    }
    
    public static class WeakOutput extends RedstoneEvent
    {        
        public WeakOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, int power)
        {
            super(state, world, pos, side, power);
        }
    }
    
    public static class StrongOutput extends RedstoneEvent
    {

        public StrongOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, int power)
        {
            super(state, world, pos, side, power);
        }

    }
}
