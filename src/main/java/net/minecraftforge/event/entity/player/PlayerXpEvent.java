/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * PlayerXpEvent is fired whenever an event involving player experience occurs. <br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public sealed interface PlayerXpEvent extends PlayerEvent {
    /**
     * This event is fired after the player collides with an experience orb, but before the player has been given the experience.
     * It can be cancelled, and no further processing will be done.
     */
    record PickupXp(Player entity, ExperienceOrb orb) implements Cancellable, PlayerXpEvent {
        public static final CancellableEventBus<PickupXp> BUS = CancellableEventBus.create(PickupXp.class);
    }

    /**
     * This event is fired when the player's experience changes through the {@link Player#giveExperiencePoints(int)} method.
     * It can be cancelled, and no further processing will be done.
     */
    final class XpChange implements Cancellable, PlayerXpEvent {
        public static final CancellableEventBus<XpChange> BUS = CancellableEventBus.create(XpChange.class);

        private final Player entity;
        private int amount;

        public XpChange(Player player, int amount) {
            this.entity = player;
            this.amount = amount;
        }

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        @Override
        public Player entity() {
            return this.entity;
        }
    }

    /**
     * This event is fired when the player's experience level changes through the {@link Player#giveExperienceLevels(int)} method.
     * It can be cancelled, and no further processing will be done.
     */
    final class LevelChange implements Cancellable, PlayerXpEvent {
        public static final CancellableEventBus<LevelChange> BUS = CancellableEventBus.create(LevelChange.class);

        private final Player entity;
        private int levels;

        public LevelChange(Player player, int levels) {
            this.entity = player;
            this.levels = levels;
        }

        public int getLevels() {
            return this.levels;
        }

        public void setLevels(int levels) {
            this.levels = levels;
        }

        @Override
        public Player entity() {
            return this.entity;
        }
    }
}
