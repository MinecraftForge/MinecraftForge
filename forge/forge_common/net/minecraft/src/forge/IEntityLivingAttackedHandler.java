/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.DamageSource;

public interface IEntityLivingAttackedHandler
{
    /** 
     * This is called when any EntityLiving takes damage from any DamageSource.
     * In multiplayer, this is called by both the client and the server.
     * 
     * @param entity The entity being attacked
     * @param attack DamageSource instance of the attack, contains attacking entity if exists
	 * @param damage Unmitigated damage the attack would cause
     * @return True to continue processing, false to cancel.
     */
    public boolean onEntityLivingAttacked(EntityLiving entity, DamageSource attack, int damage);
}