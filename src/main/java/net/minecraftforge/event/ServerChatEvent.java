package net.minecraftforge.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

@Cancelable
public class ServerChatEvent extends Event
{
    public final String message, username;
    public final EntityPlayerMP player;
    public ChatComponentTranslation component;
    public ServerChatEvent(EntityPlayerMP player, String message, ChatComponentTranslation component)
    {
        super();
        this.message = message;
        this.player = player;
        this.username = player.getGameProfile().getName();
        this.component = component;
    }
}
