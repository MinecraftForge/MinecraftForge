package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDENTIFIERS;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_RESPONSE;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_MISSING;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDMAP;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.*;
import net.minecraft.network.packet.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModListResponsePacket extends FMLPacket
{
    private Map<String,String> modVersions;
    private List<String> missingMods;

    public ModListResponsePacket()
    {
        super(MOD_LIST_RESPONSE);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        Map<String,String> modVersions = (Map<String, String>) data[0];
        List<String> missingMods = (List<String>) data[1];
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(modVersions.size());
        for (Entry<String, String> version : modVersions.entrySet())
        {
            dat.writeUTF(version.getKey());
            dat.writeUTF(version.getValue());
        }
        dat.writeInt(missingMods.size());
        for (String missing : missingMods)
        {
            dat.writeUTF(missing);
        }
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        int versionListSize = dat.readInt();
        modVersions = Maps.newHashMapWithExpectedSize(versionListSize);
        for (int i = 0; i < versionListSize; i++)
        {
            String modName = dat.readUTF();
            String modVersion = dat.readUTF();
            modVersions.put(modName, modVersion);
        }

        int missingModSize = dat.readInt();
        missingMods = Lists.newArrayListWithExpectedSize(missingModSize);

        for (int i = 0; i < missingModSize; i++)
        {
            missingMods.add(dat.readUTF());
        }
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        Map<String, ModContainer> indexedModList = Maps.newHashMap(Loader.instance().getIndexedModList());
        List<String> missingClientMods = Lists.newArrayList();
        List<String> versionIncorrectMods = Lists.newArrayList();

        for (String m : missingMods)
        {
            ModContainer mc = indexedModList.get(m);
            NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (networkMod.requiresClientSide())
            {
                missingClientMods.add(m);
            }
        }

        for (Entry<String,String> modVersion : modVersions.entrySet())
        {
            ModContainer mc = indexedModList.get(modVersion.getKey());
            NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (!networkMod.acceptVersion(modVersion.getValue()))
            {
                versionIncorrectMods.add(modVersion.getKey());
            }
        }

        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "FML";
        if (missingClientMods.size()>0 || versionIncorrectMods.size() > 0)
        {
            pkt.field_73629_c = FMLPacket.makePacket(MOD_MISSING, missingClientMods, versionIncorrectMods);
            Logger.getLogger("Minecraft").info(String.format("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods));
            FMLLog.info("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods);
            // Mark this as bad
            FMLNetworkHandler.setHandlerState((NetLoginHandler) netHandler, FMLNetworkHandler.MISSING_MODS_OR_VERSIONS);
            pkt.field_73628_b = pkt.field_73629_c.length;
            network.func_74429_a(pkt);
        }
        else
        {
            pkt.field_73629_c = FMLPacket.makePacket(MOD_IDENTIFIERS, netHandler);
            Logger.getLogger("Minecraft").info(String.format("User %s connecting with mods %s", userName, modVersions.keySet()));
            FMLLog.info("User %s connecting with mods %s", userName, modVersions.keySet());
            pkt.field_73628_b = pkt.field_73629_c.length;
            network.func_74429_a(pkt);
            NBTTagList itemList = new NBTTagList();
            GameData.writeItemData(itemList);
            byte[][] registryPackets = FMLPacket.makePacketSet(MOD_IDMAP, itemList);
            for (int i = 0; i < registryPackets.length; i++)
            {
                network.func_74429_a(PacketDispatcher.getPacket("FML", registryPackets[i]));
            }
        }

        // reset the continuation flag - we have completed extra negotiation and the login should complete now
        NetLoginHandler.func_72531_a((NetLoginHandler) netHandler, true);
    }

}
