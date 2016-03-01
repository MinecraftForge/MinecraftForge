package net.minecraftforge.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IPlantable
{
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos);
    public IBlockState getPlant(IBlockAccess world, BlockPos pos);
}