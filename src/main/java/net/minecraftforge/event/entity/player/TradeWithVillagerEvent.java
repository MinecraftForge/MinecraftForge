/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;

/**
 * Fired when a player trades with an {@link AbstractVillager}.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain Event.HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 *
 * @param merchantOffer the {@link MerchantOffer} selected by the player to trade with
 * @param abstractVillager the villager the player traded with
 */
public record TradeWithVillagerEvent(Player entity, MerchantOffer merchantOffer, AbstractVillager abstractVillager)
        implements PlayerEvent {
    public static final EventBus<TradeWithVillagerEvent> BUS = EventBus.create(TradeWithVillagerEvent.class);
}
