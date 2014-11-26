package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

/**
 * LivingSpawnEvent is fired whenever a living Entity is spawned. <br>
 * If a method utilizes this {@link Event} as its parameter, the method will 
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} contains the world in which this living Entity is being spawned.<br>
 * {@link #x} contains the x-coordinate this entity is being spawned at.<br>
 * {@link #y} contains the y-coordinate this entity is being spawned at.<br>
 * {@link #z} contains the z-coordinate this entity is being spawned at.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingSpawnEvent extends LivingEvent
{
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    
    public LivingSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Fires before mob spawn events.
     * 
     * Result is significant:
     *    DEFAULT: use vanilla spawn rules
     *    ALLOW:   allow the spawn
     *    DENY:    deny the spawn
     *
     */
    @HasResult
    public static class CheckSpawn extends LivingSpawnEvent
    {
        public CheckSpawn(EntityLiving entity, World world, float x, float y, float z)
        {
            super(entity, world, x, y, z);
        }
    }

    /**
     * SpecialSpawn is fired when an Entity is to be spawned from a mob spawner.<br>
     * This event is fired whenever an Entity is spawned in a mob spawner in<br>
     * SpawnerAnimals#findChunksForSpawning(WorldServer, boolean, boolean, boolean).<br>
     * <br>
     * This event is fired via the {@link ForgeHooks#doSpecialSpawn(EntityLiving, World, float, float, float)}.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the Entity is not spawned.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class SpecialSpawn extends LivingSpawnEvent
    {
        public SpecialSpawn(EntityLiving entity, World world, float x, float y, float z)
        {
            super(entity, world, x, y, z);
        }
    }
    
    /**
     * Fired each tick for despawnable mobs to allow control over despawning.
     * {@link Result#DEFAULT} will pass the mob on to vanilla despawn mechanics.
     * {@link Result#ALLOW} will force the mob to despawn.
     * {@link Result#DENY} will force the mob to remain.
     * This is fired every tick for every despawnable entity. Be efficient in your handlers.
     * 
     * Note: this is not fired <em>if</em> the mob is definitely going to otherwise despawn. It is fired to check if
     * the mob can be allowed to despawn. See {@link EntityLiving#despawnEntity}
     * 
     * @author cpw
     *
     */
    @HasResult
    public static class AllowDespawn extends LivingSpawnEvent
    {
        public AllowDespawn(EntityLiving entity)
        {
            super(entity, entity.worldObj, (float)entity.posX, (float)entity.posY, (float)entity.posZ);
        }
        
    }
}