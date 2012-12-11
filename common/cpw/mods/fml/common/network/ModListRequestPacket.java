package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_REQUEST;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_RESPONSE;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ModListRequestPacket extends FMLPacket
{
    private List<String> sentModList;
    private byte compatibilityLevel;

    public ModListRequestPacket()
    {
        super(MOD_LIST_REQUEST);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        Set<ModContainer> activeMods = FMLNetworkHandler.instance().getNetworkModList();
        dat.writeInt(activeMods.size());
        for (ModContainer mc : activeMods)
        {
            dat.writeUTF(mc.getModId());
        }
        dat.writeByte(FMLNetworkHandler.getCompatibilityLevel());
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        sentModList = Lists.newArrayList();
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        int listSize = in.readInt();
        for (int i = 0; i < listSize; i++)
        {
            sentModList.add(in.readUTF());
        }
        try
        {
            compatibilityLevel = in.readByte();
        }
        catch (IllegalStateException e)
        {
            FMLLog.fine("No compatibility byte found - the server is too old");
        }
        return this;
    }

    /**
     *
     * This packet is executed on the client to evaluate the server's mod list against
     * the client
     *
     * @see cpw.mods.fml.common.network.FMLPacket#execute(INetworkManager, FMLNetworkHandler, NetHandler, String)
     */
    @Override
    public void execute(INetworkManager mgr, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        List<String> missingMods = Lists.newArrayList();
        Map<String,String> modVersions = Maps.newHashMap();
        Map<String, ModContainer> indexedModList = Maps.newHashMap(Loader.instance().getIndexedModList());

        for (String m : sentModList)
        {
            ModContainer mc = indexedModList.get(m);
            if (mc == null)
            {
                missingMods.add(m);
                continue;
            }
            indexedModList.remove(m);
            modVersions.put(m, mc.getVersion());
        }

        if (indexedModList.size()>0)
        {
            for (Entry<String, ModContainer> e : indexedModList.entrySet())
            {
                if (e.getValue().isNetworkMod())
                {
                    NetworkModHandler missingHandler = FMLNetworkHandler.instance().findNetworkModHandler(e.getValue());
                    if (missingHandler.requiresServerSide())
                    {
                        // TODO : what should we do if a mod is marked "serverSideRequired"? Stop the connection?
                        FMLLog.warning("The mod %s was not found on the server you connected to, but requested that the server side be present", e.getKey());
                    }
                }
            }
        }

        FMLLog.fine("The server has compatibility level %d", compatibilityLevel);
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel(compatibilityLevel);

        mgr.func_74429_a(PacketDispatcher.getPacket("FML", FMLPacket.makePacket(MOD_LIST_RESPONSE, modVersions, missingMods)));
    }
}
