package net.minecraft.dispenser;

import net.minecraft.world.World;

public interface ILocation extends IPosition
{
    World getWorld();
}