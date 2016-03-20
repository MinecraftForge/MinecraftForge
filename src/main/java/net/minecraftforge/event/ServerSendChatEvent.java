package net.minecraftforge.event;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * ServerSendChatEvent is fired whenever a chat message is sent by the server (e.g. with the /say command)
 * Fired via {@link ForgeEventFactory#serverSendChat} which is called from {@link net.minecraft.server.management.ServerConfigurationManager#sendChatMsg}
 * 
 * {@link #message} contains the message to be sent by the server
 * 
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}
 * This event does not have a result
 */
@Cancelable
public class ServerSendChatEvent extends Event
{
	
    private IChatComponent message;

    public ServerSendChatEvent(IChatComponent message)
    {
        this.message = message;
    }

    public IChatComponent getMessage()
    {
        return message;
    }

    public void setMessage(IChatComponent message)
    {
        this.message = message;
    }

}
