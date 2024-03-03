/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.Connection;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Client side player connectivity events.
 */
public class ClientPlayerNetworkEvent extends Event {
    private final MultiPlayerGameMode controller;
    private final LocalPlayer player;
    private final Connection networkManager;

    /**
     * @return the player controller for the client side
     */
    @Nullable
    public MultiPlayerGameMode getController() {
        return controller;
    }

    /**
     * @return the player instance (if present - may be null)
     */
    @Nullable
    public LocalPlayer getPlayer() {
        return player;
    }

    /**
     * @return the network connection (if present - may be null)
     */
    @Nullable
    public Connection getNetworkManager() {
        return networkManager;
    }

    ClientPlayerNetworkEvent(final MultiPlayerGameMode controller, final LocalPlayer player, final Connection networkManager) {
        this.controller = controller;
        this.player = player;
        this.networkManager = networkManager;
    }

    /**
     * Fired when the client player logs in to the server. The player should be initialized.
     */
    public static class LoggedInEvent extends ClientPlayerNetworkEvent {

        public LoggedInEvent(final MultiPlayerGameMode controller, final LocalPlayer player, final Connection networkManager) {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the player logs out. Note this might also fire when a new integrated server is being created.
     */
    public static class LoggedOutEvent extends ClientPlayerNetworkEvent {

        public LoggedOutEvent(final MultiPlayerGameMode controller, final LocalPlayer player, final Connection networkManager) {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the player object respawns, such as dimension changes.
     */
    public static class RespawnEvent extends ClientPlayerNetworkEvent {
        private final LocalPlayer oldPlayer;

        public RespawnEvent(final MultiPlayerGameMode pc, final LocalPlayer oldPlayer, final LocalPlayer newPlayer, final Connection networkManager) {
            super(pc, newPlayer, networkManager);
            this.oldPlayer = oldPlayer;
        }

        public LocalPlayer getOldPlayer() {
            return oldPlayer;
        }

        public LocalPlayer getNewPlayer() {
            return super.getPlayer();
        }
    }
}
