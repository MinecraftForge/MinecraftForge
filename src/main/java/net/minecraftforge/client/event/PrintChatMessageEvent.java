package net.minecraftforge.client.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired whenever a message is printed to the chat in {@link net.minecraft.client.gui.GuiNewChat#printChatMessageWithOptionalDeletion(ITextComponent, int)}
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * This event is {@link Cancelable}
 *
 * {@link #message} contains the message that is printed to the chat (may not be null)
 * {@link #chatLineId} contains the id for optional message deletion (default is 0)
 */
@Cancelable
public class PrintChatMessageEvent extends Event
{

    private ITextComponent message;
    private int chatLineId;

    public PrintChatMessageEvent(ITextComponent message, int chatLineId)
    {
        this.message = message;
        this.chatLineId = chatLineId;
    }

    public ITextComponent getMessage()
    {
        return message;
    }

    public void setMessage(ITextComponent message)
    {
        this.message = message;
    }

    public int getChatLineId()
    {
        return chatLineId;
    }

    public void setChatLineId(int chatLineId)
    {
        this.chatLineId = chatLineId;
    }

}
