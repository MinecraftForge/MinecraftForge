/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * ServerOverloadedEvent is fired when the server is running behind ticking schedule and can't keep up.
 * You should avoid doing any heavy work in this event as doing so may cause the server watchdog to timeout and crash the server.<br>
 * <br>
 * Please do NOT use this event as a trigger for disabling Vanilla features - this may cause unexpected behaviour for players and
 * hard to reproduce compatibility issues with other mods that expect consistent Vanilla behaviour.<br>
 * <br>
 * Here's a couple of examples of intended usage of this event:
 * <ol>
 *     <li> to log data that could be useful for diagnosing why the server is overloaded
 *     <li> for sending automated alerts to server staff outside of the game
 * </ol>
 * <br>
 * This event is fired via {@link ServerLifecycleHooks#onServerOverloaded(MinecraftServer, long, long, long)},
 * which is executed from {@link MinecraftServer#runServer()} <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class ServerOverloadedEvent extends ServerLifecycleEvent
{
    private final long msBehind;
    private final long ticksBehind;
    private final long lastOverloadWarningMs;

    public ServerOverloadedEvent(final MinecraftServer server, final long msBehind, final long ticksBehind, final long lastOverloadWarningMs)
    {
        super(server);
        this.msBehind = msBehind;
        this.ticksBehind = ticksBehind;
        this.lastOverloadWarningMs = lastOverloadWarningMs;
    }

    /**
     * @return The amount of milliseconds the server is behind schedule
     */
    public long getMsBehind() { return msBehind; }

    /**
     * @return The number of ticks the server is behind schedule.
     */
    public long getTicksBehind() { return ticksBehind; }

    /**
     * @return The last time the server warned about being behind schedule.
     */
    public long getLastOverloadWarningMs() { return lastOverloadWarningMs; }
}
