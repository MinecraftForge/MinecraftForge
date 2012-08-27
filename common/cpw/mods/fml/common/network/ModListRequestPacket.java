package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_REQUEST;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_RESPONSE;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.src.NetHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ModListRequestPacket extends FMLPacket
{
    private List<String> sentModList;

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
        return this;
    }

    /**
     *
     * This packet is executed on the client to evaluate the server's mod list against
     * the client
     *
     * @see cpw.mods.fml.common.network.FMLPacket#execute()
     */
    @Override
    public void execute(NetworkManager mgr, FMLNetworkHandler handler, NetHandler netHandler, String userName)
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

        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "FML";
        pkt.field_73629_c = FMLPacket.makePacket(MOD_LIST_RESPONSE, modVersions, missingMods);
        pkt.field_73628_b = pkt.field_73629_c.length;

        mgr.func_74429_a(pkt);
    }
}
