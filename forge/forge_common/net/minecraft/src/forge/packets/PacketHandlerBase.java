package net.minecraft.src.forge.packets;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.forge.IPacketHandler;

/**
 * A helper class used to make a shared interface for sending packets,
 * Should not be used outside the API itself.
 */
public abstract class PacketHandlerBase implements IPacketHandler 
{   
    public static boolean DEBUG = false;
    
    /**
     * Sends out a packet to the specified network manager.
     * This is necessary because NetClientHandler, and 
     * NetServerHandler are not on both sides.
     * 
     * @param network The manager to send the packet to
     * @param packet The packet to send
     */
    public abstract void sendPacket(NetworkManager network, Packet packet);
}
