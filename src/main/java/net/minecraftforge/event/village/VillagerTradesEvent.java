/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event.village;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

/**
 * VillagerTradesEvent is fired during the {@link FMLServerAboutToStartEvent}.  It is used to gather the trade lists for each profession.
 * It is fired on the {@link MinecraftForge#EVENT_BUS}.
 * It is fired once for each registered villager profession.
 * Villagers pick two trades from their trade map, based on their level.
 * Villager level is increased by successful trades.
 * The map is populated for levels 1-5 (inclusive), so Map#get will never return null for those keys.
 * Levels outside of this range do nothing, as specified by {@link VillagerData#canLevelUp(int)} which is called before attempting to level up.
 * To add trades to the merchant, simply add new trades to the list. {@link BasicTrade} provides a default implementation.
 */
public class VillagerTradesEvent extends Event
{

    protected Int2ObjectMap<List<ITrade>> trades;
    protected VillagerProfession type;

    public VillagerTradesEvent(Int2ObjectMap<List<ITrade>> trades, VillagerProfession type)
    {
        this.trades = trades;
        this.type = type;
    }

    public Int2ObjectMap<List<ITrade>> getTrades()
    {
        return trades;
    }

    public VillagerProfession getType()
    {
        return type;
    }

}
