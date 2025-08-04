/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.bus.EventBus;

public sealed class PlayerContainerEvent extends PlayerEvent {
    public static final EventBus<PlayerContainerEvent> BUS = EventBus.create(PlayerContainerEvent.class);

    private final AbstractContainerMenu container;

    public PlayerContainerEvent(Player player, AbstractContainerMenu container) {
        super(player);
        this.container = container;
    }

    public static final class Open extends PlayerContainerEvent {
        public static final EventBus<Open> BUS = EventBus.create(Open.class);

        public Open(Player player, AbstractContainerMenu container) {
            super(player, container);
        }
    }

    public static final class Close extends PlayerContainerEvent {
        public static final EventBus<Close> BUS = EventBus.create(Close.class);

        public Close(Player player, AbstractContainerMenu container) {
            super(player, container);
        }
    }

    public AbstractContainerMenu getContainer() {
        return container;
    }
}
