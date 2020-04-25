/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Client side player connectivity events.
 */
public class ClientPlayerNetworkEvent extends Event {
    private final PlayerController controller;
    private final ClientPlayerEntity player;
    private final NetworkManager networkManager;

    /**
     * @return the player controller for the client side
     */
    @Nullable
    public PlayerController getController() {
        return controller;
    }

    /**
     * @return the player instance (if present - may be null)
     */
    @Nullable
    public ClientPlayerEntity getPlayer() {
        return player;
    }

    /**
     * @return the network connection (if present - may be null)
     */
    @Nullable
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    ClientPlayerNetworkEvent(final PlayerController controller, final ClientPlayerEntity player, final NetworkManager networkManager) {
        this.controller = controller;
        this.player = player;
        this.networkManager = networkManager;
    }

    /**
     * Fired when the client player logs in to the server. The player should be initialized.
     */
    public static class LoggedInEvent extends ClientPlayerNetworkEvent {

        public LoggedInEvent(final PlayerController controller, final ClientPlayerEntity player, final NetworkManager networkManager) {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the player logs out. Note this might also fire when a new integrated server is being created.
     */
    public static class LoggedOutEvent extends ClientPlayerNetworkEvent {

        public LoggedOutEvent(final PlayerController controller, final ClientPlayerEntity player, final NetworkManager networkManager) {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the player object respawns, such as dimension changes.
     */
    public static class RespawnEvent extends ClientPlayerNetworkEvent {
        private final ClientPlayerEntity oldPlayer;

        public RespawnEvent(final PlayerController pc, final ClientPlayerEntity oldPlayer, final ClientPlayerEntity newPlayer, final NetworkManager networkManager) {
            super(pc, newPlayer, networkManager);
            this.oldPlayer = oldPlayer;
        }

        public ClientPlayerEntity getOldPlayer() {
            return oldPlayer;
        }

        public ClientPlayerEntity getNewPlayer() {
            return super.getPlayer();
        }
    }
}
