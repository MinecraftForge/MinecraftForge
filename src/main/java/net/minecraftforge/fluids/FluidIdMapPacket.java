
package net.minecraftforge.fluids;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraftforge.common.network.ForgePacket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class FluidIdMapPacket extends ForgePacket
{
    private BiMap<String, Integer> fluidIds = HashBiMap.create();

    @Override
    public byte[] generatePacket()
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();

        dat.writeInt(FluidRegistry.maxID);
        for (Map.Entry<String, Integer> entry : FluidRegistry.fluidIDs.entrySet())
        {
            dat.writeUTF(entry.getKey());
            dat.writeInt(entry.getValue());
        }
        return dat.toByteArray();
    }

    @Override
    public ForgePacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        int listSize = dat.readInt();
        for (int i = 0; i < listSize; i++) {
            String fluidName = dat.readUTF();
            int fluidId = dat.readInt();
            fluidIds.put(fluidName, fluidId);
        }
        return this;
    }

    @Override
    public void execute(INetworkManager network, EntityPlayer player)
    {
        FluidRegistry.initFluidIDs(fluidIds);
    }
}
