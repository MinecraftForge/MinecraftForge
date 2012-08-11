package cpw.mods.fml.common.network;

import java.util.List;

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
        // TODO
        List<String> missing = (List<String>) data[0];
        List<String> badVersion = (List<String>) data[1];

        return new byte[0];
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        // TODO
        return this;
    }

    @Override
    public void execute(NetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        // TODO
    }

}
