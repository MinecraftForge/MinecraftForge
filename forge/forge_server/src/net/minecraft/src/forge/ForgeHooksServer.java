package net.minecraft.src.forge;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import net.minecraft.src.*;
import net.minecraft.src.forge.packets.ForgePacket;
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
    
    public static void handleLoginPacket(Packet1Login pktLogin, NetServerHandler net, NetworkManager manager)
    {
        init();
        if (pktLogin.serverMode == ForgePacket.FORGE_ID)
        {
            ForgeHooks.onLogin(manager, pktLogin);  
            
            String[] channels = MessageManager.getInstance().getRegisteredChannels(manager);
            StringBuilder tmp = new StringBuilder();
            tmp.append("Forge");
            for(String channel : channels)
            {
                tmp.append("\0");
                tmp.append(channel);
            }
            Packet250CustomPayload pkt = new Packet250CustomPayload(); 
            pkt.channel = "REGISTER";
            try {
                pkt.data = tmp.toString().getBytes("UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pkt.length = pkt.data.length;
            net.sendPacket(pkt);
            ForgeHooksServer.sendModListRequest(manager);
        }
        else
        {
            net.kickPlayer(mod_MinecraftForge.NO_FORGE_KICK_MESSAGE);
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
