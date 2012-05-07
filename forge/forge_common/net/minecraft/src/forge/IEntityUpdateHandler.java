/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Entity;

/** 
 * Provides hooks for adding extra code to the beginning and end
 * of Entity.onEntityUpdate(), which is the method that updates entities
 * each tick.
 * @author CowedOffACliff */
public interface IEntityUpdateHandler 
{
	/** This method is a hook into the beginning of the Entity.onEntityUpdate() method.
	 * Implementing this method allows you to run additional code at the start of the
	 * updating process for all Entities within the game, which effectively means you can modify the
	 * updating process of any Entity without editing base classes.
	 * If you do not need to use this method in your implementation, simply leave it blank. 
	 * 
	 * @param entity The entity being updated*/
	public void onEntityUpdateStart(Entity entity);
	
	/** This method is a hook into the end of the Entity.onEntityUpdate() method.
	 * Implementing this method allows you to run additional code at the end of the
	 * updating process for all Entities within the game, which effectively means you can modify the
	 * updating process of any Entity without editing base classes.
	 * If you do not need to use this method in your implementation, simply leave it blank. 
	 * 
	 * @param entity The entity being updated*/
	public void onEntityUpdateEnd(Entity entity);
}
