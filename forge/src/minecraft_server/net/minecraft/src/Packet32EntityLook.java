package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet32EntityLook extends Packet30Entity
{
    public Packet32EntityLook()
    {
        this.rotating = true;
    }

    public Packet32EntityLook(int par1, byte par2, byte par3)
    {
        super(par1);
        this.yaw = par2;
        this.pitch = par3;
        this.rotating = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        super.readPacketData(par1DataInputStream);
        this.yaw = par1DataInputStream.readByte();
        this.pitch = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        super.writePacketData(par1DataOutputStream);
        par1DataOutputStream.writeByte(this.yaw);
        par1DataOutputStream.writeByte(this.pitch);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 6;
    }
}
