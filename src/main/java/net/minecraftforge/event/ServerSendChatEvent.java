package net.minecraftforge.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * ServerSendChatEvent is fired whenever a chat message is sent by the server (e.g. with the /say command)
 * Fired via {@link ForgeEventFactory#serverSendChat} which is called from {@link net.minecraft.server.management.PlayerList#sendChatMsg}
 *
 * {@link #component} contains the message to be sent by the server
 *
 * This event is {@link Cancelable}
 * This event does not have a result
 */
@Cancelable
public class ServerSendChatEvent extends Event
{

    private ITextComponent component;

    public ServerSendChatEvent(ITextComponent component)
    {
        this.component = component;
    }

    public ITextComponent getComponent()
    {
        return component;
    }

    public void setComponent(ITextComponent component)
    {
        this.component = component;
    }

}
