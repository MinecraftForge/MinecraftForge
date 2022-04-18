/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.village;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.server.ServerAboutToStartEvent;

/**
 * VillagerTradesEvent is fired during the {@link ServerAboutToStartEvent}.  It is used to gather the trade lists for each profession.
 * It is fired on the {@link MinecraftForge#EVENT_BUS}.
 * It is fired once for each registered villager profession.
 * Villagers pick two trades from their trade map, based on their level.
 * Villager level is increased by successful trades.
 * The map is populated for levels 1-5 (inclusive), so Map#get will never return null for those keys.
 * Levels outside of this range do nothing, as specified by {@link VillagerData#canLevelUp(int)} which is called before attempting to level up.
 * To add trades to the merchant, simply add new trades to the list. {@link BasicItemListing} provides a default implementation.
 */
public class VillagerTradesEvent extends Event
{

    protected Int2ObjectMap<List<ItemListing>> trades;
    protected VillagerProfession type;

    public VillagerTradesEvent(Int2ObjectMap<List<ItemListing>> trades, VillagerProfession type)
    {
        this.trades = trades;
        this.type = type;
    }

    public Int2ObjectMap<List<ItemListing>> getTrades()
    {
        return trades;
    }

    public VillagerProfession getType()
    {
        return type;
    }

}
