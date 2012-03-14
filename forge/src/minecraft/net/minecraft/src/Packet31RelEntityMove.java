package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet31RelEntityMove extends Packet30Entity
{
    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        super.readPacketData(par1DataInputStream);
        this.xPosition = par1DataInputStream.readByte();
        this.yPosition = par1DataInputStream.readByte();
        this.zPosition = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        super.writePacketData(par1DataOutputStream);
        par1DataOutputStream.writeByte(this.xPosition);
        par1DataOutputStream.writeByte(this.yPosition);
        par1DataOutputStream.writeByte(this.zPosition);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 7;
    }
}
