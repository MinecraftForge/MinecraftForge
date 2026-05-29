/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.common.singularityHooks;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * LivingHurtEvent is fired when an Entity is set to be hurt. <br>
 * This event is fired whenever an Entity is hurt in
 * {@code LivingEntity#actuallyHurt(DamageSource, float)} and
 * {@code Player#actuallyHurt(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link singularityHooks#onLivingHurt(LivingEntity, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the amount of damage dealt to the Entity that was hurt. <br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}. If this event is cancelled, the Entity is not hurt.<br>
 *
 * @see LivingDamageEvent
 **/
public final class LivingHurtEvent extends MutableEvent implements Cancellable, LivingEvent {
    public static final CancellableEventBus<LivingHurtEvent> BUS = CancellableEventBus.create(LivingHurtEvent.class);

    private final LivingEntity entity;
    private final DamageSource source;
    private float amount;

    public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    public DamageSource getSource() { return source; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}
