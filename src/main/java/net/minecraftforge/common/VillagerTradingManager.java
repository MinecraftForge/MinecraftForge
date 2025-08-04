/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.core.NonNullList;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class VillagerTradingManager {
    private static final Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> VANILLA_TRADES = new HashMap<>();
    private static final List<Pair<ItemListing[], Integer>> WANDERER_TRADES = new ArrayList<>();

    static {
        VillagerTrades.TRADES.forEach((key, value) -> {
            Int2ObjectMap<ItemListing[]> copy = new Int2ObjectOpenHashMap<>();
            for (var ent : value.int2ObjectEntrySet()) {
                copy.put(ent.getIntKey(), Arrays.copyOf(ent.getValue(), ent.getValue().length));
            }
            VANILLA_TRADES.put(key, copy);
        });
        WANDERER_TRADES.addAll(VillagerTrades.WANDERING_TRADER_TRADES);
    }

    static void loadTrades(ServerAboutToStartEvent e) {
        postWandererEvent();
        postVillagerEvents();
    }

    /** Posts the WandererTradesEvent. */
    private static void postWandererEvent() {
        var event = WandererTradesEvent.BUS.fire(new WandererTradesEvent(WANDERER_TRADES));
        VillagerTrades.WANDERING_TRADER_TRADES.clear();
        for (var pool : event.getPools())
            VillagerTrades.WANDERING_TRADER_TRADES.add(Pair.of(pool.getEntries().toArray(ItemListing[]::new), pool.getRolls()));
    }

    /** Posts a VillagerTradesEvent for each registered profession. */
    private static void postVillagerEvents() {
        // TODO [VillagerType][1.21.5] Villager Professions are stored as keys in vanilla now? Re-evaluate this.
        for (VillagerProfession value : ForgeRegistries.VILLAGER_PROFESSIONS) {
            var prof = ForgeRegistries.VILLAGER_PROFESSIONS.getResourceKey(value).orElseThrow();
            Int2ObjectMap<ItemListing[]> trades = VANILLA_TRADES.getOrDefault(prof, new Int2ObjectOpenHashMap<>());
            Int2ObjectMap<List<ItemListing>> mutableTrades = new Int2ObjectOpenHashMap<>();
            for (int i = 1; i < 6; i++)
                mutableTrades.put(i, NonNullList.create());

            for (var entry : trades.int2ObjectEntrySet())
                Arrays.stream(entry.getValue()).forEach(mutableTrades.get(entry.getIntKey())::add);

            VillagerTradesEvent.BUS.post(new VillagerTradesEvent(mutableTrades, prof));
            Int2ObjectMap<ItemListing[]> newTrades = new Int2ObjectOpenHashMap<>();
            for (var entry : mutableTrades.int2ObjectEntrySet())
                newTrades.put(entry.getIntKey(), entry.getValue().toArray(new ItemListing[0]));

            VillagerTrades.TRADES.put(prof, newTrades);
        }
    }

}
