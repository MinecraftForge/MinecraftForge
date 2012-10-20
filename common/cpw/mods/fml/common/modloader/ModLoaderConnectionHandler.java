package cpw.mods.fml.common.modloader;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet1Login;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ModLoaderConnectionHandler implements IConnectionHandler
{
    private BaseModProxy mod;

    public ModLoaderConnectionHandler(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
    {
        mod.onClientLogin((EntityPlayer)player);
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
    {
        ModLoaderHelper.sidedHelper.clientConnectionOpened(netClientHandler, manager, mod);
    }

    @Override
    public void connectionClosed(INetworkManager manager)
    {
        if (!ModLoaderHelper.sidedHelper.clientConnectionClosed(manager, mod))
        {
            mod.serverDisconnect();
            mod.onClientLogout(manager);
        }
    }

    @Override
    public void clientLoggedIn(NetHandler nh, INetworkManager manager, Packet1Login login)
    {
        mod.serverConnect(nh);
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
    {
        ModLoaderHelper.sidedHelper.clientConnectionOpened(netClientHandler, manager, mod);
    }

}
