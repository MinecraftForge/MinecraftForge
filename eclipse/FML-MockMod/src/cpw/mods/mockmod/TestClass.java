package cpw.mods.mockmod;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.Side;

import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

@SideOnly(Side.CLIENT)
public class TestClass implements IPacketHandler
{

    static
    {
        Thread.dumpStack();
    }
    @Override
    public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        // TODO Auto-generated method stub

    }

}
