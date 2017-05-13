package net.minecraftforge.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface IParticleColorControlling 
{
    public boolean preventColoring(IBlockState blockState, BlockPos pos);
}
