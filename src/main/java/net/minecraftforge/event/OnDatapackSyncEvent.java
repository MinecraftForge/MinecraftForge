/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
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
    private final ServerPlayer player;

    public OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayer player)
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
    public ServerPlayer getPlayer()
    {
        return this.player;
    }
}
