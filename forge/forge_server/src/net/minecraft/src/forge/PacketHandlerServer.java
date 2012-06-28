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

import cpw.mods.fml.server.FMLServerHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraft.src.forge.packets.*;

public class PacketHandlerServer extends PacketHandlerBase
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
            switch (packetID)
            {
                case ForgePacket.MODLIST:
                    pkt = new PacketModList(true);
                    pkt.readData(data);
                    onModListResponse(net, (PacketModList)pkt);
                    break;
            }
        }
        catch (IOException e)
        {
            ModLoader.getLogger().log(Level.SEVERE, "Exception in PacketHandlerServer.onPacketData", e);
            e.printStackTrace();
        }
    }

    private void onModListResponse(NetServerHandler net, PacketModList pkt) throws IOException
    {
        if (DEBUG)
        {
            System.out.println("C->S: " + pkt.toString(true));
        }
        if (pkt.Length < 0)
        {
            net.kickPlayer("Invalid mod list response, Size: " + pkt.Length);
            return;
        }
        if (!pkt.has4096)
        {
            net.kickPlayer("Must have Forge build #136+ (4096 fix) to connect to this server");
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
            boolean found = false;
            for (String modName : pkt.Mods)
            {
                if (modName.equals(mod.toString()))
                {
                    found = true;
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
            finishLogin(net);
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
        for (NetworkMod mod : list)
        {
            pkt.Mods[x++] = mod.toString();
        }
        if (DEBUG)
        {
            System.out.println("S->C: " + pkt.toString(true));
        }
        net.sendPacket(pkt.getPacket());
        disconnectUser(net);
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
    
    private void finishLogin(NetServerHandler net)
    {
        EntityPlayerMP player = net.getPlayerEntity();
        WorldServer world = net.mcServer.getWorldManager(player.dimension);
        ChunkCoordinates spawn = world.getSpawnPoint();
        
        net.sendPacket(new Packet1Login("", player.entityId, world.getWorldInfo().getTerrainType(), 
                player.itemInWorldManager.getGameType(), world.worldProvider.worldType, 
                (byte)world.difficultySetting,          (byte)world.getHeight(), 
                (byte)net.mcServer.configManager.getMaxPlayers()));
        
        net.sendPacket(new Packet6SpawnPosition(spawn.posX, spawn.posY, spawn.posZ));
        net.sendPacket(new Packet202PlayerAbilities(player.capabilities));
        net.mcServer.configManager.updateTimeAndWeather(player, world);
        net.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + player.username + " joined the game."));
        net.mcServer.configManager.playerLoggedIn(player);
        
        net.teleportTo(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        net.sendPacket(new Packet4UpdateTime(world.getWorldTime()));
        
        for (Object efx : player.getActivePotionEffects())
        {
            net.sendPacket(new Packet41EntityEffect(player.entityId, (PotionEffect)efx));
        }
        
        player.func_20057_k();
        FMLServerHandler.instance().announceLogin(player);
    }

    @Override
    public void sendPacket(NetworkManager network, Packet packet) 
    {
        NetServerHandler net = (NetServerHandler)network.getNetHandler();
        net.sendPacket(packet);
    }
}
