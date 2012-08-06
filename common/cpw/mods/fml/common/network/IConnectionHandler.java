package cpw.mods.fml.common.network;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;

public interface IConnectionHandler
{
    void playerLoggedIn(Player player, NetHandler netHandler, NetworkManager manager);

}
