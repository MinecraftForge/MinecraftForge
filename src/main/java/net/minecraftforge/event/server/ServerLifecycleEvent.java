package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerLifecycleEvent extends Event
{
    private final MinecraftServer server;
    protected ServerLifecycleEvent(MinecraftServer server)
    {
        this.server = server;
    }
    public MinecraftServer getServer()
    {
        return this.server;
    }
    
    /**
     * This event is fired before the server starts.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class Starting extends ServerLifecycleEvent
    {
        public Starting(MinecraftServer server)
        {
            super(server);
        }
    }

    /**
     * This event is fired after the server starts.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class Started extends ServerLifecycleEvent
    {
        public Started(MinecraftServer server)
        {
            super(server);
        }
    }

    /**
     * This event is fired before the server shuts down.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class Stopping extends ServerLifecycleEvent
    {
        public Stopping(MinecraftServer server)
        {
            super(server);
        }
    }

    /**
     * This event is fired after the server shuts down.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class Stopped extends ServerLifecycleEvent
    {
        public Stopped(MinecraftServer server)
        {
            super(server);
        }
    }
}
