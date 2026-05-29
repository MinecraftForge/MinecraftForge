/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a player trades with an {@link AbstractVillager}.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 *
 * @param getMerchantOffer the {@link MerchantOffer} selected by the player to trade with
 * @param getAbstractVillager the villager the player traded with
 */
public record TradeWithVillagerEvent(
        Player getEntity,
        MerchantOffer getMerchantOffer,
        AbstractVillager getAbstractVillager
) implements RecordEvent, PlayerEvent {
    public static final EventBus<TradeWithVillagerEvent> BUS = EventBus.create(TradeWithVillagerEvent.class);

    @ApiStatus.Internal
    public TradeWithVillagerEvent {}
}
