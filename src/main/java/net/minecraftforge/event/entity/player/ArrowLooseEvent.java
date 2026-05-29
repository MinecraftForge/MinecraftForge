/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * ArrowLooseEvent is fired when a player stops using a bow.<br>
 * This event is fired whenever a player stops using a bow in
 * {@link BowItem#releaseUsing(ItemStack, Level, LivingEntity, int)}.<br>
 * <br>
 * {@link #bow} contains the ItemBow ItemStack that was used in this event.<br>
 * {@link #charge} contains the value for how much the player had charged before stopping the shot.<br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}.<br>
 * If this event is cancelled, the player does not stop using the bow.<br>
 * For crossbows, the charge will always be 1; Set it to -1 in order to prevent firing the arrow. <br>
 */
public final class ArrowLooseEvent extends MutableEvent implements Cancellable, PlayerEvent {
    public static final CancellableEventBus<ArrowLooseEvent> BUS = CancellableEventBus.create(ArrowLooseEvent.class);

    private final Player player;
    private final ItemStack bow;
    private final Level level;
    private final boolean hasAmmo;
    private int charge;

    public ArrowLooseEvent(Player player, @NotNull ItemStack bow, Level level, int charge, boolean hasAmmo) {
        this.player = player;
        this.bow = bow;
        this.level = level;
        this.charge = charge;
        this.hasAmmo = hasAmmo;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    @NotNull
    public ItemStack getBow() { return this.bow; }
    public Level getLevel() { return this.level; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public int getCharge() { return this.charge; }
    public void setCharge(int charge) { this.charge = charge; }
}
