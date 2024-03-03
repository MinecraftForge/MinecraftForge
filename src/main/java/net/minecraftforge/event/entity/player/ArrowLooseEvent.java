/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * ArrowLooseEvent is fired when a player stops using a bow.<br>
 * This event is fired whenever a player stops using a bow in
 * {@link BowItem#releaseUsing(ItemStack, Level, LivingEntity, int)}.<br>
 * <br>
 * {@link #bow} contains the ItemBow ItemStack that was used in this event.<br>
 * {@link #charge} contains the value for how much the player had charged before stopping the shot.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not stop using the bow.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ArrowLooseEvent extends PlayerEvent
{
    private final ItemStack bow;
    private final Level world;
    private final boolean hasAmmo;
    private int charge;

    public ArrowLooseEvent(Player player, @Nonnull ItemStack bow, Level world, int charge, boolean hasAmmo)
    {
        super(player);
        this.bow = bow;
        this.world = world;
        this.charge = charge;
        this.hasAmmo = hasAmmo;
    }

    @Nonnull
    public ItemStack getBow() { return this.bow; }
    public Level getWorld() { return this.world; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public int getCharge() { return this.charge; }
    public void setCharge(int charge) { this.charge = charge; }
}
