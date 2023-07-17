/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeEntity;
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
     * This event is fired on server and client after an Entity has entered a different section. <br>
     * Sections are 16x16x16 block grids of the world.<br>
     * This event does not fire when a new entity is spawned, only when an entity moves from one section to another one.
     * Use {@link EntityJoinLevelEvent} to detect new entities joining the world.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class EnteringSection extends EntityEvent
    {

        private final long packedOldPos;
        private final long packedNewPos;

        public EnteringSection(Entity entity, long packedOldPos, long packedNewPos)
        {
            super(entity);
            this.packedOldPos = packedOldPos;
            this.packedNewPos = packedNewPos;
        }

        /**
         * A packed version of the old section's position. This is to be used with the various methods in {@link SectionPos},
         * such as {@link SectionPos#of(long)} or {@link SectionPos#x(long)} to avoid allocation.
         * @return the packed position of the old section
         */
        public long getPackedOldPos()
        {
            return packedOldPos;
        }

        /**
         * A packed version of the new section's position. This is to be used with the various methods in {@link SectionPos},
         * such as {@link SectionPos#of(long)} or {@link SectionPos#x(long)} to avoid allocation.
         * @return the packed position of the new section
         */
        public long getPackedNewPos()
        {
            return packedNewPos;
        }

        /**
         * @return the position of the old section
         */
        public SectionPos getOldPos()
        {
            return SectionPos.of(packedOldPos);
        }

        /**
         * @return the position of the new section
         */
        public SectionPos getNewPos()
        {
            return SectionPos.of(packedNewPos);
        }

        /**
         * Whether the chunk has changed as part of this event. If this method returns false, only the Y position of the
         * section has changed.
         */
        public boolean didChunkChange()
        {
            return SectionPos.x(packedOldPos) != SectionPos.x(packedNewPos) || SectionPos.z(packedOldPos) != SectionPos.z(packedNewPos);
        }

    }

    /**
     * This event is fired whenever {@link Entity#getDimensionsForge(Pose)} gets called.<br>
     * CAREFUL: This is also fired in the Entity constructor. Therefore, the entity (subclass) might not be fully initialized. Check {@link Entity#isAddedToWorld()} or {@code !Entity.firstUpdate}.<br>
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
        private final EntityDimensions originalSize;
        private EntityDimensions newSize;
        private final float oldEyeHeight;
        private float newEyeHeight;

        public Size(Entity entity, Pose pose, EntityDimensions size)
        {
            this(entity, pose, size, 1.0f); // Eye height doesn't matter this is just for binary compatibility.
        }

        @Deprecated(forRemoval = true, since = "1.20.1")
        public Size(Entity entity, Pose pose, EntityDimensions size, float defaultEyeHeight)
        {
            this(entity, pose, size, size, defaultEyeHeight, defaultEyeHeight);
        }

        @Deprecated(forRemoval = true, since = "1.20.1")
        public Size(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float oldEyeHeight, float newEyeHeight)
        {
            super(entity);
            this.pose = pose;
            this.originalSize = oldSize;
            this.newSize = newSize;
            this.oldEyeHeight = oldEyeHeight;
            this.newEyeHeight = newEyeHeight;
        }

        public Pose getPose() { return pose; }
        /** @deprecated Use {@link #getOriginalSize()} */
        @Deprecated(forRemoval = true, since = "1.20.1")
        public EntityDimensions getOldSize() { return this.getOriginalSize(); }
        public EntityDimensions getOriginalSize() { return originalSize; }
        public EntityDimensions getNewSize() { return newSize; }
        public void setNewSize(EntityDimensions size) { this.newSize = size; }

        /** @deprecated Use {@link EyeHeight} to hook into changes to eye height. Updating the eye height will not actually so anything anymore. */
        @Deprecated(forRemoval = true)
        public void setNewSize(EntityDimensions size, boolean updateEyeHeight)
        {
            this.setNewSize(size);
            if (updateEyeHeight)
            {
                this.newEyeHeight = this.getEntity().getEyeHeightAccess(this.getPose(), this.newSize);
            }
        }
        /** @deprecated Use {@link EyeHeight} to hook into changes to eye height. */
        @Deprecated(forRemoval = true, since = "1.20.1")
        public float getOldEyeHeight() { return oldEyeHeight; }
        /** @deprecated Use {@link EyeHeight} to hook into changes to eye height. */
        @Deprecated(forRemoval = true, since = "1.20.1")
        public void setNewEyeHeight(float eyeHeight) { this.newEyeHeight = eyeHeight; }
    }

    /**
     * This event is fired whenever {@link IForgeEntity#getEyeHeightForge(Pose, EntityDimensions)} gets called.<br>
     * CAREFUL: This is also fired in the Entity constructor. Therefore, the entity (subclass) might not be fully initialized. Check {@link Entity#isAddedToWorld()} or {@code !Entity.firstUpdate}.<br>
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
        private final EntityDimensions size;
        private final float originalEyeHeight;
        private float newEyeHeight;

        public EyeHeight(Entity entity, Pose pose, EntityDimensions size, float eyeHeight)
        {
            super(entity);
            this.pose = pose;
            this.size = size;
            this.originalEyeHeight = eyeHeight;
            this.newEyeHeight = eyeHeight;
        }

        public Pose getPose() { return pose; }
        public EntityDimensions getSize() { return size; }
        public float getOriginalEyeHeight() { return originalEyeHeight; }
        public float getNewEyeHeight() { return newEyeHeight; }
        public void setNewEyeHeight(float newEyeHeight) { this.newEyeHeight = newEyeHeight; }
    }
}
