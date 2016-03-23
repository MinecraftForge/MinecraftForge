package net.minecraftforge.client.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * ClientChatEvent is fired when the user enters a massage in their chat box. <br>
 * This is a good place to catch chat before it is sent to the server. <br>
 * <br>
 * If you are modifying the chat, simply use setMessage(message) <br>
 * <br>
 * If you need to send more than one message you can use: <br>
 * <code>getSender().sendChatMessage(message)</code> <br>
 * to send extra message. (note: there is no risk of recursive events)<br>
 * <br>
 * To stop the message form being sent call <code>setMessage("" or null)</code> or <code>setCanceled(true)</code> <br>
 * <br>
 * note: if you want client side commands you should use ClientCommandHandler instead
 */
@Cancelable
public class ClientChatEvent extends Event
{

    private String message;
    private final EntityPlayerSP sender;

    public ClientChatEvent(EntityPlayerSP player, String msg)
    {
        sender = player;
        message = msg;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String msg)
    {
        message = msg;
    }

    public EntityPlayerSP getSender()
    {
        return sender;
    }
}
