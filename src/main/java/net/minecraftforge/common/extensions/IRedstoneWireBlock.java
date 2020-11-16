package net.minecraftforge.common.extensions;

import net.minecraft.block.BlockState;

public interface IRedstoneWireBlock
{
    /**
     * Return true to allow connection with the other wire.
     * Always return true to allow all redstone wires to connect to this wire.
     * Always return false to not allow any wires to connect to this wire, even itself.
     */
    default boolean canConnectToOther(BlockState thisState, BlockState otherState)
    {
        return true;
    }
}
