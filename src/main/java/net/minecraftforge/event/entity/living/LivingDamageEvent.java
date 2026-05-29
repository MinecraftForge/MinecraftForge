/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraftsingularity.common.singularityHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * LivingDamageEvent is fired just before damage is applied to entity.<br>
 * At this point armor, potion and absorption modifiers have already been applied to damage - this is FINAL value.<br>
 * Also note that appropriate resources (like armor durability and absorption extra hearths) have already been consumed.<br>
 * This event is fired whenever an Entity is damaged in
 * {@code LivingEntity#actuallyHurt(DamageSource, float)} and
 * {@code Player#actuallyHurt(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link singularityHooks#onLivingDamage(LivingEntity, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the final amount of damage that will be dealt to entity. <br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}.<br>
 * If this event is cancelled, the Entity is not hurt. Used resources WILL NOT be restored.<br>
 *
 * @see LivingHurtEvent
 **/
public final class LivingDamageEvent extends MutableEvent implements Cancellable, LivingEvent {
    public static final CancellableEventBus<LivingDamageEvent> BUS = CancellableEventBus.create(LivingDamageEvent.class);

    private final LivingEntity entity;
    private final DamageSource source;
    private float amount;

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
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
