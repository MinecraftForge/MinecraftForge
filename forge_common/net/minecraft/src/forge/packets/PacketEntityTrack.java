package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketEntityTrack extends ForgePacket
{
    public int entityId;
    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    public PacketEntityTrack(){}
    public PacketEntityTrack(int entityId, int serverPosX, int serverPosY, int serverPosZ)
    {
        this.entityId = entityId;
        this.serverPosX = serverPosX;
        this.serverPosY = serverPosY;
        this.serverPosZ = serverPosZ;
    }

    public void writeData(DataOutputStream data) throws IOException
    {
        data.writeInt(entityId);
        data.writeInt(serverPosX);
        data.writeInt(serverPosY);
        data.writeInt(serverPosZ);
    }

    public void readData(DataInputStream data) throws IOException
    {
        entityId = data.readInt();
        serverPosX = data.readInt();
        serverPosY = data.readInt();
        serverPosZ = data.readInt();
    }

    @Override
    public int getID()
    {
        return ForgePacket.TRACK;
    }
}
