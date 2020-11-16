package net.minecraftforge.common.extensions;

import net.minecraft.block.BlockState;

public interface IRedstoneWireBlock
{
    /**
     * Return true to allow connection with the other wire.
     * The default implementation makes redstone wires only connect to themselves.
     * Always return true to allow all redstone wires to connect to this wire.
     */
    default boolean canConnectToOther(BlockState thisState, BlockState otherState)
    {
        return otherState.isIn(thisState.getBlock());
    }
}
