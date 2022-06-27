/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Fired for different client connectivity events.
 * See the various subclasses to listen for specific events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see LoggingIn
 * @see LoggingOut
 * @see Clone
 **/
public abstract class ClientPlayerNetworkEvent extends Event
{
    private final MultiPlayerGameMode multiPlayerGameMode;
    private final LocalPlayer player;
    private final Connection connection;

    @ApiStatus.Internal
    protected ClientPlayerNetworkEvent(final MultiPlayerGameMode multiPlayerGameMode, final LocalPlayer player, final Connection connection)
    {
        this.multiPlayerGameMode = multiPlayerGameMode;
        this.player = player;
        this.connection = connection;
    }

    /**
     * {@return the multiplayer game mode controller for the player}
     */
    public MultiPlayerGameMode getMultiPlayerGameMode()
    {
        return multiPlayerGameMode;
    }

    /**
     * {@return the player instance}
     */
    public LocalPlayer getPlayer()
    {
        return player;
    }

    /**
     * {@return the network connection for the player}
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Fired when the client player logs in to the server. The player should be initialized.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class LoggingIn extends ClientPlayerNetworkEvent
    {
        @ApiStatus.Internal
        public LoggingIn(final MultiPlayerGameMode controller, final LocalPlayer player, final Connection networkManager)
        {
            super(controller, player, networkManager);
        }
    }

    /**
     * Fired when the client player logs out. This event may also fire when a new integrated server is being created.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @SuppressWarnings("NullableProblems")
    // Shush IntelliJ, we override non-nullables as nullables in this specific event; see later comment
    public static class LoggingOut extends ClientPlayerNetworkEvent
    {
        @ApiStatus.Internal
        public LoggingOut(@Nullable final MultiPlayerGameMode controller, @Nullable final LocalPlayer player, @Nullable final Connection networkManager)
        {
            //noinspection ConstantConditions we know these are nullable, but we don't want to annotate the super as nullable since this is the only event with nullables
            super(controller, player, networkManager);
        }

        /**
         * {@return the multiplayer game mode controller for the player, may be {@code null}}. This may be {@code null}
         * in certain situations such as the creating a new integrated server (singleplayer world) or connecting to
         * a multiplayer server.
         */
        @Nullable
        @Override
        public MultiPlayerGameMode getMultiPlayerGameMode()
        {
            return super.getMultiPlayerGameMode();
        }

        /**
         * {@return the player instance, may be {@code null}}. This may be {@code null}
         * in certain situations such as the creating a new integrated server (singleplayer world) or connecting to
         * a multiplayer server.
         */
        @Nullable
        @Override
        public LocalPlayer getPlayer()
        {
            return super.getPlayer();
        }

        /**
         * {@return the network connection for the player, may be {@code null}}. This may be {@code null}
         * in certain situations such as the creating a new integrated server (singleplayer world) or connecting to
         * a multiplayer server.
         */
        @Nullable
        @Override
        public Connection getConnection()
        {
            return super.getConnection();
        }
    }

    /**
     * Fired when the client player respawns, creating a new player instance to replace the old player instance.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Clone extends ClientPlayerNetworkEvent
    {
        private final LocalPlayer oldPlayer;

        @ApiStatus.Internal
        public Clone(final MultiPlayerGameMode pc, final LocalPlayer oldPlayer, final LocalPlayer newPlayer, final Connection networkManager)
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

        /**
         * {@return the newly created player instance}
         *
         * @see #getNewPlayer()
         */
        @Override
        public LocalPlayer getPlayer()
        {
            return super.getPlayer();
        }
    }
}
