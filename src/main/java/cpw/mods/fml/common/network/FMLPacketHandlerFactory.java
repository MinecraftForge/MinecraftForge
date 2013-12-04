package cpw.mods.fml.common.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;

public class FMLPacketHandlerFactory implements IPacketHandlerFactory {
    private static final String[] CHANNEL = new String[] { "FML" };
    @Override
    public String[] channels()
    {
        return CHANNEL;
    }

    @Override
    public IClientSidePacketHandler makeClientPacketHandler(NetworkMod mod, NetworkManager manager, INetHandler clientPlayHandler, String channel)
    {
        return null;
    }

    @Override
    public IServerSidePacketHandler makeServerPacketHandler(NetworkMod mod, NetworkManager manager, INetHandler serverPlayHandler)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
