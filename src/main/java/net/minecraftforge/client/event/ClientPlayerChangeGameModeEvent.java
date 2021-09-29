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
