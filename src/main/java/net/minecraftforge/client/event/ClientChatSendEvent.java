package net.minecraftforge.client.event;

import net.minecraft.util.IChatComponent;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class ClientChatSendEvent extends Event
{
    public String message;
    public ClientChatSendEvent(String p_146403_1_)
    {
        this.message = p_146403_1_;
    }
}