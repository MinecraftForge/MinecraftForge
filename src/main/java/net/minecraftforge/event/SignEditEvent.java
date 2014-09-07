package net.minecraftforge.event;

import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * SignEditEvent is fired whenever a C12PacketUpdateSign is processed. <br>
 * This event is fired via {@link ForgeHooks#onSignEditEvent(NetHandlerPlayServer, C12PacketUpdateSign)}, 
 * which is executed by the NetHandlerPlayServer#processUpdateSign(net.minecraft.network.play.client.C12PacketUpdateSign)<br>
 * <br>
 * {@link #text} contains the text being written.<br>
 * {@link #player} the instance of EntityPlayerMP for the player sending the chat message.<br>
 * {@link #x} contains the x-coordinate of the sign being edited.
 * {@link #y} contains the y-coordinate of the sign being edited.
 * {@link #z} contains the z-coordinate of the sign being edited.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message is never distributed to all clients.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/

@Cancelable
public class SignEditEvent extends Event
{
    public final int x, y, z;
    public final String[] text;
    public final EntityPlayerMP editor;
    
    public SignEditEvent(int x, int y, int z, String[] text, EntityPlayerMP editor)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
        this.text = text;
        this.editor = editor;
    }
}