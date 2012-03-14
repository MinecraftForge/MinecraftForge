package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet132TileEntityData extends Packet
{
    public int field_48122_a;
    public int field_48120_b;
    public int field_48121_c;
    public int field_48118_d;
    public int field_48119_e;
    public int field_48116_f;
    public int field_48117_g;

    public Packet132TileEntityData()
    {
        this.isChunkDataPacket = true;
    }

    public Packet132TileEntityData(int par1, int par2, int par3, int par4, int par5)
    {
        this.isChunkDataPacket = true;
        this.field_48122_a = par1;
        this.field_48120_b = par2;
        this.field_48121_c = par3;
        this.field_48118_d = par4;
        this.field_48119_e = par5;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.field_48122_a = par1DataInputStream.readInt();
        this.field_48120_b = par1DataInputStream.readShort();
        this.field_48121_c = par1DataInputStream.readInt();
        this.field_48118_d = par1DataInputStream.readByte();
        this.field_48119_e = par1DataInputStream.readInt();
        this.field_48116_f = par1DataInputStream.readInt();
        this.field_48117_g = par1DataInputStream.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.field_48122_a);
        par1DataOutputStream.writeShort(this.field_48120_b);
        par1DataOutputStream.writeInt(this.field_48121_c);
        par1DataOutputStream.writeByte((byte)this.field_48118_d);
        par1DataOutputStream.writeInt(this.field_48119_e);
        par1DataOutputStream.writeInt(this.field_48116_f);
        par1DataOutputStream.writeInt(this.field_48117_g);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.func_48071_a(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 25;
    }
}
