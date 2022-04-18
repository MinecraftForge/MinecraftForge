/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * LivingSpawnEvent is fired for any events associated with Living Entities spawn status. <br>
 * If a method utilizes this event as its parameter, the method will
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
    private final LevelAccessor world;
    private final double x;
    private final double y;
    private final double z;

    public LivingSpawnEvent(Mob entity, LevelAccessor world, double x, double y, double z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LevelAccessor getWorld() { return world; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
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
        @Nullable
        private final BaseSpawner spawner;
        private final MobSpawnType spawnReason;

        /**
         * CheckSpawn is fired when an Entity is about to be spawned.
         * @param entity the spawning entity
         * @param world the world to spawn in
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         * @param spawner position of the MobSpawner
         *                  null if it this spawn is coming from a WorldSpawner
         */
        public CheckSpawn(Mob entity, LevelAccessor world, double x, double y, double z, @Nullable BaseSpawner spawner, MobSpawnType spawnReason)
        {
            super(entity, world, x, y, z);
            this.spawner = spawner;
            this.spawnReason = spawnReason;
        }

        public boolean isSpawner()
        {
            return spawner != null;
        }

        @Nullable
        public BaseSpawner getSpawner()
        {
            return spawner;
        }

        public MobSpawnType getSpawnReason()
        {
            return spawnReason;
        }
    }

    /**
     * SpecialSpawn is fired when an Entity is to be spawned.<br>
     * This allows you to do special inializers in the new entity.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#doSpecialSpawn(Mob, LevelAccessor, float, float, float, BaseSpawner, MobSpawnType)}.<br>
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If this event is canceled, the Entity is not spawned.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @net.minecraftforge.eventbus.api.Cancelable
    public static class SpecialSpawn extends LivingSpawnEvent
    {
        @Nullable
        private final BaseSpawner spawner;
        private final MobSpawnType spawnReason;

        /**
         * @param spawner the position of a tileentity or approximate position of an entity that initiated the spawn if any
         */
        @Deprecated(forRemoval = true, since = "1.18.1")
        public SpecialSpawn(Mob entity, Level world, double x, double y, double z, @Nullable BaseSpawner spawner, MobSpawnType spawnReason)
        {
            this(entity, (LevelAccessor)world, x, y, z, spawner, spawnReason);
        }

        public SpecialSpawn(Mob entity, LevelAccessor world, double x, double y, double z, @Nullable BaseSpawner spawner, MobSpawnType spawnReason)
        {
            super(entity, world, x, y, z);
            this.spawner = spawner;
            this.spawnReason = spawnReason;
        }

        @Nullable
        public BaseSpawner getSpawner()
        {
            return spawner;
        }

        public MobSpawnType getSpawnReason()
        {
            return spawnReason;
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
     * the mob can be allowed to despawn. See {@link LivingEntity#checkDespawn()}
     *
     * @author cpw
     *
     */
    @HasResult
    public static class AllowDespawn extends LivingSpawnEvent
    {
        public AllowDespawn(Mob entity)
        {
            super(entity, entity.level, entity.getX(), entity.getY(), entity.getZ());
        }

    }
}
