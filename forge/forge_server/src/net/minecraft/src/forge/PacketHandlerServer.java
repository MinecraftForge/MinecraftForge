package net.minecraft.src.forge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraft.src.forge.packets.*;

public class PacketHandlerServer implements IPacketHandler
{
    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] bytes) 
    {
        NetServerHandler net = (NetServerHandler)network.getNetHandler();
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
        ForgePacket pkt = null;
        
        try
        {
            int packetID = data.read();
            switch(packetID)
            {
                case ForgePacket.MODLIST:
                    pkt = new PacketModList(true);
                    pkt.readData(data);
                    onModListResponse(net, (PacketModList)pkt);
                    break;
            }
        }
        catch(IOException e)
        {
            ModLoader.getLogger().log(Level.SEVERE, "Exception in PacketHandlerServer.onPacketData", e);
            e.printStackTrace();
        }
    }

    private void onModListResponse(NetServerHandler net, PacketModList pkt) throws IOException 
    {
        if (pkt.Length < 0)
        {
            net.kickPlayer("Invalid mod list response, Size: " + pkt.Length);
            return;
        }        
        if (pkt.Mods.length == 0)
        {
            ModLoader.getLogger().log(Level.INFO, net.getUsername() + " joined with no mods");
        }
        else
        {
            ModLoader.getLogger().log(Level.INFO, net.getUsername() + " joined with: " + Arrays.toString(pkt.Mods).replaceAll("mod_", ""));
        }
        
        //TODO: Write a 'banned mods' system and do the checks here
        
        NetworkMod[] serverMods = MinecraftForge.getNetworkMods();
        ArrayList<NetworkMod> missing = new ArrayList<NetworkMod>();
        for (NetworkMod mod : serverMods)
        {
            if (!mod.clientSideRequired())
            {
                continue;
            }
            boolean found = true;
            for (String modName : pkt.Mods)
            {
                if (modName.equals(mod.toString()))
                {
                    found = false;
                    break;
                }
            }
            if (!found)
            {
                missing.add(mod);
            }
        }
        if (missing.size() > 0)
        {
            doMissingMods(net, missing);
        }
        else
        {
            sendModIDs(net, serverMods);
        }
    }
    
    /**
     * Sends the user a list of mods they are missing and then disconnects them
     * @param net The network handler
     */
    private void doMissingMods(NetServerHandler net, ArrayList<NetworkMod> list)
    {
        PacketMissingMods pkt = new PacketMissingMods(true);
        pkt.Mods = new String[list.size()];
        int x = 0;
        for(NetworkMod mod : list)
        {
            pkt.Mods[x++] = mod.toString();
        }
        net.sendPacket(pkt.getPacket());
        disconnectUser(net);
    }
    

    /**
     * Sends a list of mod id mappings to the client.
     * Only mod ID's are sent, not item or blocks.
     * 
     * @param net The network handler
     * @param list A list of network mods
     */
    private void sendModIDs(NetServerHandler net, NetworkMod[] list)
    {
        PacketModIDs pkt = new PacketModIDs();
        for (NetworkMod mod : list)
        {
            pkt.Mods.put(MinecraftForge.getModID(mod), mod.toString());
        }
        net.sendPacket(pkt.getPacket());
    }
    
    /**
     * Disconnects the player just like kicking them, just without the kick message.
     * @param net The network handler
     */
    private void disconnectUser(NetServerHandler net)
    {
        MinecraftServer mc = ModLoader.getMinecraftServerInstance();
        net.getPlayerEntity().func_30002_A();
        net.netManager.serverShutdown();
        mc.configManager.sendPacketToAllPlayers(new Packet3Chat("\247e" + net.getUsername() + " left the game."));
        mc.configManager.playerLoggedOut(net.getPlayerEntity());
        net.connectionClosed = true;
    }
}
