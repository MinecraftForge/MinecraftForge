/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fires when a player joins the server or when the reload command is ran,
 * before tags and crafting recipes are sent to the client. Send datapack data
 * to clients when this event fires.
 *
 * @param playerList The server's player list to get a view of all players.
 * @param player The player to sync datapacks to. Null when syncing for all players,
 *               such as when the reload command runs.
 */
public record OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayer player) implements RecordEvent {
    public static final EventBus<OnDatapackSyncEvent> BUS = EventBus.create(OnDatapackSyncEvent.class);

    /**
     * @return A list of players that should receive data during this event, which is the specified player (if not null)
     *         or all players otherwise.
     */
    public List<ServerPlayer> players() {
        return this.player == null ? this.playerList.getPlayers() : List.of(this.player);
    }
}
