package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet
{
    public int field_48109_a;
    public int field_48107_b;
    public int field_48108_c;
    public int field_48105_d;
    public byte[] field_48106_e;
    public boolean field_48103_f;
    private int field_48104_g;
    private int field_48110_h;
    private static byte[] field_48111_i = new byte[0];

    public Packet51MapChunk()
    {
        this.isChunkDataPacket = true;
    }

    public Packet51MapChunk(Chunk par1Chunk, boolean par2, int par3)
    {
        this.isChunkDataPacket = true;
        this.field_48109_a = par1Chunk.xPosition;
        this.field_48107_b = par1Chunk.zPosition;
        this.field_48103_f = par2;

        if (par2)
        {
            par3 = 65535;
        }

        ExtendedBlockStorage[] var4 = par1Chunk.func_48553_h();
        int var5 = 0;
        int var6 = 0;
        int var7;

        for (var7 = 0; var7 < var4.length; ++var7)
        {
            if (var4[var7] != null && (!par2 || !var4[var7].func_48595_a()) && (par3 & 1 << var7) != 0)
            {
                this.field_48108_c |= 1 << var7;
                ++var5;

                if (var4[var7].func_48601_h() != null)
                {
                    this.field_48105_d |= 1 << var7;
                    ++var6;
                }
            }
        }

        var7 = 2048 * (5 * var5 + var6);

        if (par2)
        {
            var7 += 256;
        }

        if (field_48111_i.length < var7)
        {
            field_48111_i = new byte[var7];
        }

        byte[] var8 = field_48111_i;
        int var9 = 0;
        int var10;

        for (var10 = 0; var10 < var4.length; ++var10)
        {
            if (var4[var10] != null && (!par2 || !var4[var10].func_48595_a()) && (par3 & 1 << var10) != 0)
            {
                byte[] var11 = var4[var10].func_48590_g();
                System.arraycopy(var11, 0, var8, var9, var11.length);
                var9 += var11.length;
            }
        }

        NibbleArray var15;

        for (var10 = 0; var10 < var4.length; ++var10)
        {
            if (var4[var10] != null && (!par2 || !var4[var10].func_48595_a()) && (par3 & 1 << var10) != 0)
            {
                var15 = var4[var10].func_48594_i();
                System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
                var9 += var15.data.length;
            }
        }

        for (var10 = 0; var10 < var4.length; ++var10)
        {
            if (var4[var10] != null && (!par2 || !var4[var10].func_48595_a()) && (par3 & 1 << var10) != 0)
            {
                var15 = var4[var10].func_48600_j();
                System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
                var9 += var15.data.length;
            }
        }

        for (var10 = 0; var10 < var4.length; ++var10)
        {
            if (var4[var10] != null && (!par2 || !var4[var10].func_48595_a()) && (par3 & 1 << var10) != 0)
            {
                var15 = var4[var10].func_48605_k();
                System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
                var9 += var15.data.length;
            }
        }

        if (var6 > 0)
        {
            for (var10 = 0; var10 < var4.length; ++var10)
            {
                if (var4[var10] != null && (!par2 || !var4[var10].func_48595_a()) && var4[var10].func_48601_h() != null && (par3 & 1 << var10) != 0)
                {
                    var15 = var4[var10].func_48601_h();
                    System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
                    var9 += var15.data.length;
                }
            }
        }

        if (par2)
        {
            byte[] var17 = par1Chunk.func_48552_l();
            System.arraycopy(var17, 0, var8, var9, var17.length);
            var9 += var17.length;
        }

        Deflater var16 = new Deflater(-1);

        try
        {
            var16.setInput(var8, 0, var9);
            var16.finish();
            this.field_48106_e = new byte[var9];
            this.field_48104_g = var16.deflate(this.field_48106_e);
        }
        finally
        {
            var16.end();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.field_48109_a = par1DataInputStream.readInt();
        this.field_48107_b = par1DataInputStream.readInt();
        this.field_48103_f = par1DataInputStream.readBoolean();
        this.field_48108_c = par1DataInputStream.readShort();
        this.field_48105_d = par1DataInputStream.readShort();
        this.field_48104_g = par1DataInputStream.readInt();
        this.field_48110_h = par1DataInputStream.readInt();

        if (field_48111_i.length < this.field_48104_g)
        {
            field_48111_i = new byte[this.field_48104_g];
        }

        par1DataInputStream.readFully(field_48111_i, 0, this.field_48104_g);
        int var2 = 0;
        int var3;

        for (var3 = 0; var3 < 16; ++var3)
        {
            var2 += this.field_48108_c >> var3 & 1;
        }

        var3 = 12288 * var2;

        if (this.field_48103_f)
        {
            var3 += 256;
        }

        this.field_48106_e = new byte[var3];
        Inflater var4 = new Inflater();
        var4.setInput(field_48111_i, 0, this.field_48104_g);

        try
        {
            var4.inflate(this.field_48106_e);
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
        par1DataOutputStream.writeInt(this.field_48109_a);
        par1DataOutputStream.writeInt(this.field_48107_b);
        par1DataOutputStream.writeBoolean(this.field_48103_f);
        par1DataOutputStream.writeShort((short)(this.field_48108_c & 65535));
        par1DataOutputStream.writeShort((short)(this.field_48105_d & 65535));
        par1DataOutputStream.writeInt(this.field_48104_g);
        par1DataOutputStream.writeInt(this.field_48110_h);
        par1DataOutputStream.write(this.field_48106_e, 0, this.field_48104_g);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.func_48070_a(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 17 + this.field_48104_g;
    }
}
