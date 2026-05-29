/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

import java.util.function.IntConsumer;

/**
 * Currently known conversions:
 * <ul>
 *     <li>Pig -> Zombie Piglin when struck by lightning</li>
 *     <li>Villager -> Zombie Villager when killed by a zombie</li>
 *     <li>Zombie -> Drowned when under water</li>
 *     <li>Husk -> Zombie when under water</li>
 *     <li>Zombie Villager -> Villager</li>
 *     <li>Hoglin -> Zoglin when in overworld</li>
 *     <li>Piglin/Piglin Brute -> Zombie Pigman when in overworld</li>
 *     <li>Villager -> Witch when struck by lightning</li>
 *     <li>Skeleton -> Stray when sitting in snow</li>
 *     <li>Tadpole -> Frog when it grows up</li>
 *     <li>Mushroom Cow -> Cow when sheared</li>
 * </ul>
 */
public sealed interface LivingConversionEvent extends LivingEvent, InheritableEvent {
    EventBus<LivingConversionEvent> BUS = EventBus.create(LivingConversionEvent.class);

    /**
     * LivingConversionEvent.Pre is triggered when an entity is trying
     * to replace itself with another entity
     * <br>
     * This event may trigger every tick even if it was cancelled last tick
     * for entities like Zombies and Hoglins. To prevent it, the conversion
     * timer needs to be changed or reset
     * <br>
     * This event is {@linkplain Cancellable}. If cancelled, the replacement will not occur
     */
    final class Pre implements Cancellable, LivingConversionEvent {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

        private final LivingEntity entity;
        private final EntityType<? extends LivingEntity> outcome;
        private final IntConsumer timer;

        public Pre(LivingEntity entity, EntityType<? extends LivingEntity> outcome, IntConsumer timer) {
            this.entity = entity;
            this.outcome = outcome;
            this.timer = timer;
        }

        @Override
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Gets the entity type of the new entity this living entity is
         * converting to
         * @return the entity type of the new entity
         */
        public EntityType<? extends LivingEntity> getOutcome() {
            return outcome;
        }

        /**
         * Sets the conversion timer, by changing this it prevents the
         * event being triggered every tick
         * Do note the timer of some of the entities are increments, but
         * some of them are decrements
         * Not every conversion is applicable for this
         * @param ticks timer ticks
         */
        public void setConversionTimer(int ticks) {
            timer.accept(ticks);
        }
    }

    /**
     * LivingConversionEvent.Post is triggered when an entity is replacing
     * itself with another entity.
     * The old living entity is likely to be removed right after this event.
     *
     * @param getOutcome Gets the finalized new entity (with all data like potion effect and equipments set)
     */
    record Post(LivingEntity getEntity, LivingEntity getOutcome) implements LivingConversionEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);
    }
}
