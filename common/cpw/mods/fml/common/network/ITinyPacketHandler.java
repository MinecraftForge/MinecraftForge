package cpw.mods.fml.common.network;

import net.minecraft.network.packet.*;

public interface ITinyPacketHandler
{
    void handle(NetHandler handler, Packet131MapData mapData);
}
