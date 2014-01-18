package net.minecraftforge.common;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public interface IPlantable
{
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z);
    public Block getPlant(IBlockAccess world, int x, int y, int z);
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z);
}