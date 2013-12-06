package cpw.mods.fml.common.network.packet;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.packet.FMLOldPacket.Type;

public enum PacketManager {
    INSTANCE;

    private Map<Class<? extends FMLOldPacket>, PacketExecutor<?,?>> packetExecutors = Maps.newHashMap();


    public C17PacketCustomPayload makeGuiPacket(int windowId, int i, int modGuiId, int x, int y, int z)
    {
        byte[] data = FMLOldPacket.makePacket(Type.GUIOPEN, windowId, i, modGuiId, x, y, z);
        C17PacketCustomPayload packet = new C17PacketCustomPayload("FML", data);
        return packet;
    }



    public FMLOldPacket readPacket(NetworkDispatcher dispatcher, C17PacketCustomPayload clientSentPacket)
    {
        byte[] payload = clientSentPacket.func_149558_e();
        return buildPacket(dispatcher, payload);
    }

    public FMLOldPacket readPacket(NetworkDispatcher dispatcher, S3FPacketCustomPayload serverSentPacket)
    {
        byte[] payload = serverSentPacket.func_149168_d();
        return buildPacket(dispatcher, payload);
    }

    private FMLOldPacket buildPacket(NetworkDispatcher dispatcher, byte[] payload)
    {
        int type = UnsignedBytes.toInt(payload[0]);
        Type eType = Type.values()[type];
        FMLOldPacket pkt;
        if (eType.isMultipart())
        {
            pkt = eType.findCurrentPart(network);
        }
        else
        {
            pkt = eType.make();
        }
        return pkt.consumePacketData(Arrays.copyOfRange(payload, 1, payload.length));
    }


    static abstract class PacketExecutor<P, T extends INetHandler> implements Callable<Void>
    {
        P packet;
        T netHandler;
    }


    public Packet makeFMLHandshake()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
