/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.trading;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent.TradeType;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerTradingManager
{

    /**
     * Posts the WandererTradesEvent.  Called during common setup.
     */
    public static void postWandererEvent()
    {
        List<ITrade> generic = new ArrayList<>();
        List<ITrade> rare = new ArrayList<>();
        generic.addAll(Arrays.asList(VillagerTrades.field_221240_b.get(1)));
        rare.addAll(Arrays.asList(VillagerTrades.field_221240_b.get(2)));
        Map<TradeType, List<ITrade>> trades = new HashMap<>();
        trades.put(TradeType.GENERIC, generic);
        trades.put(TradeType.RARE, rare);
        MinecraftForge.EVENT_BUS.post(new WandererTradesEvent(trades));
        VillagerTrades.field_221240_b.put(1, trades.get(TradeType.GENERIC).toArray(new ITrade[0]));
        VillagerTrades.field_221240_b.put(2, trades.get(TradeType.RARE).toArray(new ITrade[0]));
    }

    /**
     * Posts a VillagerTradesEvent for each registered profession.  Called during common setup.
     */
    public static void postVillagerEvents()
    {
        for (VillagerProfession prof : ForgeRegistries.PROFESSIONS)
        {
            Int2ObjectMap<ITrade[]> trades = VillagerTrades.field_221239_a.computeIfAbsent(prof, a -> new Int2ObjectOpenHashMap<>());
            Int2ObjectMap<List<ITrade>> mutableTrades = new Int2ObjectOpenHashMap<>();
            trades.int2ObjectEntrySet().forEach(e -> mutableTrades.put(e.getIntKey(), Lists.newArrayList(e.getValue())));
            for (int i = 1; i < 6; i++)
                mutableTrades.computeIfAbsent(i, e -> new ArrayList<>());
            MinecraftForge.EVENT_BUS.post(new VillagerTradesEvent(mutableTrades, prof));
            mutableTrades.int2ObjectEntrySet().forEach(e -> trades.put(e.getIntKey(), e.getValue().toArray(new ITrade[0])));
        }
    }

}