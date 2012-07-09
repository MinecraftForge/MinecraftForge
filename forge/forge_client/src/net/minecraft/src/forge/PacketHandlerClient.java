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

public class PacketHandlerClient extends PacketHandlerBase
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
            switch (packetID)
            {
                case ForgePacket.SPAWN:
                    pkt = new PacketEntitySpawn();
                    pkt.readData(data);
                    onEntitySpawnPacket((PacketEntitySpawn)pkt, data, ModLoader.getMinecraftInstance().theWorld);
                    break;

                case ForgePacket.MODLIST:
                    pkt = new PacketModList(false);
                    pkt.readData(data);
                    onModListCheck(net, (PacketModList)pkt);
                    break;

                case ForgePacket.MOD_MISSING:
                    pkt = new PacketMissingMods(false);
                    pkt.readData(data);
                    onMissingMods((PacketMissingMods)pkt, net);
                    break;

                case ForgePacket.OPEN_GUI:
                    pkt = new PacketOpenGUI();
                    pkt.readData(data);
                    onOpenGui((PacketOpenGUI)pkt);
                    break;

                case ForgePacket.TRACK:
                    pkt = new PacketEntityTrack();
                    pkt.readData(data);
                    onEntityTrackPacket((PacketEntityTrack)pkt, ModLoader.getMinecraftInstance().theWorld);
                    break;
            }
        }
        catch (IOException e)
        {
            ModLoader.getLogger().log(Level.SEVERE, "Exception in PacketHandlerClient.onPacketData", e);
            e.printStackTrace();
        }
    }

    /**
     * Process the Entity Track packet. This corrects the serverPos[XYZ] to match
     *  with the server, so any following relative updates are valid.
     * @param packet The Track Packet
     * @param world The world the entity is in
     */
    public void onEntityTrackPacket(PacketEntityTrack packet, World world)
    {
        if (DEBUG)
        {
            System.out.println("S->C: " + packet.toString(true));
        }

        Minecraft mc = ModLoader.getMinecraftInstance();
        Entity entity = ((WorldClient)world).getEntityByID(packet.entityId);
        if (entity == null)
        {
            return;
        }

        entity.serverPosX = packet.serverPosX;
        entity.serverPosY = packet.serverPosY;
        entity.serverPosZ = packet.serverPosZ;
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
        if (DEBUG)
        {
            System.out.println("S->C: " + packet.toString(true));
        }
        Class cls = MinecraftForge.getEntityClass(packet.modID, packet.typeID);
        if (cls == null)
        {
            System.out.println("Could not find entity info for " + Integer.toHexString(packet.modID) + " : " + packet.typeID);
            return;
        }

        double posX = (double)packet.posX / 32D;
        double posY = (double)packet.posY / 32D;
        double posZ = (double)packet.posZ / 32D;
        float  yaw     = (float)(packet.yaw * 360) / 256.0F;
        float  pitch   = (float)(packet.pitch * 360) / 256.0F;
        float  yawHead = (float)(packet.yawHead * 360) / 256.0F;
        try
        {
            Entity entity = (Entity)(cls.getConstructor(World.class).newInstance(world));
            if (entity instanceof IThrowableEntity)
            {
                Minecraft mc = ModLoader.getMinecraftInstance();
                Entity thrower = (mc.thePlayer.entityId == packet.throwerID ? mc.thePlayer : ((WorldClient)world).getEntityByID(packet.throwerID));
                ((IThrowableEntity)entity).setThrower(thrower);
            }

            entity.serverPosX = packet.posX;
            entity.serverPosY = packet.posY;
            entity.serverPosZ = packet.posZ;

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
            entity.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
            
            if (entity instanceof EntityLiving)
            {
            	((EntityLiving)entity).rotationYawHead = yawHead;
            }
            
            if (packet.metadata != null)
            {
                entity.getDataWatcher().updateWatchedObjectsFromList((List)packet.metadata);
            }

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
            ModLoader.throwException(String.format("Error spawning entity of type %d for %s.", packet.typeID, MinecraftForge.getModByID(packet.modID)), e);
        }
    }

    /**
     * Sets up the list of ID to mod mappings.
     * TODO; Make it display an error, and prompt if the user wishes to continue anyways
     * if it detects that the server does not have a corresponding mod to one it has installed.
     * 
     * Sends a list of all loaded mods to the server.
     * For now, it it simple a String[] of mod.toString()
     * 
     * @param network The network connection to send the packet on.
     * @param packet The Server to client packet containing a list of NetworkMod ID's
     */
    private void onModListCheck(NetClientHandler net, PacketModList packet)
    {
        if (DEBUG)
        {
            System.out.println("S->C: " + packet.toString(true));
        }
        
        ForgeHooksClient.enable4096 = packet.has4096;

        ForgeHooks.networkMods.clear();
        NetworkMod[] mods = MinecraftForge.getNetworkMods();
        for (NetworkMod mod : mods)
        {
            for (Entry<Integer, String> entry : packet.ModIDs.entrySet())
            {
                if (mod.toString().equals(entry.getValue()))
                {
                    ForgeHooks.networkMods.put(entry.getKey(), mod);
                }
            }
        }
        ArrayList<NetworkMod> missing = new ArrayList<NetworkMod>();
        for (NetworkMod mod : mods)
        {
            if (MinecraftForge.getModID(mod) == -1 && mod.serverSideRequired())
            {
                missing.add(mod);
            }
        }
        //TODO: Display error/confirmation screen
        
        PacketModList pkt = new PacketModList(false);
        pkt.Mods = new String[ModLoader.getLoadedMods().size()];
        int x = 0;
        for (BaseMod mod : (List<BaseMod>)ModLoader.getLoadedMods())
        {
            pkt.Mods[x++] = mod.toString();
        }
        net.addToSendQueue(pkt.getPacket());
        if (DEBUG)
        {
            System.out.println("C->S: " + pkt.toString(true));
        }
    }

    /**
     * Received when the client does not have a mod installed that the server requires them to.
     * Displays a informative screen, and disconnects from the server.
     *
     * @param pkt The missing mods packet
     * @param net The network handler
     */
    private void onMissingMods(PacketMissingMods pkt, NetClientHandler net)
    {
        if (DEBUG)
        {
            System.out.println("S->C: " + pkt.toString(true));
        }
        net.disconnect();
        Minecraft mc = ModLoader.getMinecraftInstance();
        mc.changeWorld1(null);
        mc.displayGuiScreen(new GuiMissingMods(pkt));
    }

    /**
     * Handles opening the Gui for the player.
     *
     * @param pkt The Open Gui Packet
     */
    private void onOpenGui(PacketOpenGUI pkt)
    {
        if (DEBUG)
        {
            System.out.println("S->C: " + pkt.toString(true));
        }
        NetworkMod mod = MinecraftForge.getModByID(pkt.ModID);
        if (mod != null)
        {
            EntityPlayerSP player = (EntityPlayerSP)ModLoader.getMinecraftInstance().thePlayer;
            player.openGui(mod, pkt.GuiID, player.worldObj, pkt.X, pkt.Y, pkt.Z);
            player.craftingInventory.windowId = pkt.WindowID;
        }
    }

    @Override
    public void sendPacket(NetworkManager network, Packet packet) 
    {
        NetClientHandler net = (NetClientHandler)network.getNetHandler();
        net.addToSendQueue(packet);
    }
}
