/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired before the client player is notified of a change in game mode from the server.
 */
public class ClientPlayerChangeGameTypeEvent extends Event
{
    private final PlayerInfo info;
    private final GameType currentGameType;
    private final GameType newGameType;

    public ClientPlayerChangeGameTypeEvent(PlayerInfo info, GameType currentGameType, GameType newGameType)
    {
        this.info = info;
        this.currentGameType = currentGameType;
        this.newGameType = newGameType;
    }

    public PlayerInfo getInfo()
    {
        return info;
    }

    public GameType getCurrentGameType()
    {
        return currentGameType;
    }

    public GameType getNewGameType()
    {
        return newGameType;
    }
}
