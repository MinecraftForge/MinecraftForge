package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

/**
 * <p>Fired when a vanilla MobSpawner spawns an Entity. Any custom NBT data on the Entity specified through the spawner's NBT data
 * (e.g. riding entities) will not have been set yet when this Event fires.</p>
 * <p>The result of this event has the following meaning:</p>
 * <ul>
 *  <li>{@code Result.DEFAULT} - Preserve vanilla mechanics ({@link EntityLiving#getCanSpawnHere()})</li>
 *  <li>{@code Result.ALLOW} - Override vanilla mechanics and allow the entity to spawn</li>
 *  <li>{@code Result.DENY} - Override vanilla mechanics and disallow the entity to spawn</li>
 * </ul>
 * <p>Note that setting the result to {@code ALLOW} also overrides the vanilla collision check for the entity, which can cause Entities to spawn inside Blocks.
 * Either do a check for that yourself or use the convenience method {@link #allowOnNoCollision()}.</p>
 *
 */
@HasResult
public class MobSpawnerSpawnEvent extends EntityEvent
{

    /**
     * <p>The living entity being spawned, if any. This field will be null if the Entity being spawned does not extend
     * {@code EntityLiving}. In that case use {@link EntityEvent#entity}.</p>
     */
    public final EntityLiving entityLiving;
    
    public MobSpawnerSpawnEvent(Entity entity, EntityLiving living)
    {
        super(entity);
        this.entityLiving = living;
    }
    
    /**
     * <p>Sets the result to {@code ALLOW}, but only if the Entity's bounding box is clear, otherwise sets the result to {@code DENY}.</p>
     */
    public void allowOnNoCollision()
    {
        World world = entity.worldObj;
        // snagged from EntityLiving#getCanSpawnHere
        if (world.checkNoEntityCollision(entity.boundingBox) && world.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() && !world.isAnyLiquid(entity.boundingBox))
        {
            setResult(Result.ALLOW);
        }
        else
        {
            setResult(Result.DENY);
        }
    }
    
}
