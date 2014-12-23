package net.minecraftforge.event.entity.player;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientPlayerChatEvent extends cpw.mods.fml.common.eventhandler.Event 
{
    public String message;
    public ClientPlayerChatEvent(String message)
    {
        this.message=message;
    }
}
