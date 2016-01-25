package net.minecraftforge.event;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ServerSendChatEvent extends Event
{
	
	private IChatComponent message;
	
	public ServerSendChatEvent(IChatComponent message)
	{
		this.message = message;
	}
	
	public IChatComponent getMessage()
	{
		return message;
	}
	
	public void setMessage(IChatComponent message)
	{
		this.message = message;
	}

}
