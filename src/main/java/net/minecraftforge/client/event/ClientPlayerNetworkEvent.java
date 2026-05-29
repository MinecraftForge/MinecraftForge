/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * Fired for different client connectivity events.
 * See the various subclasses to listen for specific events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see LoggingIn
 * @see LoggingOut
 * @see Clone
 **/
public sealed interface ClientPlayerNetworkEvent extends InheritableEvent {
    EventBus<ClientPlayerNetworkEvent> BUS = EventBus.create(ClientPlayerNetworkEvent.class);

    /**
     * {@return the multiplayer game mode controller for the player}
     */
    MultiPlayerGameMode getMultiPlayerGameMode();

    /**
     * {@return the player instance}
     */
    LocalPlayer getPlayer();

    /**
     * {@return the network connection for the player}
     */
    Connection getConnection();

    /**
     * Fired when the client player logs in to the server. The player should be initialized.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @NullMarked
    record LoggingIn(MultiPlayerGameMode getMultiPlayerGameMode, LocalPlayer getPlayer, Connection getConnection)
            implements ClientPlayerNetworkEvent {
        public static final EventBus<LoggingIn> BUS = EventBus.create(LoggingIn.class);

        @ApiStatus.Internal
        public LoggingIn {}
    }

    /**
     * Fired when the client player logs out. This event may also fire when a new integrated server is being created.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @SuppressWarnings("NullableProblems")
    // Shush IntelliJ, we override non-nullables as nullables in this specific event; see later comment
    record LoggingOut(
            @Nullable MultiPlayerGameMode getMultiPlayerGameMode,
            @Nullable LocalPlayer getPlayer,
            @Nullable Connection getConnection
    ) implements ClientPlayerNetworkEvent {
        public static final EventBus<LoggingOut> BUS = EventBus.create(LoggingOut.class);

        @ApiStatus.Internal
        public LoggingOut {
            //noinspection ConstantConditions we know these are nullable, but we don't want to annotate the super as nullable since this is the only event with nullables
        }

        /**
         * {@return the multiplayer game mode controller for the player, may be {@code null}}. This may be {@code null}
         * in certain situations such as the creating a new integrated server (singleplayer world) or connecting to
         * a multiplayer server.
         */
        @Nullable
        @Override
        public MultiPlayerGameMode getMultiPlayerGameMode() {
            return getMultiPlayerGameMode;
        }

        /**
         * {@return the player instance, may be {@code null}}. This may be {@code null}
         * in certain situations such as the creating a new integrated server (singleplayer world) or connecting to
         * a multiplayer server.
         */
        @Nullable
        @Override
        public LocalPlayer getPlayer() {
            return getPlayer;
        }

        /**
         * {@return the network connection for the player, may be {@code null}}. This may be {@code null}
         * in certain situations such as the creating a new integrated server (singleplayer world) or connecting to
         * a multiplayer server.
         */
        @Nullable
        @Override
        public Connection getConnection() {
            return getConnection;
        }
    }

    /**
     * Fired when the client player respawns, creating a new player instance to replace the old player instance.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param getOldPlayer the previous player instance
     * @param getNewPlayer the newly created player instance
     */
    @NullMarked
    record Clone(
            MultiPlayerGameMode getMultiPlayerGameMode,
            LocalPlayer getOldPlayer,
            LocalPlayer getNewPlayer,
            Connection getConnection
    ) implements ClientPlayerNetworkEvent {
        public static final EventBus<Clone> BUS = EventBus.create(Clone.class);

        @ApiStatus.Internal
        public Clone {}

        /**
         * {@return the newly created player instance}
         *
         * @see #getNewPlayer()
         */
        @Override
        public LocalPlayer getPlayer() {
            return getNewPlayer;
        }
    }
}
