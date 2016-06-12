package net.minecraftforge.common.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Representation of the {@link World} and {@link BlockPos} of an object.
 */
public interface IWorldLocation
{
    
    public World getWorldIn();

    public BlockPos getPosIn();

}