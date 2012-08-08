package cpw.mods.fml.common.network;

import net.minecraft.src.NetHandler;
import net.minecraft.src.NetworkManager;

public class ModMissingPacket extends FMLPacket
{

    public ModMissingPacket()
    {
        super(Type.MOD_MISSING);
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
    public void execute(NetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        // TODO Auto-generated method stub

    }

}
