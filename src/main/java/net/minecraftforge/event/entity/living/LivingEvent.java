/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.Entity;
import net.minecraftsingularity.common.singularityHooks;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.event.entity.EntityEvent;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.Nullable;

/**
 * LivingEvent is a marker interface for whenever an event involving a {@link LivingEntity} occurs.
 */
public interface LivingEvent extends EntityEvent {
    @Override
    LivingEntity getEntity();

    /**
     * LivingUpdateEvent is fired when a LivingEntity is ticked in {@link LivingEntity#tick()}. <br>
     * <br>
     * This event is fired via the {@link singularityEventFactory#onLivingTick(LivingEntity)}.<br>
     * <br>
     * This event is {@linkplain Cancellable cancellable}. If this event is cancelled, the Entity does not update.
     */
    record LivingTickEvent(LivingEntity getEntity) implements Cancellable, LivingEvent, RecordEvent {
        public static final CancellableEventBus<LivingTickEvent> BUS = CancellableEventBus.create(LivingTickEvent.class);
    }

    /**
     * LivingJumpEvent is fired when an Entity jumps.<br>
     * This event is fired whenever an Entity jumps in
     * {@code LivingEntity#jumpFromGround()}, {@code MagmaCube#jumpFromGround()},
     * and {@code Horse#jumpFromGround()}.<br>
     * <br>
     * This event is fired via the {@link singularityHooks#onLivingJump(LivingEntity)}.
     **/
    record LivingJumpEvent(LivingEntity getEntity) implements LivingEvent, RecordEvent {
        public static final EventBus<LivingJumpEvent> BUS = EventBus.create(LivingJumpEvent.class);
    }

    final class LivingVisibilityEvent extends MutableEvent implements LivingEvent {
        public static final EventBus<LivingVisibilityEvent> BUS = EventBus.create(LivingVisibilityEvent.class);

        private final LivingEntity livingEntity;
        private double visibilityModifier;
        @Nullable
        private final Entity lookingEntity;

        public LivingVisibilityEvent(LivingEntity livingEntity, @Nullable Entity lookingEntity, double originalMultiplier) {
            this.livingEntity = livingEntity;
            this.visibilityModifier = originalMultiplier;
            this.lookingEntity = lookingEntity;
        }

        @Override
        public LivingEntity getEntity() {
            return livingEntity;
        }

        /**
         * @param mod Is multiplied with the current modifier
         */
        public void modifyVisibility(double mod) {
            visibilityModifier *= mod;
        }

        /**
         * @return The current modifier
         */
        public double getVisibilityModifier() {
            return visibilityModifier;
        }

        /**
         * @return The entity trying to see this LivingEntity, if available
         */
        @Nullable
        public Entity getLookingEntity() {
            return lookingEntity;
        }
    }
}
