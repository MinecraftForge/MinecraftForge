package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet14BlockDig extends Packet
{
    /** Block X position. */
    public int xPosition;

    /** Block Y position. */
    public int yPosition;

    /** Block Z position. */
    public int zPosition;

    /** Punched face of the block. */
    public int face;

    /** Status of the digging (started, ongoing, broken). */
    public int status;

    public Packet14BlockDig() {}

    public Packet14BlockDig(int par1, int par2, int par3, int par4, int par5)
    {
        this.status = par1;
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        this.face = par5;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.status = par1DataInputStream.read();
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.read();
        this.zPosition = par1DataInputStream.readInt();
        this.face = par1DataInputStream.read();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.write(this.status);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.write(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.write(this.face);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleBlockDig(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 11;
    }
}
