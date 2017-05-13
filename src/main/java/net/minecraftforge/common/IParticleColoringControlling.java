package net.minecraftforge.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IParticleColorControlling 
{
    public boolean preventColoring(IBlockState blockState, BlockPos pos);
}
