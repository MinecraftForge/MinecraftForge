package cpw.mods.fml.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class OpenGuiPacket extends FMLPacket
{
    private int windowId;
    private int networkId;
    private int modGuiId;
    private int x;
    private int y;
    private int z;
    
    public OpenGuiPacket()
    {
        super(Type.GUIOPEN);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt((Integer) data[0]); // windowId
        dat.writeInt((Integer) data[1]); // networkId
        dat.writeInt((Integer) data[2]); // modGuiId
        dat.writeInt((Integer) data[3]); // x
        dat.writeInt((Integer) data[4]); // y
        dat.writeInt((Integer) data[5]); // z
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        windowId = dat.readInt();
        networkId = dat.readInt();
        modGuiId = dat.readInt();
        x = dat.readInt();
        y = dat.readInt();
        z = dat.readInt();
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        EntityPlayer player = netHandler.getPlayer();
        player.openGui(networkId, modGuiId, player.field_70170_p, x, y, z);
        player.field_71070_bA.field_75152_c = windowId;
    }

}
