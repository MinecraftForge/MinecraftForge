package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDENTIFIERS;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ModIdentifiersPacket extends FMLPacket
{

    private Map<String, Integer> modIds = Maps.newHashMap();

    public ModIdentifiersPacket()
    {
        super(MOD_IDENTIFIERS);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        Collection<NetworkModHandler >networkMods = FMLNetworkHandler.instance().getNetworkIdMap().values();

        dat.writeInt(networkMods.size());
        for (NetworkModHandler handler : networkMods)
        {
            dat.writeUTF(handler.getContainer().getModId());
            dat.writeInt(handler.getNetworkId());
        }

        // TODO send the other id maps as well
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        int listSize = dat.readInt();
        for (int i = 0; i < listSize; i++)
        {
            String modId = dat.readUTF();
            int networkId = dat.readInt();
            modIds.put(modId, networkId);
        }
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        for (Entry<String,Integer> idEntry : modIds.entrySet())
        {
            handler.bindNetworkId(idEntry.getKey(), idEntry.getValue());
        }
        // TODO other id maps
    }
}
