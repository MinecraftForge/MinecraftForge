package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet38EntityStatus extends Packet
{
    public int entityId;

    /** 2 for hurt, 3 for dead */
    public byte entityStatus;

    public Packet38EntityStatus() {}

    public Packet38EntityStatus(int par1, byte par2)
    {
        this.entityId = par1;
        this.entityStatus = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.entityStatus = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeByte(this.entityStatus);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityStatus(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 5;
    }
}
