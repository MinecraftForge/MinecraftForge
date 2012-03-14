package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet61DoorChange extends Packet
{
    public int sfxID;
    public int auxData;
    public int posX;
    public int posY;
    public int posZ;

    public Packet61DoorChange() {}

    public Packet61DoorChange(int par1, int par2, int par3, int par4, int par5)
    {
        this.sfxID = par1;
        this.posX = par2;
        this.posY = par3;
        this.posZ = par4;
        this.auxData = par5;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.sfxID = par1DataInputStream.readInt();
        this.posX = par1DataInputStream.readInt();
        this.posY = par1DataInputStream.readByte() & 255;
        this.posZ = par1DataInputStream.readInt();
        this.auxData = par1DataInputStream.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.sfxID);
        par1DataOutputStream.writeInt(this.posX);
        par1DataOutputStream.writeByte(this.posY & 255);
        par1DataOutputStream.writeInt(this.posZ);
        par1DataOutputStream.writeInt(this.auxData);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleDoorChange(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 20;
    }
}
