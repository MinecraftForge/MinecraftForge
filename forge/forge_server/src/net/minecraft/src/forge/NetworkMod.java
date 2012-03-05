package net.minecraft.src.forge;

import net.minecraft.src.BaseModMp;

/**
 * This is for mods that are designed to be used on the server side alone,
 * or both the client and server side. This class is used when registering
 * various things relating to network traffic. Entity ID's, GUI Id's, etc..
 */
public abstract class NetworkMod extends BaseModMp
{
    /**
     * Returns true if every client is required to have this
     * mod installed when it is installed on a server.
     * @return True if client is required, false if not.
     */
    public abstract boolean clientSideRequired();

    /**
     * Returns true if the server is required to have this
     * mod when it is installed on the client.
     * @return True if server is required, false if not.
     */
    public abstract boolean serverSideRequired();
}
