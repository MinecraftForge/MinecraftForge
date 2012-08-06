package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.*;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetworkManager;

public class ModIdentifiersPacket extends FMLPacket
{

    public ModIdentifiersPacket()
    {
        super(MOD_IDENTIFIERS);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execute(NetworkManager network, FMLNetworkHandler handler, NetHandler netHandler)
    {
        // TODO Auto-generated method stub

    }
}
