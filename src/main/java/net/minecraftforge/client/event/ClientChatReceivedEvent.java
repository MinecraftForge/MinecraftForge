package net.minecraftforge.client.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ClientChatReceivedEvent extends Event
{
    private ITextComponent message;
    private final byte type;
    public ClientChatReceivedEvent(byte type, ITextComponent message)
    {
        this.type = type;
        this.setMessage(message);
    }

    public ITextComponent getMessage()
    {
        return message;
    }

    public void setMessage(ITextComponent message)
    {
        this.message = message;
    }

    /**
     * Introduced in 1.8:
     * 0 : Standard Text Message
     * 1 : 'System' message, displayed as standard text.
     * 2 : 'Status' message, displayed above action bar, where song notifications are.
     */
    public byte getType()
    {
        return type;
    }
}
