package net.minecraft.src.forge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.forge.packets.*;

public class PacketHandlerClient implements IPacketHandler
{
    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] bytes) 
    {        
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
        try
        {
            ForgePacket pkt = null;
            NetClientHandler net = (NetClientHandler)network.getNetHandler();
            
            int packetID = data.read();
            switch(packetID)
            {
                case ForgePacket.SPAWN:
                    pkt = new PacketEntitySpawn();
                    pkt.readData(data);
                    onEntitySpawnPacket((PacketEntitySpawn)pkt, data, ModLoader.getMinecraftInstance().theWorld);
                    break;
            }
        }
        catch(IOException e)
        {
            ModLoader.getLogger().log(Level.SEVERE, "Exception in PacketHandlerClient.onPacketData", e);
            e.printStackTrace();
        }
    }

    /**
     * Processes the Entity Spawn packet. And spawns an entity in world as needed.
     * If the client has the required client mod.
     * @param packet The Spawn Packet
     * @param data A stream holding extra data for the entity to read
     * @param world The world to spawn the entity in
     */
    public void onEntitySpawnPacket(PacketEntitySpawn packet, DataInputStream data, World world)
    {
        Class cls = MinecraftForge.getEntityClass(packet.modID, packet.typeID);
        if (cls == null)
        {
            System.out.println("Could not find entity info for " + Integer.toHexString(packet.modID) + " : " + packet.typeID);
            return;
        }

        double posX = (double)packet.posX / 32D;
        double posY = (double)packet.posY / 32D;
        double posZ = (double)packet.posZ / 32D;
        try 
        {
            Entity entity = (Entity)(cls.getConstructor(World.class, double.class, double.class, double.class).newInstance(world, posX, posY, posZ));
            if (entity instanceof IThrowableEntity)
            {
                Minecraft mc = ModLoader.getMinecraftInstance();
                Entity thrower = (mc.thePlayer.entityId == packet.throwerID ? mc.thePlayer : ((WorldClient)world).getEntityByID(packet.throwerID));
                ((IThrowableEntity)entity).setThrower(thrower);
            }

            entity.serverPosX    = packet.posX;
            entity.serverPosY    = packet.posX;
            entity.serverPosZ    = packet.posZ;
            entity.rotationYaw   = 0.0F;
            entity.rotationPitch = 0.0F;
            
            Entity parts[] = entity.getParts();
            if (parts != null)
            {
                int i = packet.entityID - entity.entityId;
                for (int j = 0; j < parts.length; j++)
                {
                    parts[j].entityId += i;
                }
            }
            
            entity.entityId = packet.entityID;
            
            if (packet.throwerID > 0)
            {
                entity.setVelocity(packet.speedX / 8000D, packet.speedY / 8000D, packet.speedZ / 8000D);
            }
            
            if (entity instanceof ISpawnHandler)
            {
                ((ISpawnHandler)entity).readSpawnData(data);
            }
            
            ((WorldClient)world).addEntityToWorld(packet.entityID, entity);            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            ModLoader.getLogger().throwing("ForgeHooksClient", "onEntitySpawnPacket", e);
            ModLoader.ThrowException(String.format("Error spawning entity of type %d for %s.", packet.typeID, MinecraftForge.getModByID(packet.modID)), e);
        }
    }

    /**
     * Sends a list of all loaded mods to the server.
     * For now, it it simple a String[] of mod.toString()
     * @param network The network connection to send the packet on.
     */
    private void onModListCheck(NetClientHandler net)
    {
        PacketModList pkt = new PacketModList(false);
        pkt.Mods = new String[ModLoader.getLoadedMods().size()];
        int x = 0;
        for(BaseMod mod : (List<BaseMod>)ModLoader.getLoadedMods())
        {
            pkt.Mods[x++] = mod.toString(); 
        }
        net.addToSendQueue(pkt.getPacket());
    }
}
