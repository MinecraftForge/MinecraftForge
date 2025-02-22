/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MarkerEvent;

@MarkerEvent
public sealed interface PlayerContainerEvent extends PlayerEvent {
    AbstractContainerMenu container();

    record Open(Player entity, AbstractContainerMenu container) implements PlayerContainerEvent {
        public static final EventBus<Open> BUS = EventBus.create(Open.class);
    }

    record Close(Player entity, AbstractContainerMenu container) implements PlayerContainerEvent {
        public static final EventBus<Close> BUS = EventBus.create(Close.class);
    }
}
