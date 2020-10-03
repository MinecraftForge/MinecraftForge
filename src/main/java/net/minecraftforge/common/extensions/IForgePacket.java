package net.minecraftforge.common.extensions;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.ConnectionType;

import java.io.IOException;

public interface IForgePacket
{

    default IPacket<?> getPacket()
    {
        return ((IPacket<?>) this);
    }

    default void writePacketData(PacketBuffer buf, ConnectionType connectionType) throws IOException
    {
        getPacket().writePacketData(buf);
    }

}
