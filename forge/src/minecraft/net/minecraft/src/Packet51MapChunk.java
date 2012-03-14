package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet
{
    public int field_48177_a;
    public int field_48175_b;
    public int field_48176_c;
    public int field_48173_d;
    public byte[] field_48174_e;
    public boolean field_48171_f;
    private int field_48172_g;
    private int field_48178_h;
    private static byte[] field_48179_i = new byte[0];

    public Packet51MapChunk()
    {
        this.isChunkDataPacket = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.field_48177_a = par1DataInputStream.readInt();
        this.field_48175_b = par1DataInputStream.readInt();
        this.field_48171_f = par1DataInputStream.readBoolean();
        this.field_48176_c = par1DataInputStream.readShort();
        this.field_48173_d = par1DataInputStream.readShort();
        this.field_48172_g = par1DataInputStream.readInt();
        this.field_48178_h = par1DataInputStream.readInt();

        if (field_48179_i.length < this.field_48172_g)
        {
            field_48179_i = new byte[this.field_48172_g];
        }

        par1DataInputStream.readFully(field_48179_i, 0, this.field_48172_g);
        int var2 = 0;
        int var3;

        for (var3 = 0; var3 < 16; ++var3)
        {
            var2 += this.field_48176_c >> var3 & 1;
        }

        var3 = 12288 * var2;

        if (this.field_48171_f)
        {
            var3 += 256;
        }

        this.field_48174_e = new byte[var3];
        Inflater var4 = new Inflater();
        var4.setInput(field_48179_i, 0, this.field_48172_g);

        try
        {
            var4.inflate(this.field_48174_e);
        }
        catch (DataFormatException var9)
        {
            throw new IOException("Bad compressed data format");
        }
        finally
        {
            var4.end();
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.field_48177_a);
        par1DataOutputStream.writeInt(this.field_48175_b);
        par1DataOutputStream.writeBoolean(this.field_48171_f);
        par1DataOutputStream.writeShort((short)(this.field_48176_c & 65535));
        par1DataOutputStream.writeShort((short)(this.field_48173_d & 65535));
        par1DataOutputStream.writeInt(this.field_48172_g);
        par1DataOutputStream.writeInt(this.field_48178_h);
        par1DataOutputStream.write(this.field_48174_e, 0, this.field_48172_g);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.func_48487_a(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 17 + this.field_48172_g;
    }
}
