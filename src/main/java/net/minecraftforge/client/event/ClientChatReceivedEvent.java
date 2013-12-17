package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class ClientChatReceivedEvent extends Event
{
    public String message;
    public ClientChatReceivedEvent(String message)
    {
        this.message = message;
    }
}
