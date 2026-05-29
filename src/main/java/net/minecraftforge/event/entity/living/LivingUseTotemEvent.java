/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;

/**
 * Fired when an Entity attempts to use a totem to prevent its death.
 *
 * <p>This event is {@linkplain Cancellable cancellable}.
 * If this event is cancelled, the totem will not prevent the entity's death.</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 */
public record LivingUseTotemEvent(
        LivingEntity getEntity,
        DamageSource getSource,
        ItemStack getTotem,
        InteractionHand getHandHolding
) implements Cancellable, LivingEvent, RecordEvent {
    public static final CancellableEventBus<LivingUseTotemEvent> BUS = CancellableEventBus.create(LivingUseTotemEvent.class);

    /**
     * {@return the damage source that caused the entity to die}
     */
    public DamageSource getSource() {
        return getSource;
    }

    /**
     * {@return the totem of undying being used from the entity's inventory}
     */
    public ItemStack getTotem() {
        return getTotem;
    }

    /**
     * {@return the hand holding the totem}
     */
    public InteractionHand getHandHolding() {
        return getHandHolding;
    }
}
