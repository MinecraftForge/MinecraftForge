package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet17Sleep extends Packet
{
    public int entityID;
    public int bedX;
    public int bedY;
    public int bedZ;
    public int field_22046_e;

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityID = par1DataInputStream.readInt();
        this.field_22046_e = par1DataInputStream.readByte();
        this.bedX = par1DataInputStream.readInt();
        this.bedY = par1DataInputStream.readByte();
        this.bedZ = par1DataInputStream.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityID);
        par1DataOutputStream.writeByte(this.field_22046_e);
        par1DataOutputStream.writeInt(this.bedX);
        par1DataOutputStream.writeByte(this.bedY);
        par1DataOutputStream.writeInt(this.bedZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleSleep(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 14;
    }
}
