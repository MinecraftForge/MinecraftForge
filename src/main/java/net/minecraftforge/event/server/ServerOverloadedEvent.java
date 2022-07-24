package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * ServerOverloadedEvent is fired when the server is running behind ticking schedule and can't keep up.<br>
 * This event is fired via (todo) <br>
 * <br>
 * {@link #msBehind} contains the amount of milliseconds the server is behind schedule.<br>
 * {@link #ticksBehind} contains the number of ticks the server is behind schedule.<br>
 * {@link #lastOverloadWarningMs} contains the last time the server warned about being behind schedule.<br>
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

    public long getMsBehind() { return msBehind; }
    public long getTicksBehind() { return ticksBehind; }
    public long getLastOverloadWarningMs() { return lastOverloadWarningMs; }
}
