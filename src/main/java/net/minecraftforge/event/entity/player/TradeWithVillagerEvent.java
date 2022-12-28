/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a player trades with an {@link AbstractVillager}.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain Event.HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 */
public class TradeWithVillagerEvent extends PlayerEvent
{
    private final MerchantOffer offer;
    private final AbstractVillager abstractVillager;

    @ApiStatus.Internal
    public TradeWithVillagerEvent(Player player, MerchantOffer offer, AbstractVillager abstractVillager)
    {
        super(player);
        this.offer = offer;
        this.abstractVillager = abstractVillager;
    }

    /**
     * {@return the {@link MerchantOffer} selected by the player to trade with}
     */
    public MerchantOffer getMerchantOffer()
    {
        return offer;
    }

    /**
     * {@return the villager the player traded with}
     */
    public AbstractVillager getAbstractVillager()
    {
        return abstractVillager;
    }
}