/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.DamageSource;
import net.minecraft.src.World;

public interface IEntityLivingEventsHandler
{
    /**
     * Raised when an EntityLiving is spawned into the world from natural means, meaning 
     * not by command, MobSpawner, cheat, etc.. Just naturally throughout the world.
     * 
     * This allows the mod to create special functionality that runs on a mob natural 
     * spawn. The Vanilla minecraft mechanic of having 'Spider Jockies', the color of 
     * sheep's wool, and Ocelot's spawning with babies can be canceled by returning 
     * true from this function
     * 
     * Returning true will indicate that you have performed your special spawning, 
     * and no more handling will be done. 
     * 
     * @param entity The newly spawned entity
     * @param world The world the entity is in
     * @param x The Entitie's X Position
     * @param y The Entitie's Y Position
     * @param z The Entitie's Z Position
     * @return True to prevent any further special case handling from executing.
     */
    public boolean onEntityLivingSpawn(EntityLiving entity, World world, float x, float y, float z);
    
    /** 
     * This is called when any EntityLiving's health reaches 0.
     * You can cancel the death here, but you must raise the health or it will die again.
     * In multiplayer, this is called by both the client and the server.
     * 
     * @param entity Entity about to die
     * @param killer DamageSource instance causing the death
     * @return True to continue processing, false to cancel.
     */
    public boolean onEntityLivingDeath(EntityLiving entity, DamageSource killer);
    
    /** 
     * This is called immediatly after an EntityLiving receives a new attack target from AI classes.
     * eg when a Zombie 'spots' a player. Handles friendly fire (setRevengeTarget) aswell.
     * In multiplayer, this is called by the server only.
     * 
     * @param entity Entity attacking
     * @param target Entity designated as new target
     */
    public void onEntityLivingSetAttackTarget(EntityLiving entity, EntityLiving target);
    
    /** 
     * This is called when any EntityLiving takes damage from any DamageSource.
     * In multiplayer, this is called by both the client and the server.
     * 
     * @param entity Entity being attacked
     * @param attack DamageSource instance of the attack
     * @param damage Unmitigated damage the attack would cause
     * @return True to continue processing, false to cancel.
     */
    public boolean onEntityLivingAttacked(EntityLiving entity, DamageSource attack, int damage);

    /**
     * This is called immediatly after an EntityLiving started a jump
     * Velocity will already be set and can be modified.
     * 
     * @param entity Entity starting the jump
     */
    public void onEntityLivingJump(EntityLiving entity);

    /**
     * This is called when an EntityLiving reconnects with the ground.
     * Aborting this would stop both damage and the landing sound.
     * 
     * @param entity Entity which fell
     * @param distance absolute height between the last position touching the ground and the current.
     * @return True to continue processing, false to cancel.
     */
    public boolean onEntityLivingFall(EntityLiving entity, float distance);
    
    /**
     * This is called before EntityLiving's Base Update Tick.
     * Aborting this process will freeze both Movement and Actions.
     * 
     * @param entity Entity being ticked
     * @return True to continue processing, false to cancel.
     */
    public boolean onEntityLivingUpdate(EntityLiving entity);
}