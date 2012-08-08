package cpw.mods.fml.common.network;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;

public interface IConnectionHandler
{
    /**
     * Called when a player logs into the server
     * 
     * @param player
     * @param netHandler
     * @param manager
     */
    void playerLoggedIn(Player player, NetHandler netHandler, NetworkManager manager);

    /**
     * If you don't want the connection to continue, return a non-empty string here
     * If you do, you can do other stuff here- note no FML negotiation has occured yet
     * though the client is verified as having FML installed
     * 
     * @param netHandler
     * @param manager
     * @return
     */
    String connectionReceived(NetLoginHandler netHandler, NetworkManager manager);

}
