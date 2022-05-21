/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.village;

import java.util.List;

import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.server.ServerAboutToStartEvent;

/**
 * WandererTradesEvent is fired during the {@link ServerAboutToStartEvent}.  It is used to gather the trade lists for the wandering merchant.
 * It is fired on the {@link MinecraftForge#EVENT_BUS}.
 * The wandering merchant picks a few trades from {@code generic} and a single trade from {@code rare}.
 * To add trades to the merchant, simply add new trades to the list. {@link BasicItemListing} provides a default implementation.
*/
public class WandererTradesEvent extends Event
{

    protected List<ItemListing> generic;
    protected List<ItemListing> rare;

    public WandererTradesEvent(List<ItemListing> generic, List<ItemListing> rare)
    {
        this.generic = generic;
        this.rare = rare;
    }

    public List<ItemListing> getGenericTrades()
    {
        return generic;
    }

    public List<ItemListing> getRareTrades()
    {
        return rare;
    }
}
