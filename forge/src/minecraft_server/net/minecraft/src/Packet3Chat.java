package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet3Chat extends Packet
{
    /** The message being sent. */
    public String message;

    public Packet3Chat() {}

    public Packet3Chat(String par1Str)
    {
        if (par1Str.length() > 119)
        {
            par1Str = par1Str.substring(0, 119);
        }

        this.message = par1Str;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.message = readString(par1DataInputStream, 119);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        writeString(this.message, par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleChat(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.message.length() * 2;
    }
}
