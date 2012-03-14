package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet70Bed extends Packet
{
    public static final String[] bedChat = new String[] {"tile.bed.notValid", null, null, "gameMode.changed"};
    public int bedState;

    /** Used only when reason = 3. 0 is survival, 1 is creative. */
    public int gameMode;

    public Packet70Bed() {}

    public Packet70Bed(int par1, int par2)
    {
        this.bedState = par1;
        this.gameMode = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.bedState = par1DataInputStream.readByte();
        this.gameMode = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte(this.bedState);
        par1DataOutputStream.writeByte(this.gameMode);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleBed(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2;
    }
}
