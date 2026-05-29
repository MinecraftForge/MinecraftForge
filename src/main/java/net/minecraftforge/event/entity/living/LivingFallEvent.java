/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * LivingFallEvent is fired when an Entity is set to be falling.<br>
 * This event is fired whenever an Entity is set to fall in
 * {@link LivingEntity#causeFallDamage(double, float, DamageSource)}.<br>
 * <br>
 * This event is fired via the {@link net.minecraftsingularity.event.singularityEventFactory#onLivingFall(LivingEntity, double, float)}.<br>
 * <br>
 * {@link #distance} contains the distance the Entity is to fall. If this event is cancelled, this value is set to 0.0F.
 * <br>
 * This event is {@linkplain Cancellable cancellable}. If this event is cancelled, the Entity does not fall.
 **/
public final class LivingFallEvent extends MutableEvent implements Cancellable, LivingEvent {
    public static final CancellableEventBus<LivingFallEvent> BUS = CancellableEventBus.create(LivingFallEvent.class);

    private final LivingEntity entity;
    private double distance;
    private float damageMultiplier;

    public LivingFallEvent(LivingEntity entity, double distance, float damageMultiplier) {
        this.entity = entity;
        this.distance = distance;
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    public double getDistance() { return this.distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public void setDamageMultiplier(float damageMultiplier) { this.damageMultiplier = damageMultiplier; }
}
