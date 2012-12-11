package cpw.mods.fml.common.modloader;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ModLoaderPacketHandler implements IPacketHandler
{
    private BaseModProxy mod;

    public ModLoaderPacketHandler(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        if (player instanceof EntityPlayerMP)
        {
            mod.serverCustomPayload(((EntityPlayerMP)player).field_71135_a, packet);
        }
        else
        {
            ModLoaderHelper.sidedHelper.sendClientPacket(mod, packet);
        }
    }

}
