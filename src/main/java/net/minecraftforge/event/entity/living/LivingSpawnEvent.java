/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.living;

import javax.annotation.Nullable;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

/**
 * LivingSpawnEvent is fired for any events associated with Living Enttnies spawn status. <br>
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
    private final World world;
    private final float x;
    private final float y;
    private final float z;

    public LivingSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() { return world; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
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
        private final boolean isSpawner; // TODO: remove in 1.13
        @Nullable
        private final MobSpawnerBaseLogic spawner;


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
        public CheckSpawn(EntityLiving entity, World world, float x, float y, float z, @Nullable MobSpawnerBaseLogic spawner)
        {
            super(entity, world, x, y, z);
            this.isSpawner = spawner != null;
            this.spawner = spawner;
        }

        /**
         * @deprecated Use {@link CheckSpawn##CheckSpawn(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)}
         *   with a spawner instance, or null if not a spawner
         * CheckSpawn is fired when an Entity is about to be spawned.
         * @param entity the spawning entity
         * @param world the world to spawn in
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         * @param isSpawner true if this spawn is done by a MobSpawner,
         *                  false if it this spawn is coming from a WorldSpawner
         */
        @Deprecated // TODO: Remove in 1.13
        public CheckSpawn(EntityLiving entity, World world, float x, float y, float z, boolean isSpawner)
        {
            super(entity, world, x, y, z);
            this.isSpawner = isSpawner;
            spawner = null;
        }

        /**
         * @deprecated Use {@link CheckSpawn#CheckSpawn(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)} instead
         */
        @Deprecated // TODO: Remove in 1.13
        public CheckSpawn(EntityLiving entity, World world, float x, float y, float z)
        {
            this(entity, world, x, y, z, true);
        }

        public boolean isSpawner()
        {
            return isSpawner; // TODO: replace with spawner null check in 1.13
        }

        @Nullable
        public MobSpawnerBaseLogic getSpawner()
        {
            return spawner;
        }
    }

    /**
     * SpecialSpawn is fired when an Entity is to be spawned.<br>
     * This allows you to do special inializers in the new entity.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#doSpecialSpawn(EntityLiving, World, float, float, float)}.<br>
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
        @Nullable
        private final MobSpawnerBaseLogic spawner;

        /**
         * @deprecated Use {@link SpecialSpawn#SpecialSpawn(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)}
         *   with originating spawner instance or null
         */
        @Deprecated // TODO: remove in 1.13
        public SpecialSpawn(EntityLiving entity, World world, float x, float y, float z)
        {
            super(entity, world, x, y, z);
            spawner = null;
        }

        /**
         * @param spawner the position of a tileentity or approximate position of an entity that initiated the spawn if any
         */
        public SpecialSpawn(EntityLiving entity, World world, float x, float y, float z, @Nullable MobSpawnerBaseLogic spawner)
        {
            super(entity, world, x, y, z);
            this.spawner = spawner;
        }

        @Nullable
        public MobSpawnerBaseLogic getSpawner()
        {
            return spawner;
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
            super(entity, entity.world, (float)entity.posX, (float)entity.posY, (float)entity.posZ);
        }

    }
}