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

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.Connection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * Fired on the client for different connectivity events.
 * See the various subclasses to listen for specific events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ClientPlayerNetworkEvent.LoggedInEvent
 * @see ClientPlayerNetworkEvent.LoggedOutEvent
 * @see ClientPlayerNetworkEvent.RespawnEvent
 **/
public class ClientPlayerNetworkEvent extends Event
{
    private final MultiPlayerGameMode multiPlayerGameMode;
    private final LocalPlayer player;
    private final Connection connection;

    /**
     * {@return the multiplayer game mode controller for the player, or {@code null}}
     */
    @Nullable
    public MultiPlayerGameMode getMultiPlayerGameMode()
    {
        return multiPlayerGameMode;
    }

    /**
     * {@return the player instance, or {@code null}}
     */
    @Nullable
    public LocalPlayer getPlayer()
    {
        return player;
    }

    /**
     * {@return the network connection for the player, or {@code null}}
     */
    @Nullable
    public Connection getConnection()
    {
        return connection;
    }

    ClientPlayerNetworkEvent(final MultiPlayerGameMode multiPlayerGameMode, final LocalPlayer player, final Connection connection)
    {
        this.multiPlayerGameMode = multiPlayerGameMode;
        this.player = player;
        this.connection = connection;
    }

    /**
     * Fired when the client player logs in to the server. The player should be initialized.
     *
     * <p>{@link #getMultiPlayerGameMode()}, {@link #getPlayer()}, and {@link #getConnection()} are guaranteed
     * to never return {@code null} in this event. </p>
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see net.minecraftforge.fmlclient.ClientHooks#firePlayerLogin(MultiPlayerGameMode, LocalPlayer, Connection)
     */
    public static class LoggedInEvent extends ClientPlayerNetworkEvent
    {
        public LoggedInEvent(final MultiPlayerGameMode controller, final LocalPlayer player, final Connection networkManager)
        {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the player logs out. This event may also fire when a new integrated server is being created.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see net.minecraftforge.fmlclient.ClientHooks#firePlayerLogout(MultiPlayerGameMode, LocalPlayer)
     */
    public static class LoggedOutEvent extends ClientPlayerNetworkEvent
    {
        public LoggedOutEvent(final MultiPlayerGameMode controller, final LocalPlayer player, final Connection networkManager)
        {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the player object respawns, such as dimension changes.
     *
     * <p>{@link #getNewPlayer()} returns the same player instance as {@link #getPlayer()}.
     * {@link #getMultiPlayerGameMode()}, {@link #getPlayer()}, and {@link #getConnection()} are guaranteed
     * to never return {@code null} in this event. </p>
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see net.minecraftforge.fmlclient.ClientHooks#firePlayerRespawn(MultiPlayerGameMode, LocalPlayer, LocalPlayer, Connection)
     */
    public static class RespawnEvent extends ClientPlayerNetworkEvent
    {
        private final LocalPlayer oldPlayer;

        public RespawnEvent(final MultiPlayerGameMode pc, final LocalPlayer oldPlayer, final LocalPlayer newPlayer, final Connection networkManager)
        {
            super(pc, newPlayer, networkManager);
            this.oldPlayer = oldPlayer;
        }

        /**
         * {@return the previous player instance}
         */
        public LocalPlayer getOldPlayer()
        {
            return oldPlayer;
        }

        /**
         * {@return the newly created player instance}
         */
        public LocalPlayer getNewPlayer()
        {
            return super.getPlayer();
        }
    }
}
