/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.World;

public interface IBonemealHandler
{
    /** Called when bonemeal is used on a block.  This is also called for
     * multiplayer servers, which must still return true if appropriate but
     * shouldn't actually perform world manipulation.
     * @return true to use up the bonemeal and stop processing.
     */
    public boolean onUseBonemeal(World world, int blockID, int X, int Y, int Z);
}

