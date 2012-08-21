package cpw.mods.fml.common.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

/**
 * A simple utility class to send packet 250 packets around the place
 *
 * @author cpw
 *
 */
public class PacketDispatcher
{
    public static Packet250CustomPayload getPacket(String type, byte[] data)
    {
        return new Packet250CustomPayload(type, data);
    }

    public static void sendPacketToServer(Packet packet)
    {
        FMLCommonHandler.instance().getSidedDelegate().sendPacket(packet);
    }

    public static void sendPacketToPlayer(Packet packet, Player player)
    {
        if (player instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)player).field_71135_a.func_72567_b(packet);
        }
    }

    public static void sendPacketToAllAround(double X, double Y, double Z, double range, int dimensionId, Packet packet)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null)
        {
            server.func_71203_ab().func_72393_a(X, Y, Z, range, dimensionId, packet);
        }
        else
        {
            FMLLog.fine("Attempt to send packet to all around without a server instance available");
        }
    }
}
