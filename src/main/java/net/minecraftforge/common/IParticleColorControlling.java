package net.minecraftforge.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface IParticleColorControlling 
{
    @SideOnly(Side.CLIENT)
    public boolean preventColoring(IBlockState blockState, BlockPos pos);
}
