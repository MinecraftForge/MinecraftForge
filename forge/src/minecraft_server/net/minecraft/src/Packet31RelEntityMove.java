package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet31RelEntityMove extends Packet30Entity
{
    public Packet31RelEntityMove() {}

    public Packet31RelEntityMove(int par1, byte par2, byte par3, byte par4)
    {
        super(par1);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
    }

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
