package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet35EntityHeadRotation extends Packet
{
    public int field_48115_a;
    public byte field_48114_b;

    public Packet35EntityHeadRotation() {}

    public Packet35EntityHeadRotation(int par1, byte par2)
    {
        this.field_48115_a = par1;
        this.field_48114_b = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.field_48115_a = par1DataInputStream.readInt();
        this.field_48114_b = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.field_48115_a);
        par1DataOutputStream.writeByte(this.field_48114_b);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.func_48072_a(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 5;
    }
}
