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
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class VillagerTradingManager {
    private static final Lazy<Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>>> VANILLA_TRADES = Lazy.of(VillagerTradingManager::copyVanillaTrades);
    private static final Lazy<List<Pair<ItemListing[], Integer>>> WANDERER_TRADES = Lazy.of(() -> new ArrayList<Pair<ItemListing[], Integer>>(VillagerTrades.WANDERING_TRADER_TRADES));

    private static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> copyVanillaTrades() {
        var ret = new HashMap<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>>();
        VillagerTrades.TRADES.forEach((key, value) -> {
            Int2ObjectMap<ItemListing[]> copy = new Int2ObjectOpenHashMap<>();
            for (var ent : value.int2ObjectEntrySet()) {
                copy.put(ent.getIntKey(), Arrays.copyOf(ent.getValue(), ent.getValue().length));
            }
            ret.put(key, copy);
        });
        return ret;
    }

    static void loadTrades(ServerAboutToStartEvent e) {
        postWandererEvent();
        postVillagerEvents();
    }

    /** Posts the WandererTradesEvent. */
    private static void postWandererEvent() {
        if (!WandererTradesEvent.BUS.hasListeners())
            return;
        var event = WandererTradesEvent.BUS.fire(new WandererTradesEvent(WANDERER_TRADES.get()));
        VillagerTrades.WANDERING_TRADER_TRADES.clear();
        for (var pool : event.getPools())
            VillagerTrades.WANDERING_TRADER_TRADES.add(Pair.of(pool.getEntries().toArray(ItemListing[]::new), pool.getRolls()));
    }

    /** Posts a VillagerTradesEvent for each registered profession. */
    private static void postVillagerEvents() {
        if (!VillagerTradesEvent.BUS.hasListeners())
            return;
        // TODO [VillagerType][1.21.5] Villager Professions are stored as keys in vanilla now? Re-evaluate this.
        var vanilla = VANILLA_TRADES.get();
        for (VillagerProfession value : ForgeRegistries.VILLAGER_PROFESSIONS) {
            var prof = ForgeRegistries.VILLAGER_PROFESSIONS.getResourceKey(value).orElseThrow();
            Int2ObjectMap<ItemListing[]> trades = vanilla.getOrDefault(prof, new Int2ObjectOpenHashMap<>());
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
