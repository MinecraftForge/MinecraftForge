package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet255KickDisconnect extends Packet
{
    /** Displayed to the client when the connection terminates. */
    public String reason;

    public Packet255KickDisconnect() {}

    public Packet255KickDisconnect(String par1Str)
    {
        this.reason = par1Str;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.reason = readString(par1DataInputStream, 1024);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        writeString(this.reason, par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleKickDisconnect(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return this.reason.length();
    }
}
