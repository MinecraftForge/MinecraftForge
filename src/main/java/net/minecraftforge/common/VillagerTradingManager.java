/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.core.NonNullList;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerTradingManager
{

    private static final Map<VillagerProfession, Int2ObjectMap<ItemListing[]>> VANILLA_TRADES = new HashMap<>();
    private static final Int2ObjectMap<ItemListing[]> WANDERER_TRADES = new Int2ObjectOpenHashMap<>();

    static
    {
        VillagerTrades.TRADES.forEach((key, value) -> {
            Int2ObjectMap<ItemListing[]> copy = new Int2ObjectOpenHashMap<>();
            for (var ent : value.int2ObjectEntrySet()) {
                copy.put(ent.getIntKey(), Arrays.copyOf(ent.getValue(), ent.getValue().length));
            }
            VANILLA_TRADES.put(key, copy);
        });
        VillagerTrades.WANDERING_TRADER_TRADES.int2ObjectEntrySet().forEach(e -> WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf(e.getValue(), e.getValue().length)));
    }

    static void loadTrades(ServerAboutToStartEvent e)
    {
        postWandererEvent();
        postVillagerEvents();
    }

    /**
     * Posts the WandererTradesEvent.
     */
    private static void postWandererEvent()
    {
        List<ItemListing> generic = NonNullList.create();
        List<ItemListing> rare = NonNullList.create();
        Arrays.stream(WANDERER_TRADES.get(1)).forEach(generic::add);
        Arrays.stream(WANDERER_TRADES.get(2)).forEach(rare::add);
        MinecraftForge.EVENT_BUS.post(new WandererTradesEvent(generic, rare));
        VillagerTrades.WANDERING_TRADER_TRADES.put(1, generic.toArray(new ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, rare.toArray(new ItemListing[0]));
    }

    /**
     * Posts a VillagerTradesEvent for each registered profession.
     */
    private static void postVillagerEvents()
    {
        for (VillagerProfession prof : ForgeRegistries.VILLAGER_PROFESSIONS)
        {
            Int2ObjectMap<ItemListing[]> trades = VANILLA_TRADES.getOrDefault(prof, new Int2ObjectOpenHashMap<>());
            Int2ObjectMap<List<ItemListing>> mutableTrades = new Int2ObjectOpenHashMap<>();
            for (int i = 1; i < 6; i++)
            {
                mutableTrades.put(i, NonNullList.create());
            }
            for (var entry : trades.int2ObjectEntrySet()) {
                Arrays.stream(entry.getValue()).forEach(mutableTrades.get(entry.getIntKey())::add);
            }
            MinecraftForge.EVENT_BUS.post(new VillagerTradesEvent(mutableTrades, prof));
            Int2ObjectMap<ItemListing[]> newTrades = new Int2ObjectOpenHashMap<>();
            for (var entry : mutableTrades.int2ObjectEntrySet()) {
                newTrades.put(entry.getIntKey(), entry.getValue().toArray(new ItemListing[0]));
            }
            VillagerTrades.TRADES.put(prof, newTrades);
        }
    }

}
