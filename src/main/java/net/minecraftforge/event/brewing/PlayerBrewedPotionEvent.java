/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.brewing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;

/**
 * This event is called when a player picks up a potion from a brewing stand.
 *
 * @param stack The item stack of the potion
 */
public record PlayerBrewedPotionEvent(Player entity, ItemStack stack) implements PlayerEvent, RecordEvent {
    public static final EventBus<PlayerBrewedPotionEvent> BUS = EventBus.create(PlayerBrewedPotionEvent.class);
}
