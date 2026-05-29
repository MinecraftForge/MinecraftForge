/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.NotNull;

/**
 * {@link LivingEquipmentChangeEvent} is fired when the Equipment of a Entity changes. <br>
 * This event is fired whenever changes in Equipment are detected in {@link LivingEntity#tick()}. <br>
 * This also includes entities joining the World, as well as being cloned. <br>
 * This event is fired on server-side only. <br>
 * <br>
 * @param getSlot contains the affected {@link EquipmentSlot}.
 * @param getFrom contains the {@link ItemStack} that was equipped previously.
 * @param getTo contains the {@link ItemStack} that is equipped now.
 */
public record LivingEquipmentChangeEvent(
        LivingEntity getEntity,
        EquipmentSlot getSlot,
        @NotNull ItemStack getFrom,
        @NotNull ItemStack getTo
) implements LivingEvent, RecordEvent {
    public static final EventBus<LivingEquipmentChangeEvent> BUS = EventBus.create(LivingEquipmentChangeEvent.class);
}
