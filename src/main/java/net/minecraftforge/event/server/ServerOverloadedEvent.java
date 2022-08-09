/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.ServerWatchdog;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the server is running behind ticking schedule and can't keep up.
 * <p>Heavy work should be avoided in listeners for this event, as doing so may cause the {@link ServerWatchdog} to timeout and crash the server.</p>
 * <br>
 * <p>Please <strong>do <em>not</em> use this event as a trigger for disabling Vanilla features</strong> - this may cause unexpected behaviour for players and
 * hard-to-reproduce compatibility issues with other mods that expect consistent Vanilla behaviour.</p>
 * <br>
 * <p>Here are a couple of examples of intended usages of this event:</p>
 * <ul>
 *     <li>to log data that could be useful for diagnosing why the server is overloaded</li>
 *     <li>for sending automated alerts to server staff outside of the game and console</li>
 * </ul>
 * <p>This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.</p>
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}, only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 */
public class ServerOverloadedEvent extends ServerLifecycleEvent
{
    private final long msBehind;
    private final long ticksBehind;
    private final long lastOverloadWarningMs;

    @ApiStatus.Internal
    public ServerOverloadedEvent(final MinecraftServer server, final long msBehind, final long ticksBehind, final long lastOverloadWarningMs)
    {
        super(server);
        this.msBehind = msBehind;
        this.ticksBehind = ticksBehind;
        this.lastOverloadWarningMs = lastOverloadWarningMs;
    }

    /**
     * {@return The amount of milliseconds the server is behind schedule}
     */
    public long getMsBehind() { return msBehind; }

    /**
     * {@return The number of ticks the server is behind schedule.}
     */
    public long getTicksBehind() { return ticksBehind; }

    /**
     * {@return The last time the server warned about being behind schedule.}
     */
    public long getLastOverloadWarningMs() { return lastOverloadWarningMs; }
}
