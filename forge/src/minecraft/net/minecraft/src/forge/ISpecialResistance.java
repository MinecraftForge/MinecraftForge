/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.World;

/**
 * This interface is to be implemented by block classes. It will allow a block
 * to control how it resists to explosion
 *
 * @see Block
 */
public interface ISpecialResistance
{

    /**
     * Return the explosion resistance of the block located at position X, Y,
     * Z, from an exploder explosing on srcX, srcY, srcZ.
     */
    public float getSpecialExplosionResistance(World world, int X, int Y, int Z, double srcX, double srcY, double srcZ, Entity exploder);
}
