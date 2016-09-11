package net.minecraftforge.client.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * ClientChatPrintedEvent is posted right <i>before</i> a chat message is added to the player's chat GUI.
 * Canceling the event will prevent it from being added to the GUI or logged.
 *
 * It is posted in {@link net.minecraft.client.gui.GuiNewChat#printChatMessageWithOptionalDeletion(ITextComponent, int)}
 */
@Cancelable
public class ClientChatPrintedEvent extends Event
{
    private ITextComponent message;
    private int chatLineID;

    public ClientChatPrintedEvent(ITextComponent message, int chatLineID)
    {
        this.message = message;
        this.chatLineID = chatLineID;
    }

    public ITextComponent getMessage()
    {
        return message;
    }

    public void setMessage(ITextComponent message)
    {
        this.message = message;
    }

    public int getChatLineID()
    {
        return chatLineID;
    }
}
