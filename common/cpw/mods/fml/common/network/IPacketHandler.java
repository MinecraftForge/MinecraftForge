package cpw.mods.fml.common.network;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;

public interface IPacketHandler
{
    public void onPacketData(NetworkManager manager, Packet250CustomPayload packet);
}
