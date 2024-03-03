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
public class ClientPlayerChangeGameModeEvent extends Event
{
    private final PlayerInfo info;
    private final GameType currentGameMode;
    private final GameType newGameMode;

    public ClientPlayerChangeGameModeEvent(PlayerInfo info, GameType currentGameMode, GameType newGameMode)
    {
        this.info = info;
        this.currentGameMode = currentGameMode;
        this.newGameMode = newGameMode;
    }

    public PlayerInfo getInfo()
    {
        return info;
    }

    public GameType getCurrentGameMode()
    {
        return currentGameMode;
    }

    public GameType getNewGameMode()
    {
        return newGameMode;
    }
}
