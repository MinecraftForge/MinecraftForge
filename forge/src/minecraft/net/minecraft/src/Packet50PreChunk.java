package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet50PreChunk extends Packet
{
    /** The X position of the chunk. */
    public int xPosition;

    /** The Y position of the chunk. */
    public int yPosition;

    /**
     * If mode is true (1) the client will initialise the chunk. If it is false (0) the client will unload the chunk.
     */
    public boolean mode;

    public Packet50PreChunk()
    {
        this.isChunkDataPacket = false;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.mode = par1DataInputStream.read() != 0;
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.write(this.mode ? 1 : 0);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handlePreChunk(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 9;
    }
}
