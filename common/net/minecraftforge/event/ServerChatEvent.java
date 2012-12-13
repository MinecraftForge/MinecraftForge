package net.minecraftforge.event;

import net.minecraft.entity.player.EntityPlayerMP;

@Cancelable
public class ServerChatEvent extends Event {
    public final String message, username;
    public final EntityPlayerMP player;
    public String line;
    public ServerChatEvent(EntityPlayerMP player, String message, String line)
    {
        super();
        this.message = message;
        this.player = player;
        this.username = player.username;
        this.line = "<" + username + "> " + message;
    }
}
