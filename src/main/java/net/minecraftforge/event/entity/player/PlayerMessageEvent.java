package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.event.Cancelable;

/**
 * This event is fired before the join or leave message is sent.
 * It can be used to change the {@link ChatMessageComponent} that is sent to the client.
 * 
 * Note: Canceling one of these events does NOT prevent the player from logging in.
 * It does prevent the message from being sent.
 * 
 * @author jk-5
 */
public class PlayerMessageEvent extends PlayerEvent
{
    public ChatMessageComponent message;
    
    public PlayerMessageEvent(EntityPlayer player, ChatMessageComponent message)
    {
        super(player);
        this.message = message;
    }
    
    @Cancelable
    public static class JoinServer extends PlayerMessageEvent
    {
        public JoinServer(EntityPlayer player, ChatMessageComponent message)
        {
            super(player, message);
        }
    }
    
    @Cancelable
    public static class LeaveServer extends PlayerMessageEvent
    {
        public LeaveServer(EntityPlayer player, ChatMessageComponent message)
        {
            super(player, message);
        }
    }
}
