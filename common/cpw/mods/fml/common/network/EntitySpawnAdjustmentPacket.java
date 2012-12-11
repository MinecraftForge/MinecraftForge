package cpw.mods.fml.common.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;

public class EntitySpawnAdjustmentPacket extends FMLPacket
{

    public EntitySpawnAdjustmentPacket()
    {
        super(Type.ENTITYSPAWNADJUSTMENT);
    }

    public int entityId;
    public int serverX;
    public int serverY;
    public int serverZ;

    @Override
    public byte[] generatePacket(Object... data)
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt((Integer) data[0]);
        dat.writeInt((Integer) data[1]);
        dat.writeInt((Integer) data[2]);
        dat.writeInt((Integer) data[3]);
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        entityId = dat.readInt();
        serverX = dat.readInt();
        serverY = dat.readInt();
        serverZ = dat.readInt();
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        FMLCommonHandler.instance().adjustEntityLocationOnClient(this);
    }

}
