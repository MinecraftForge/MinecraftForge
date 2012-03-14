/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;

/**
 * This interface is to be implemented by Block classes. It will override
 * standard algorithms controlling connection between two blocks by redstone
 *
 * @see Block
 */
public interface IConnectRedstone
{
    /**
     * When this returns false, the block at location i, j, k cannot make
     * a redstone connection in the direction given in parameter, otherwise
     * it can.  Use to control which sides are inputs and outputs for redstone
     * wires.
     */
    public boolean canConnectRedstone(IBlockAccess world, int X, int Y, int Z, int direction);
}
