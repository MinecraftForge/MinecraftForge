package net.minecraftforge.event;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when a chat message is sent from the server (e.g. using the /say command).
 * Fired from {@link net.minecraft.server.management.ServerConfigurationManager#sendChatMsg}.
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
