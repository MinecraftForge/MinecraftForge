package net.minecraftforge.client.event;

import net.minecraft.util.IChatComponent;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class ClientChatSendEvent extends Event
{
    public String message;
    public ClientChatSendEvent(String msg)
    {
        this.message = msg;
    }
}