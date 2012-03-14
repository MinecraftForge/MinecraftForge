package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet13PlayerLookMove extends Packet10Flying
{
    public Packet13PlayerLookMove()
    {
        this.rotating = true;
        this.moving = true;
    }

    public Packet13PlayerLookMove(double par1, double par3, double par5, double par7, float par9, float par10, boolean par11)
    {
        this.xPosition = par1;
        this.yPosition = par3;
        this.stance = par5;
        this.zPosition = par7;
        this.yaw = par9;
        this.pitch = par10;
        this.onGround = par11;
        this.rotating = true;
        this.moving = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.xPosition = par1DataInputStream.readDouble();
        this.yPosition = par1DataInputStream.readDouble();
        this.stance = par1DataInputStream.readDouble();
        this.zPosition = par1DataInputStream.readDouble();
        this.yaw = par1DataInputStream.readFloat();
        this.pitch = par1DataInputStream.readFloat();
        super.readPacketData(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeDouble(this.xPosition);
        par1DataOutputStream.writeDouble(this.yPosition);
        par1DataOutputStream.writeDouble(this.stance);
        par1DataOutputStream.writeDouble(this.zPosition);
        par1DataOutputStream.writeFloat(this.yaw);
        par1DataOutputStream.writeFloat(this.pitch);
        super.writePacketData(par1DataOutputStream);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 41;
    }
}
