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
public interface ISpecialResistance {
    
    /**
     * Return the explosion resistance of the block located at position i, j, 
     * k, from an exploder explosing on src_x, src_y, src_z.
     */
	public float getSpecialExplosionResistance(World world, int i, int j, int k,
		double src_x, double src_y, double src_z, Entity exploder);
}
