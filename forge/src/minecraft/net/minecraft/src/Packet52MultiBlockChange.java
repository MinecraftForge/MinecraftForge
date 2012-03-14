package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet52MultiBlockChange extends Packet
{
    /** Chunk X position. */
    public int xPosition;

    /** Chunk Z position. */
    public int zPosition;

    /** The metadata for each block changed. */
    public byte[] metadataArray;

    /** The size of the arrays. */
    public int size;
    private static byte[] field_48168_e = new byte[0];

    public Packet52MultiBlockChange()
    {
        this.isChunkDataPacket = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.xPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.size = par1DataInputStream.readShort() & 65535;
        int var2 = par1DataInputStream.readInt();

        if (var2 > 0)
        {
            this.metadataArray = new byte[var2];
            par1DataInputStream.readFully(this.metadataArray);
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeShort((short)this.size);

        if (this.metadataArray != null)
        {
            par1DataOutputStream.writeInt(this.metadataArray.length);
            par1DataOutputStream.write(this.metadataArray);
        }
        else
        {
            par1DataOutputStream.writeInt(0);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleMultiBlockChange(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 10 + this.size * 4;
    }
}
