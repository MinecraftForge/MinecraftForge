package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet12PlayerLook extends Packet10Flying
{
    public Packet12PlayerLook()
    {
        this.rotating = true;
    }

    public Packet12PlayerLook(float par1, float par2, boolean par3)
    {
        this.yaw = par1;
        this.pitch = par2;
        this.onGround = par3;
        this.rotating = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.yaw = par1DataInputStream.readFloat();
        this.pitch = par1DataInputStream.readFloat();
        super.readPacketData(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeFloat(this.yaw);
        par1DataOutputStream.writeFloat(this.pitch);
        super.writePacketData(par1DataOutputStream);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 9;
    }
}
