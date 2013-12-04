package cpw.mods.fml.common.network.packet;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.packet.PacketManager.PacketExecutor;

public class OpenGuiHandler extends PacketExecutor<OpenGuiPacket, NetHandlerPlayClient> {
    @Override
    public Void call() throws Exception
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().field_71439_g;
        player.openGui(packet.networkId, packet.modGuiId, player.field_70170_p, packet.x, packet.y, packet.z);
        player.field_71070_bA.field_75152_c = packet.windowId;
        return null;
    }

}
