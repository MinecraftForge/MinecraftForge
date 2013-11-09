package net.minecraftforge.common;

import net.minecraft.world.World;

public interface IPlantable
{
    public EnumPlantType getPlantType(World world, int x, int y, int z);
    public int getPlantID(World world, int x, int y, int z);
    public int getPlantMetadata(World world, int x, int y, int z);
}