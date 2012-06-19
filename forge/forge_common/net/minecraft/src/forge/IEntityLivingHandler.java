/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import java.util.ArrayList;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.DamageSource;
import net.minecraft.src.World;

public interface IEntityLivingHandler
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
     * @return True if the event was handled and no more processing should be done, false to continue processing
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
     * This is called immediately after an EntityLiving receives a new attack target from AI classes.
     * eg when a Zombie 'spots' a player. Handles friendly fire (setRevengeTarget) as well.
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
     * @return True if the event was handled and no more processing should be done, false to continue processing
     */
    public boolean onEntityLivingAttacked(EntityLiving entity, DamageSource attack, int damage);

    /**
     * This is called immediately after an EntityLiving started a jump
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
     * @return True if the event was handled and no more processing should be done, false to continue processing
     */
    public boolean onEntityLivingFall(EntityLiving entity, float distance);
    
    /**
     * This is called before EntityLiving's Base Update Tick.
     * Aborting this process will freeze both Movement and Actions.
     * 
     * @param entity Entity being ticked
     * @return True if the event was handled and no more processing should be done, false to continue processing
     */
    public boolean onEntityLivingUpdate(EntityLiving entity);

    /**
     * This is called whenever a EntityLiving is hurt, before any armor calculations are taken into effect.
     * Before any blocking, or potions are taken into account.
     * Returning 0 from this will stop all processing.
     * For the client's entity, this is only called in Single player, or if you are the server.
     * 
     * @param entity The entity being hurt
     * @param source The type of damage being dealt
     * @param damage The amount of damage being dealt
     * @return The amount of damage to let through. Returning 0 will prevent any further processing.
     */
    public int onEntityLivingHurt(EntityLiving entity, DamageSource source, int damage);

    /**
     * This is called after a EntityLiving die, and it spawns it's loot. The drop list should contain any item that the entity spawned at death.
     * May not work properly on all Mod entities if they do not use dropFewItems/dropRareDrop/entityDropItem
     * This will not contain the special record that creepers drop when they are killed by a Skeleton, or the apple notch drops.
     * Or the player's inventory.
     * 
     * If you need to deal with the Player's inventory, do so in onEntityLivingDeath
     * In most cases, drops will be empty if the entity was a baby that hadn't reached full size yet.
     * 
     * @param entity The entity that is droping the items
     * @param source The damage source that caused the entities death
     * @param drops An ArrayList containing all items to drop, AYou must make sure to not cause any concurancy exceptions with this
     * @param lootingLevel The Looting enchantment level if the attacker was a player and they had the enchantment, else 0
     * @param recentlyHit Signifying if the entity was recently hit by a player.
     * @param specialDropValue Random number used to determine if the 'special' loot should be dropped. 0 if the entity was a child
     */
    public void onEntityLivingDrops(EntityLiving entity, DamageSource source, ArrayList<EntityItem> drops, int lootingLevel, boolean recentlyHit, int specialDropValue);
}