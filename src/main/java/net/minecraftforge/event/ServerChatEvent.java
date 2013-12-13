package net.minecraftforge.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;

@Cancelable
public class ServerChatEvent extends Event
{
    public final String message, username;
    public final EntityPlayerMP player;
    public ChatMessageComponent component;
    public ServerChatEvent(EntityPlayerMP player, String message, ChatMessageComponent component)
    {
        super();
        this.message = message;
        this.player = player;
        this.username = player.func_146103_bH().getName();
        this.component = component;
    }
}
