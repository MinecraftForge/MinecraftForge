/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when an interaction between a {@link LivingEntity} and {@link MobEffectInstance} happens.
 * <p>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class MobEffectEvent extends LivingEvent
{
    @Nullable
    protected final MobEffectInstance effectInstance;

    public MobEffectEvent(LivingEntity living, MobEffectInstance effectInstance)
    {
        super(living);
        this.effectInstance = effectInstance;
    }

    @Nullable
    public MobEffectInstance getEffectInstance()
    {
        return effectInstance;
    }

    /**
     * This Event is fired when a {@link MobEffect} is about to get removed from an Entity.
     * This Event is {@link Cancelable}. If canceled, the effect will not be removed.
     * This Event does not have a result.
     */
    @Cancelable
    public static class Remove extends MobEffectEvent
    {
        private final MobEffect effect;

        public Remove(LivingEntity living, MobEffect effect)
        {
            super(living, living.getEffect(effect));
            this.effect = effect;
        }

        public Remove(LivingEntity living, MobEffectInstance effectInstance)
        {
            super(living, effectInstance);
            this.effect = effectInstance.getEffect();
        }

        /**
         * @return the {@link MobEffectEvent} which is being removed from the entity
         */
        public MobEffect getEffect()
        {
            return this.effect;
        }

        /**
         * @return the {@link MobEffectInstance}. In the remove event, this can be null if the entity does not have a {@link MobEffect} of the right type active.
         */
        @Override
        @Nullable
        public MobEffectInstance getEffectInstance()
        {
            return super.getEffectInstance();
        }
    }

    /**
     * This event is fired to check if a {@link MobEffectInstance} can be applied to an entity.
     * This event is not {@link Cancelable}.
     * This event {@link HasResult has a result}.
     * <p>
     * {@link Result#ALLOW ALLOW} will apply this mob effect.
     * {@link Result#DENY DENY} will not apply this mob effect.
     * {@link Result#DEFAULT DEFAULT} will run vanilla logic to determine if this mob effect is applicable in {@link LivingEntity#canBeAffected}.
     */
    @HasResult
    public static class Applicable extends MobEffectEvent
    {
        public Applicable(LivingEntity living, @NotNull MobEffectInstance effectInstance)
        {
            super(living, effectInstance);
        }

        @Override
        @NotNull
        public MobEffectInstance getEffectInstance()
        {
            return super.getEffectInstance();
        }
    }

    /**
     * This event is fired when a new {@link MobEffectInstance} is added to an entity.
     * This event is also fired if an entity already has the effect but with a different duration or amplifier.
     * This event is not {@link Cancelable}.
     * This event does not have a result.
     */
    public static class Added extends MobEffectEvent
    {
        private final MobEffectInstance oldEffectInstance;
        private final Entity source;

        public Added(LivingEntity living, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source)
        {
            super(living, newEffectInstance);
            this.oldEffectInstance = oldEffectInstance;
            this.source = source;
        }

        /**
         * @return the added {@link MobEffectInstance}. This is the unmerged MobEffectInstance if the old MobEffectInstance is not null.
         */
        @Override
        @NotNull
        public MobEffectInstance getEffectInstance()
        {
            return super.getEffectInstance();
        }

        /**
         * @return the old {@link MobEffectInstance}. This can be null if the entity did not have an effect of this kind before.
         */
        @Nullable
        public MobEffectInstance getOldEffectInstance()
        {
            return oldEffectInstance;
        }

        /**
         * @return the entity source of the effect, or {@code null} if none exists
         */
        @Nullable
        public Entity getEffectSource()
        {
            return source;
        }
    }

    /**
     * This event is fired when a {@link MobEffectInstance} expires on an entity.
     * This event is not {@link Cancelable}.
     * This event does not have a result.
     */
    public static class Expired extends MobEffectEvent
    {
        public Expired(LivingEntity living, MobEffectInstance effectInstance)
        {
            super(living, effectInstance);
        }
    }
}
