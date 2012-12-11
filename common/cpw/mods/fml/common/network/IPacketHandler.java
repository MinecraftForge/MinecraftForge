package cpw.mods.fml.common.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public interface IPacketHandler
{
    /**
     * Recieve a packet from one of the registered channels for this packet handler
     * @param manager The network manager this packet arrived from
     * @param packet The packet itself
     * @param player A dummy interface representing the player - it can be cast into a real player instance if needed
     */
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player);
}
