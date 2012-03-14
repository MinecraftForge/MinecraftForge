package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet8UpdateHealth extends Packet
{
    /** Variable used for incoming health packets */
    public int healthMP;
    public int food;

    /**
     * Players logging on get a saturation of 5.0. Eating food increases the saturation as well as the food bar.
     */
    public float foodSaturation;

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.healthMP = par1DataInputStream.readShort();
        this.food = par1DataInputStream.readShort();
        this.foodSaturation = par1DataInputStream.readFloat();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeShort(this.healthMP);
        par1DataOutputStream.writeShort(this.food);
        par1DataOutputStream.writeFloat(this.foodSaturation);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUpdateHealth(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }
}
