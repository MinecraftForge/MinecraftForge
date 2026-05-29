/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.brewing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftsingularity.event.entity.player.PlayerEvent;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;

/**
 * This event is called when a player picks up a potion from a brewing stand.
 * @param getStack The ItemStack of the potion.
 */
@NullMarked
public record PlayerBrewedPotionEvent(Player getEntity, ItemStack getStack) implements RecordEvent, PlayerEvent {
    public static final EventBus<PlayerBrewedPotionEvent> BUS = EventBus.create(PlayerBrewedPotionEvent.class);
}
