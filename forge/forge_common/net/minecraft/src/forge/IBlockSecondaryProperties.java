/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.World;

/**
 * This interface provides access to a number of secondary block properties through providing  
 * overrides to methods within world.class.
 *
 * This interface is DEPRECATED.  The functions are now provided directly by
 * Block.
 */

public interface IBlockSecondaryProperties 
{
	/**
	 * Return true if this block should be treated as a normal cube by game logic 
	 * (this is independant of rendering), false otherwise.  This allows for things like secondary
	 * blocks (torches, rails, redstone dust, etc.) being attached to the surface of
	 * the block regardless of whether it is opaque, or posesses other rendering properties that
	 * would not normally make this possible.
	 */
	public boolean isBlockNormalCube( World world, int i, int j, int k );
	
	/**
	 * Return true if this block can be replaced by another by the player, false otherwise.  This provides
	 * a means of allowing blocks to not be considered when the player attempts to place another within
	 * their bounds, in the same way that water, lava, fire, and some other blocks behave within the game.
	 * Provides an override to the coresponding section in canBlockBePlacedAt() in world.class
	 */
	public boolean isBlockReplaceable( World world, int i, int j, int k );
	
	/**
	 * Return true if this block should set fire and deal fire damage to entities coming into contact with
	 * it, false otherwise.  Provides an override to the related test within isBoundingBoxBurning() within
	 * world.class
	 */
	public boolean isBlockBurning( World world, int i, int j, int k );
	
	/**
	 * Return true if this block should be treated as an air block by the rest of the code, false
	 * otherwise.  This method is primarily useful for creating pure logic-blocks that will be invisible 
	 * to the player and otherwise interact as air would.  
	 * Provides an override to isAirBlock() within world.class
	 */	
	public boolean isAirBlock( World world, int i, int j, int k );
	
}


