package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet2Handshake extends Packet
{
    /** The username of the player attempting to connect. */
    public String username;

    public Packet2Handshake() {}

    public Packet2Handshake(String par1Str)
    {
        this.username = par1Str;
    }

    public Packet2Handshake(String par1Str, String par2Str, int par3)
    {
        this.username = par1Str + ";" + par2Str + ":" + par3;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.username = readString(par1DataInputStream, 64);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        writeString(this.username, par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleHandshake(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 4 + this.username.length() + 4;
    }
}
