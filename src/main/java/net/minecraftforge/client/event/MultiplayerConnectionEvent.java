package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Represents an event having to do with server and multiplayer connections.
 */
public class MultiplayerConnectionEvent extends Event
{
        /**
         * Enables a mod to do things to the client just before connecting to a server.
         * Examples might be to set up a server-specific profile, or load config data settings.
         *
         * This event is cancelable- if canceled, the connection attempt will stop processing.
         */
        @Cancelable
        public static class Pre extends MultiplayerConnectionEvent
        {
            private ServerData serverData;
            public Pre(ServerData serverEntry)
            {
                this.serverData = serverEntry;
            }

            public ServerData getServerData()
            {
                return serverData;
            }
        }

}
