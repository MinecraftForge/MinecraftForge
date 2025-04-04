/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingFallEvent is fired when an Entity is set to be falling.<br>
 * This event is fired whenever an Entity is set to fall in
 * {@link LivingEntity#causeFallDamage(double, float, DamageSource)}.<br>
 * <br>
 * This event is fired via the {@link net.minecraftforge.event.ForgeEventFactory#onLivingFall(LivingEntity, double, float)}.<br>
 * <br>
 * {@link #distance} contains the distance the Entity is to fall. If this event is canceled, this value is set to 0.0F.
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the Entity does not fall.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public final class LivingFallEvent extends LivingEvent {
    private double distance;
    private float damageMultiplier;

    public LivingFallEvent(LivingEntity entity, double distance, float damageMultiplier) {
        super(entity);
        this.distance = distance;
        this.damageMultiplier = damageMultiplier;
    }

    public double getDistance() { return this.distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public void setDamageMultiplier(float damageMultiplier) { this.damageMultiplier = damageMultiplier; }
}
