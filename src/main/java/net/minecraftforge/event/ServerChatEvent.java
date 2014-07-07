package net.minecraftforge.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

/**
 * ServerChatEvent is fired whenever a C01PacketChatMessage is processed. <br>
 * This event is fired via {@link ForgeHooks#onServerChatEvent(net.minecraft.network.NetHandlerPlayServer, String, ChatComponentTranslation)}, 
 * which is executed by the NetHandlerPlayServer#processChatMessage(net.minecraft.network.play.client.C01PacketChatMessage)<br>
 * <br>
 * {@link #username} contains the username of the player sending the chat message.<br>
 * {@link #message} contains the message being sent.<br>
 * {@link #player} the instance of EntityPlayerMP for the player sending the chat message.<br>
 * {@link #component} contains the instance of ChatComponentTranslation for the sent message.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message is never distributed to all clients.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
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
