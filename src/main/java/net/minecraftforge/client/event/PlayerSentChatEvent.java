 
package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class PlayerSentChatEvent extends Event
    public String message;
{
    public PlayerSentChatEvent(String message)
    {
        this.message = message;
    }
}
