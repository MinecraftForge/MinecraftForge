package net.minecraft.src.forge;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;

public interface IConnectionHandler
{
    /**
     * Raised when a Client successfully connects it's socket to the Server.
     * @param network The new NetworkManager associated with this connection.
     */
    public void onConnect(NetworkManager network);

    /**
     * Raised when you receive a Packet1Login.
     * On the server, it is raised after the NetHandler is switched, and the
     * initial user placement/info packets are sent.
     *
     * On the client, this is raised after the packet is parsed, and the user
     * is sitting at the 'Downloading Terrain' screen.
     *
     * @param network The NetoworkManager associated with this connection.
     * @param login The login packet
     */
    public void onLogin(NetworkManager network, Packet1Login login);

    /**
     * Raised whenever the socket is closed, can be caused by various reasons.
     *
     * @param network The NetworkManager associated with this connection.
     * @param message The translated message to be displayed for this disconnection.
     * @param args Any additional arguments that the code may of provided.
     *   Sometimes this is further explanation, or a Throwable, in the case of errors.
     */
    public void onDisconnect(NetworkManager network, String message, Object[] args);
}
