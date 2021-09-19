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

package net.minecraftforge.event;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires when a player joins the server or when the reload command is ran,
 * before tags and crafting recipes are sent to the client. Send datapack data
 * to clients when this event fires.
 */
public class OnDatapackSyncEvent extends Event
{
    private final PlayerList playerList;
    @Nullable
    private final ServerPlayerEntity player;

    public OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayerEntity player)
    {
        this.playerList = playerList;
        this.player = player;
    }

    /**
     * @return The server's player list to get a view of all players.
     */
    public PlayerList getPlayerList()
    {
        return this.playerList;
    }

    /**
     * @return The player to sync datapacks to. Null when syncing for all players,
     *         such as when the reload command runs.
     */
    @Nullable
    public ServerPlayerEntity getPlayer()
    {
        return this.player;
    }
}
