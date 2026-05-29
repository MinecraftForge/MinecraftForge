/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when an interaction between a {@link LivingEntity} and {@link MobEffectInstance} happens.
 * <p>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public sealed interface MobEffectEvent extends LivingEvent, InheritableEvent {
    EventBus<MobEffectEvent> BUS = EventBus.create(MobEffectEvent.class);

    @Nullable
    MobEffectInstance getEffectInstance();

    /**
     * This event is fired when a {@link MobEffect} is about to get removed from an Entity.
     * This event is {@linkplain Cancellable cancellable}. If cancelled, the effect will not be removed.
     */
    record Remove(
            LivingEntity getEntity,
            MobEffect getEffect,
            MobEffectInstance getEffectInstance
    ) implements Cancellable, MobEffectEvent {
        public static final CancellableEventBus<Remove> BUS = CancellableEventBus.create(Remove.class);

        public Remove(LivingEntity living, MobEffect effect) {
            this(living, effect, living.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)));
        }

        public Remove(LivingEntity living, MobEffectInstance effectInstance) {
            this(living, effectInstance.getEffect().get(), effectInstance);
        }

        /**
         * @return the {@link MobEffectEvent} which is being removed from the entity
         */
        public MobEffect getEffect() {
            return getEffect;
        }

        /**
         * @return the {@link MobEffectInstance}. In the remove event, this can be null if the entity does not have a {@link MobEffect} of the right type active.
         */
        @Override
        @Nullable
        public MobEffectInstance getEffectInstance() {
            return getEffectInstance;
        }
    }

    /**
     * This event is fired to check if a {@link MobEffectInstance} can be applied to an entity.
     * <p>This event {@link HasResult has a result}:</p>
     * <ul>
     *     <li>{@link Result#ALLOW ALLOW} will apply this mob effect.</li>
     *     <li>{@link Result#DENY DENY} will not apply this mob effect.</li>
     *     <li>{@link Result#DEFAULT DEFAULT} will run vanilla logic to determine if this mob effect is applicable in {@link LivingEntity#canBeAffected}.</li>
     * </ul>
     */
    final class Applicable implements MobEffectEvent, HasResult {
        public static final EventBus<Applicable> BUS = EventBus.create(Applicable.class);

        private final LivingEntity living;
        private final MobEffectInstance effectInstance;
        private Result result = Result.DEFAULT;

        public Applicable(LivingEntity living, @NotNull MobEffectInstance effectInstance) {
            this.living = living;
            this.effectInstance = effectInstance;
        }

        @Override
        public LivingEntity getEntity() {
            return living;
        }

        @Override
        @NotNull
        public MobEffectInstance getEffectInstance() {
            return effectInstance;
        }

        @Override
        public Result getResult() {
            return result;
        }

        @Override
        public void setResult(Result result) {
            this.result = result;
        }
    }

    /**
     * This event is fired when a new {@link MobEffectInstance} is added to an entity.<br>
     * This event is also fired if an entity already has the effect but with a different duration or amplifier.
     */
    record Added(
            LivingEntity getEntity,
            @Nullable MobEffectInstance getOldEffectInstance,
            @NotNull MobEffectInstance getEffectInstance,
            @Nullable Entity getEffectSource
    ) implements MobEffectEvent {
        public static final EventBus<Added> BUS = EventBus.create(Added.class);

        /**
         * @return the added {@link MobEffectInstance}. This is the unmerged MobEffectInstance if the old MobEffectInstance is not null.
         */
        @Override
        @NotNull
        public MobEffectInstance getEffectInstance() {
            return getEffectInstance;
        }

        /**
         * @return the old {@link MobEffectInstance}. This can be null if the entity did not have an effect of this kind before.
         */
        @Nullable
        public MobEffectInstance getOldEffectInstance() {
            return getOldEffectInstance;
        }

        /**
         * @return the entity source of the effect, or {@code null} if none exists
         */
        @Nullable
        public Entity getEffectSource() {
            return getEffectSource;
        }
    }

    /**
     * This event is fired when a {@link MobEffectInstance} expires on an entity.
     */
    record Expired(
            LivingEntity getEntity,
            MobEffectInstance getEffectInstance
    ) implements MobEffectEvent {
        public static final EventBus<Expired> BUS = EventBus.create(Expired.class);
    }
}
