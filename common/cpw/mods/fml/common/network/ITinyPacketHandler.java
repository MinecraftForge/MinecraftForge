package cpw.mods.fml.common.network;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet131MapData;

public interface ITinyPacketHandler
{
    void handle(NetHandler handler, Packet131MapData mapData);
}
