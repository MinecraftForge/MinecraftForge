package net.minecraftforge.client.event;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ClientChatReceivedEvent extends Event
{
    public IChatComponent message;
    /**
     * Introduced in 1.8:
     * 0 : Standard Text Message
     * 1 : 'System' message, displayed as standard text.
     * 2 : 'Status' message, displayed above action bar, where song notifications are.
     */
    public final byte type;
    public ClientChatReceivedEvent(byte type, IChatComponent message)
    {
        this.type = type;
        this.message = message;
    }
}
