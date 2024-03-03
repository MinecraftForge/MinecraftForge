/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

/**
 * ArrowNockEvent is fired when a player begins using a bow.<br>
 * This event is fired whenever a player begins using a bow in
 * {@link BowItem#use(Level, Player, InteractionHand)}.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ArrowNockEvent extends PlayerEvent
{
    private final ItemStack bow;
    private final InteractionHand hand;
    private final Level world;
    private final boolean hasAmmo;
    private InteractionResultHolder<ItemStack> action;

    public ArrowNockEvent(Player player, @Nonnull ItemStack item, InteractionHand hand, Level world, boolean hasAmmo)
    {
        super(player);
        this.bow = item;
        this.hand = hand;
        this.world = world;
        this.hasAmmo = hasAmmo;
    }

    @Nonnull
    public ItemStack getBow() { return this.bow; }
    public Level getWorld() { return this.world; }
    public InteractionHand getHand() { return this.hand; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public InteractionResultHolder<ItemStack> getAction()
    {
        return this.action;
    }

    public void setAction(InteractionResultHolder<ItemStack> action)
    {
        this.action = action;
    }
}
