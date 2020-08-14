/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * EntityEvent is fired when an event involving any Entity occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} contains the entity that caused this event to occur.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class EntityEvent extends Event
{
    private final Entity entity;

    public EntityEvent(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return entity;
    }

    /**
     * EntityConstructing is fired when an Entity is being created. <br>
     * This event is fired within the constructor of the Entity.<br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class EntityConstructing extends EntityEvent
    {
        public EntityConstructing(Entity entity)
        {
            super(entity);
        }
    }
    
    /**
     * EnteringChunk is fired when an Entity enters a chunk. <br>
     * This event is fired whenever vanilla Minecraft determines that an entity <br>
     * is entering a chunk in {@link Chunk#addEntity(net.minecraft.entity.Entity)} <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class EnteringChunk extends EntityEvent
    {
        private final int newChunkX;
        private final int newChunkZ;
        private final int oldChunkX;
        private final int oldChunkZ;

        public EnteringChunk(Entity entity, int newChunkX, int newChunkZ, int oldChunkX, int oldChunkZ)
        {
            super(entity);
            this.newChunkX = newChunkX;
            this.newChunkZ = newChunkZ;
            this.oldChunkX = oldChunkX;
            this.oldChunkZ = oldChunkZ;
        }

        public int getNewChunkX() { return newChunkX; }
        public int getNewChunkZ() { return newChunkZ; }
        public int getOldChunkX() { return oldChunkX; }
        public int getOldChunkZ() { return oldChunkZ; }
    }
    
    /**
     * EyeHeight is fired when an Entity's eye height changes. <br>
     * This event is fired whenever the {@link Pose} changes, and in a few other hardcoded scenarios.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class EyeHeight extends EntityEvent
    {
        private final Pose pose;
        private final EntitySize size;
        private final float oldHeight;
        private float newHeight;
     
        public EyeHeight(Entity entity, Pose pose, EntitySize size, float defaultHeight)
        {
            super(entity);
            this.pose = pose;
            this.size = size;
            this.oldHeight = defaultHeight;
            this.newHeight = defaultHeight;
        }
        
        public Pose getPose() { return pose; }
        public EntitySize getSize() { return size; }
        public float getOldHeight() { return oldHeight; }
        public float getNewHeight() { return newHeight; }
        public void setNewHeight(float newSize) { this.newHeight = newSize; }
    }
}
