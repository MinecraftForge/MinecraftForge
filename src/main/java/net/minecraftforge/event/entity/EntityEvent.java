/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
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
     * CanUpdate is fired when an Entity is being created. <br>
     * This event is fired whenever vanilla Minecraft determines that an entity<br>
     * cannot update in {@link World#updateEntityWithOptionalForce(net.minecraft.entity.Entity, boolean)} <br>
     * <br>
     * {@link CanUpdate#canUpdate} contains the boolean value of whether this entity can update.<br>
     * If the modder decides that this Entity can be updated, they may change canUpdate to true, <br>
     * and the entity with then be updated.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class CanUpdate extends EntityEvent
    {
        private boolean canUpdate = false;
        public CanUpdate(Entity entity)
        {
            super(entity);
        }

        public boolean getCanUpdate()
        {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate)
        {
            this.canUpdate = canUpdate;
        }
    }

    /**
     * This event is fired whenever the {@link Pose} changes, and in a few other hardcoded scenarios.<br>
     * CAREFUL: This is also fired in the Entity constructor. Therefore the entity(subclass) might not be fully initialized. Check Entity#isAddedToWorld() or !Entity#firstUpdate.<br>
     * If you change the player's size, you probably want to set the eye height accordingly as well<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Size extends EntityEvent
    {
        private final Pose pose;
        private final EntityDimensions oldSize;
        private EntityDimensions newSize;
        private final float oldEyeHeight;
        private float newEyeHeight;

        public Size(Entity entity, Pose pose, EntityDimensions size, float defaultEyeHeight)
        {
            this(entity, pose, size, size, defaultEyeHeight, defaultEyeHeight);
        }

        public Size(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float oldEyeHeight, float newEyeHeight)
        {
            super(entity);
            this.pose = pose;
            this.oldSize = oldSize;
            this.newSize = newSize;
            this.oldEyeHeight = oldEyeHeight;
            this.newEyeHeight = newEyeHeight;
        }


        public Pose getPose() { return pose; }
        public EntityDimensions getOldSize() { return oldSize; }
        public EntityDimensions getNewSize() { return newSize; }
        public void setNewSize(EntityDimensions size)
        {
            setNewSize(size, false);
        }

        /**
         * Set the new size of the entity. Set updateEyeHeight to true to also update the eye height according to the new size.
         */
        public void setNewSize(EntityDimensions size, boolean updateEyeHeight)
        {
            this.newSize = size;
            if (updateEyeHeight)
            {
                this.newEyeHeight = this.getEntity().getEyeHeightAccess(this.getPose(), this.newSize);
            }
        }
        public float getOldEyeHeight() { return oldEyeHeight; }
        public float getNewEyeHeight() { return newEyeHeight; }
        public void setNewEyeHeight(float newHeight) { this.newEyeHeight = newHeight; }
    }
}
