package net.minecraftforge.event.entity.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * ClientChatEvent is fired when the user enters a massage in there chat box. <br>
 * This is a good place to catch chat before it is sent to the server. <br>
 * <br>
 * If you are modifying the chat, simply cancel the event and use: <br>
 * event.sender.sendChatMessage(message); <br>
 * to send the modified message. <br>
 * <br>
 * note: if you want client side commands you should use ClientCommandHandler instead
 */
public class ClientChatEvent extends PlayerEvent
{

    public String message;
    public EntityPlayerSP sender;

    public ClientChatEvent(EntityPlayerSP player, String msg)
    {
        super(player);
        sender = player;
        message = msg;
    }

    public boolean isCancelable()
    {
        return true;
    }
}
