package net.minecraft.src.forge;

import java.util.LinkedList;
import java.util.Map;

import net.minecraft.src.*;
import net.minecraft.src.forge.packets.PacketModList;

public class ForgeHooksServer
{
    /**
     * Called when a Entity is being added to a EntityTracker.
     * If we have valid info, register the entity.
     *
     * @param tracker The EntityTracker instance
     * @param entity The Entity to add
     * @return True if we registered the Entity
     */
    public static boolean OnTrackEntity(EntityTracker tracker, Entity entity)
    {
        EntityTrackerInfo info = MinecraftForge.getEntityTrackerInfo(entity, true);
        if (info != null)
        {
            tracker.trackEntity(entity, info.Range, info.UpdateFrequency, info.SendVelocityInfo);
            return true;
        }
        return false;
    }
    
    public static String onChatMessage(String message, String username)
    {
      	for (IChatHandler handler : chatHandlers)
       	{
      	    message = handler.processChat(message, username);
      	    if(message == null)
      	        return null;
       	}
       	return message;
    }
    static LinkedList<IChatHandler> chatHandlers = new LinkedList<IChatHandler>();

    public static void sendModListRequest(NetworkManager net)
    {
        NetworkMod[] list = MinecraftForge.getNetworkMods();
        PacketModList pkt = new PacketModList(true);
        
        for (NetworkMod mod : list)
        {
            pkt.ModIDs.put(MinecraftForge.getModID(mod), mod.toString());
        }
        
        ((NetServerHandler)net.getNetHandler()).sendPacket(pkt.getPacket());
        if (((PacketHandlerServer)ForgeHooks.getPacketHandler()).DEBUG)
        {
            System.out.println("S->C: " + pkt.toString(true));
        }
    }


    private static boolean hasInit = false;
    public static void init()
    {
        if (hasInit)
        {
            return;
        }
        hasInit = true;
        ForgeHooks.setPacketHandler(new PacketHandlerServer());
    }

    static
    {
        init();
    }
}
