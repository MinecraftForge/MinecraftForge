/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftsingularity.common.singularityHooks;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * PlayerDestroyItemEvent is fired when a player destroys an item.<br>
 * This event is fired whenever a player destroys an item in
 * {@link MultiPlayerGameMode#destroyBlock(BlockPos)},
 * {@link MultiPlayerGameMode#useItem(Player, InteractionHand)},
 * {@link MultiPlayerGameMode#useItemOn(LocalPlayer, InteractionHand, BlockHitResult)} ,
 * {@link Player#attack(Entity)},
 * {@code Player#hurtCurrentlyUsedShield(float)},
 * {@link Player#interactOn(Entity, InteractionHand)},
 * {@link singularityHooks#getCraftingRemainingItem(ItemStack)},
 * {@link ServerPlayerGameMode#useItem(ServerPlayer, Level, ItemStack, InteractionHand)} ,
 * {@link ServerPlayerGameMode#useItemOn(ServerPlayer, Level, ItemStack, InteractionHand, BlockHitResult)}
 * and {@link ServerPlayerGameMode#destroyBlock(BlockPos)}.<br>
 * <br>
 * {@link #original} contains the original ItemStack before the item was destroyed. <br>
 * (@link #hand) contains the hand that the current item was held in.<br>
 * <br>
 * This event is fired from {@link singularityEventFactory#onPlayerDestroyItem(Player, ItemStack, InteractionHand)}.<br>
 *
 * @param getSlot May be null if this player destroys the item by any use besides holding it.
 **/
@NullMarked
public record PlayerDestroyItemEvent(Player getEntity, ItemStack getOriginal, @Nullable EquipmentSlot getSlot)
        implements PlayerEvent, RecordEvent {
    public static final EventBus<PlayerDestroyItemEvent> BUS = EventBus.create(PlayerDestroyItemEvent.class);
}
