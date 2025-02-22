/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MarkerEvent;

/**
 * EntityEvent is fired when an event involving any Entity occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} contains the entity that caused this event to occur.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@MarkerEvent
public sealed interface EntityEvent extends InheritableEvent
        permits EntityEvent.EnteringSection, EntityEvent.EntityConstructing, EntityJoinLevelEvent,
                EntityLeaveLevelEvent, EntityMobGriefingEvent, EntityMountEvent, EntityStruckByLightningEvent,
                EntityTeleportEvent, EntityTravelToDimensionEvent, ProjectileImpactEvent {
    Entity entity();

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
    record EntityConstructing(Entity entity) implements EntityEvent {
        public static final EventBus<EntityConstructing> BUS = EventBus.create(EntityConstructing.class);
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
    record EnteringSection(Entity entity, long packedOldPos, long packedNewPos) implements EntityEvent {
        public static final EventBus<EnteringSection> BUS = EventBus.create(EnteringSection.class);

        /**
         * A packed version of the old section's position. This is to be used with the various methods in {@link SectionPos},
         * such as {@link SectionPos#of(long)} or {@link SectionPos#x(long)} to avoid allocation.
         * @return the packed position of the old section
         */
        public long getPackedOldPos() {
            return packedOldPos;
        }

        /**
         * A packed version of the new section's position. This is to be used with the various methods in {@link SectionPos},
         * such as {@link SectionPos#of(long)} or {@link SectionPos#x(long)} to avoid allocation.
         * @return the packed position of the new section
         */
        public long getPackedNewPos() {
            return packedNewPos;
        }

        /**
         * @return the position of the old section
         */
        public SectionPos getOldPos() {
            return SectionPos.of(packedOldPos);
        }

        /**
         * @return the position of the new section
         */
        public SectionPos getNewPos() {
            return SectionPos.of(packedNewPos);
        }

        /**
         * Whether the chunk has changed as part of this event. If this method returns false, only the Y position of the
         * section has changed.
         */
        public boolean didChunkChange() {
            return SectionPos.x(packedOldPos) != SectionPos.x(packedNewPos) || SectionPos.z(packedOldPos) != SectionPos.z(packedNewPos);
        }
    }
}
