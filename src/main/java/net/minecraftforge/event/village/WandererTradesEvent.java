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

import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

/**
 * WandererTradesEvent is fired during the {@link FMLServerAboutToStartEvent}.  It is used to gather the trade lists for the wandering merchant.
 * It is fired on the {@link MinecraftForge#EVENT_BUS}.
 * The wandering merchant picks a few trades from {@link TradeType#GENERIC} and a single trade from {@link TradeType#RARE}.
 * To add trades to the merchant, simply add new trades to the list. {@link BasicTrade} provides a default implementation.
*/
public class WandererTradesEvent extends Event
{

    protected List<ITrade> generic;
    protected List<ITrade> rare;

    public WandererTradesEvent(List<ITrade> generic, List<ITrade> rare)
    {
        this.generic = generic;
        this.rare = rare;
    }

    public List<ITrade> getGenericTrades()
    {
        return generic;
    }

    public List<ITrade> getRareTrades()
    {
        return rare;
    }
}
