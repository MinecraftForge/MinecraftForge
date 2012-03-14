package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet106Transaction extends Packet
{
    /** The id of the window that the action occurred in. */
    public int windowId;
    public short shortWindowId;
    public boolean accepted;

    public Packet106Transaction() {}

    public Packet106Transaction(int par1, short par2, boolean par3)
    {
        this.windowId = par1;
        this.shortWindowId = par2;
        this.accepted = par3;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleTransaction(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.windowId = par1DataInputStream.readByte();
        this.shortWindowId = par1DataInputStream.readShort();
        this.accepted = par1DataInputStream.readByte() != 0;
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte(this.windowId);
        par1DataOutputStream.writeShort(this.shortWindowId);
        par1DataOutputStream.writeByte(this.accepted ? 1 : 0);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 4;
    }
}
