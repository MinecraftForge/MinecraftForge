package cpw.mods.fml.common.network;

import java.io.IOException;
import java.util.BitSet;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.collect.MapDifference;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetHandler;
import net.minecraft.src.WorldClient;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDMAP;

public class ModIdMapPacket extends FMLPacket {
    private byte[][] partials;

    public ModIdMapPacket()
    {
        super(MOD_IDMAP);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        NBTTagList completeList = (NBTTagList) data[0];
        NBTTagCompound wrap = new NBTTagCompound();
        wrap.func_74782_a("List", completeList);
        try
        {
            return CompressedStreamTools.func_74798_a(wrap);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A critical error writing the id map");
            throw new FMLNetworkException(e);
        }
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput bdi = ByteStreams.newDataInput(data);
        int chunkIdx = UnsignedBytes.toInt(bdi.readByte());
        int chunkTotal = UnsignedBytes.toInt(bdi.readByte());
        int chunkLength = bdi.readInt();
        if (partials == null)
        {
            partials = new byte[chunkTotal][];
        }
        partials[chunkIdx] = new byte[chunkLength];
        bdi.readFully(partials[chunkIdx]);
        for (int i = 0; i < partials.length; i++)
        {
            if (partials[i] == null)
            {
                return null;
            }
        }
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        byte[] allData = Bytes.concat(partials);
        GameRegistry.initializeServerGate(1);
        try
        {
            NBTTagCompound serverList = CompressedStreamTools.func_74792_a(allData);
            NBTTagList list = serverList.func_74761_m("List");
            Set<ItemData> itemData = GameRegistry.buildWorldItemData(list);
            GameRegistry.validateWorldSave(itemData);
            MapDifference<Integer, ItemData> serverDifference = GameRegistry.gateWorldLoadingForValidation();
            if (serverDifference!=null)
            {
                FMLCommonHandler.instance().disconnectIDMismatch(serverDifference, netHandler, network);

            }
        }
        catch (IOException e)
        {
        }
    }

}
