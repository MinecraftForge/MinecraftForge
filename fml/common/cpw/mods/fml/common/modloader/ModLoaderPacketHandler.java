package cpw.mods.fml.common.modloader;

import net.minecraft.src.BaseMod;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ModLoaderPacketHandler implements IPacketHandler
{
    private BaseMod mod;

    public ModLoaderPacketHandler(BaseMod mod)
    {
        this.mod = mod;
    }

    @Override
    public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        mod.onPacket250Received((EntityPlayer) player, packet);
    }

}
