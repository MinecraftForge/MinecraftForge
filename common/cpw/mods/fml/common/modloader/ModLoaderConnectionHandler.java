package cpw.mods.fml.common.modloader;

import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ModLoaderConnectionHandler implements IConnectionHandler
{

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, NetworkManager manager)
    {

    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, NetworkManager manager)
    {
        return null;
    }

}
