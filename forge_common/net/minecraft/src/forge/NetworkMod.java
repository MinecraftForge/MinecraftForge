package net.minecraft.src.forge;

import net.minecraft.src.BaseMod;
import net.minecraft.src.NetworkManager;

/**
 * This is for mods that are designed to be used on the server side alone,
 * or both the client and server side. This class is used when registering
 * various things relating to network traffic. Entity ID's, GUI Id's, etc..
 */
public abstract class NetworkMod extends BaseMod
{
    /**
     * Returns true if every client is required to have this
     * mod installed when it is installed on a server.
     * @return True if client is required, false if not.
     */
    public boolean clientSideRequired()
    {
        return false;
    }

    /**
     * Returns true if the server is required to have this
     * mod when it is installed on the client.
     * @return True if server is required, false if not.
     */
    public boolean serverSideRequired()
    {
        return false;
    }
    
    /**
     * Called when the 'small' data packet is received for this NetworkMod,
     * Internally, this utilizes the Packet131MapData packet. This is useful 
     * data that is sent rapidly and would like to save the overhead of the 
     * 250 custom payload packet.
     * 
     * Data is limited to 256 bytes.
     * 
     * @param net
     * @param id
     * @param data
     */
    public void onPacketData(NetworkManager net, short id, byte[] data){}
}
