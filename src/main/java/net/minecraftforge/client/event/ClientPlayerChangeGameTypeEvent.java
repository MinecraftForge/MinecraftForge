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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when the client player is notified of a change of {@link GameType} from the server.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ForgeHooksClient#onClientChangeGameMode(PlayerInfo, GameType, GameType)
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

    /**
     * {@return the client player information}
     */
    public PlayerInfo getPlayerInfo()
    {
        return info;
    }

    /**
     * {@return the current game type of the player}
     */
    public GameType getCurrentGameType()
    {
        return currentGameType;
    }

    /**
     * {@return the new game type of the player}
     */
    public GameType getNewGameType()
    {
        return newGameType;
    }
}
