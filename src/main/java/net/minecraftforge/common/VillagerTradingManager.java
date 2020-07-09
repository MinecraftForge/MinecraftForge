/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerTradingManager
{

    private static final Map<VillagerProfession, Int2ObjectMap<ITrade[]>> VANILLA_TRADES = new HashMap<>();
    private static final Int2ObjectMap<ITrade[]> WANDERER_TRADES = new Int2ObjectOpenHashMap<>();

    static
    {
        VillagerTrades.VILLAGER_DEFAULT_TRADES.entrySet().forEach(e ->
        {
            Int2ObjectMap<ITrade[]> copy = new Int2ObjectOpenHashMap<>();
            e.getValue().int2ObjectEntrySet().forEach(ent -> copy.put(ent.getIntKey(), Arrays.copyOf(ent.getValue(), ent.getValue().length)));
            VANILLA_TRADES.put(e.getKey(), copy);
        });
        VillagerTrades.field_221240_b.int2ObjectEntrySet().forEach(e -> WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf(e.getValue(), e.getValue().length)));
    }

    static void loadTrades(FMLServerAboutToStartEvent e)
    {
        postWandererEvent();
        postVillagerEvents();
    }

    /**
     * Posts the WandererTradesEvent.
     */
    private static void postWandererEvent()
    {
        List<ITrade> generic = NonNullList.create();
        List<ITrade> rare = NonNullList.create();
        Arrays.stream(WANDERER_TRADES.get(1)).forEach(generic::add);
        Arrays.stream(WANDERER_TRADES.get(2)).forEach(rare::add);
        MinecraftForge.EVENT_BUS.post(new WandererTradesEvent(generic, rare));
        VillagerTrades.field_221240_b.put(1, generic.toArray(new ITrade[0]));
        VillagerTrades.field_221240_b.put(2, rare.toArray(new ITrade[0]));
    }

    /**
     * Posts a VillagerTradesEvent for each registered profession.
     */
    private static void postVillagerEvents()
    {
        for (VillagerProfession prof : ForgeRegistries.PROFESSIONS)
        {
            Int2ObjectMap<ITrade[]> trades = VANILLA_TRADES.getOrDefault(prof, new Int2ObjectOpenHashMap<>());
            Int2ObjectMap<List<ITrade>> mutableTrades = new Int2ObjectOpenHashMap<>();
            for (int i = 1; i < 6; i++)
            {
                mutableTrades.put(i, NonNullList.create());
            }
            trades.int2ObjectEntrySet().forEach(e ->
            {
                Arrays.stream(e.getValue()).forEach(mutableTrades.get(e.getIntKey())::add);
            });
            MinecraftForge.EVENT_BUS.post(new VillagerTradesEvent(mutableTrades, prof));
            Int2ObjectMap<ITrade[]> newTrades = new Int2ObjectOpenHashMap<>();
            mutableTrades.int2ObjectEntrySet().forEach(e -> newTrades.put(e.getIntKey(), e.getValue().toArray(new ITrade[0])));
            VillagerTrades.VILLAGER_DEFAULT_TRADES.put(prof, newTrades);
        }
    }

}
