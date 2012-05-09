/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;

/** 
 * Provides hooks for adding to or completely replacing the code run
 * when an EntityLiving dies.
 * @author CowedOffACliff */
public interface IEntityDeathHandler 
{
	
	/** This method is executed when an EntityLiving dies. It allows the user to implement
	 * additional code to be run, along with the vanilla, inside EntityLiving.onDeath(DamageSource).
	 * If you do not need to use this method in your implementation, simply leave it blank.
	 * 
	 * @param entity The instance of the EntityLiving that has died
	 * @param damagesource The damagesource by which the EntityLiving was killed */
	public void onEntityDeath(EntityLiving entity, DamageSource damagesource);
	
	/** This method is executed when an EntityLiving dies. It allows the user to completely override
	 * code within the EntityLiving.onDeath(DamageSource) method. 
	 * If this returns true, the following vanilla code will not be run.
	 * If you do not need to use this method in your implementation, simply leave it empty and have it return false.
	 * Note1: this method takes precedence over (is run before) IEntityDeathHandler.onEntityDeath(EntityLiving, DamageSource).
	 * Note2: please be aware that any code in this method that runs prior it to returning will execute regardless of return type
	 * 
	 * @param entity The instance of the EntityLiving that has died
	 * @param damagesource The damagesource by which the EntityLiving was killed */
	public boolean onOverrideEntityDeath(EntityLiving entity, DamageSource damagesource);
}
