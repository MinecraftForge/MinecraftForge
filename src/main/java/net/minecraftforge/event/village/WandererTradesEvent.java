/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.village;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.Util;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.server.ServerAboutToStartEvent;

/**
 * WandererTradesEvent is fired during the {@link ServerAboutToStartEvent}.  It is used to gather the trade lists for the wandering merchant.
 * It is fired on the {@link MinecraftForge#EVENT_BUS}.
 * The wandering merchant picks {@link Pool#rolls} from each {@link Pool}
 * To add trades to the merchant, simply add new trades to the list. {@link BasicItemListing} provides a default implementation.
*/
public class WandererTradesEvent extends Event {
    protected List<Pool> pools;

    public WandererTradesEvent(List<Pair<ItemListing[], Integer>> pools) {
        this.pools = pools.stream().map(Pool::new).collect(Util.toMutableList());
    }

    public List<Pool> getPools() {
        return pools;
    }

    public static class Pool {
        private int rolls;
        private final List<ItemListing> entries;

        public Pool(Pair<ItemListing[], Integer> data) {
            this.rolls = data.getRight();
            this.entries = new ArrayList<>(Arrays.asList(data.getLeft()));
        }

        public int getRolls() {
            return this.rolls;
        }

        public void setRolls(int value) {
            this.rolls = value;
        }

        public List<ItemListing> getEntries() {
            return this.entries;
        }
    }
}
