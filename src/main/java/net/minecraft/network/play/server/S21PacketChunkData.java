package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData extends Packet
{
    private int field_149284_a;
    private int field_149282_b;
    private int field_149283_c;
    private int field_149280_d;
    private byte[] field_149281_e;
    private byte[] field_149278_f;
    private boolean field_149279_g;
    private int field_149285_h;
    private static byte[] field_149286_i = new byte[196864];
    private static final String __OBFID = "CL_00001304";
    private Semaphore deflateGate;

    public S21PacketChunkData() {}

    public S21PacketChunkData(Chunk p_i45196_1_, boolean p_i45196_2_, int p_i45196_3_)
    {
        this.field_149284_a = p_i45196_1_.xPosition;
        this.field_149282_b = p_i45196_1_.zPosition;
        this.field_149279_g = p_i45196_2_;
        S21PacketChunkData.Extracted extracted = func_149269_a(p_i45196_1_, p_i45196_2_, p_i45196_3_);
        this.field_149280_d = extracted.field_150281_c;
        this.field_149283_c = extracted.field_150280_b;
        this.field_149278_f = extracted.field_150282_a;
        this.deflateGate = new Semaphore(1);
    }

    private void deflate()
    {
        Deflater deflater = new Deflater(-1);
        try
        {
            deflater.setInput(this.field_149278_f, 0, this.field_149278_f.length);
            deflater.finish();
            byte[] deflated = new byte[this.field_149278_f.length];
            this.field_149285_h = deflater.deflate(deflated);
            this.field_149281_e = deflated;
        }
        finally
        {
            deflater.end();
        }
    }

    public static int func_149275_c()
    {
        return 196864;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149284_a = p_148837_1_.readInt();
        this.field_149282_b = p_148837_1_.readInt();
        this.field_149279_g = p_148837_1_.readBoolean();
        this.field_149283_c = p_148837_1_.readShort();
        this.field_149280_d = p_148837_1_.readShort();
        this.field_149285_h = p_148837_1_.readInt();

        if (field_149286_i.length < this.field_149285_h)
        {
            field_149286_i = new byte[this.field_149285_h];
        }

        p_148837_1_.readBytes(field_149286_i, 0, this.field_149285_h);
        int i = 0;
        int j;
        int msb = 0; //BugFix: MC does not read the MSB array from the packet properly, causing issues for servers that use blocks > 256

        for (j = 0; j < 16; ++j)
        {
            i += this.field_149283_c >> j & 1;
            msb += this.field_149283_c >> j & 1;
        }

        j = 12288 * i;
        j += 2048 * msb;

        if (this.field_149279_g)
        {
            j += 256;
        }

        this.field_149278_f = new byte[j];
        Inflater inflater = new Inflater();
        inflater.setInput(field_149286_i, 0, this.field_149285_h);

        try
        {
            inflater.inflate(this.field_149278_f);
        }
        catch (DataFormatException dataformatexception)
        {
            throw new IOException("Bad compressed data format");
        }
        finally
        {
            inflater.end();
        }
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        if (this.field_149281_e == null)
        {
            deflateGate.acquireUninterruptibly();
            if (this.field_149281_e == null)
            {
                deflate();
            }
            deflateGate.release();
        }
        p_148840_1_.writeInt(this.field_149284_a);
        p_148840_1_.writeInt(this.field_149282_b);
        p_148840_1_.writeBoolean(this.field_149279_g);
        p_148840_1_.writeShort((short)(this.field_149283_c & 65535));
        p_148840_1_.writeShort((short)(this.field_149280_d & 65535));
        p_148840_1_.writeInt(this.field_149285_h);
        p_148840_1_.writeBytes(this.field_149281_e, 0, this.field_149285_h);
    }

    public void func_148833_a(INetHandlerPlayClient p_149277_1_)
    {
        p_149277_1_.func_147263_a(this);
    }

    public String func_148835_b()
    {
        return String.format("x=%d, z=%d, full=%b, sects=%d, add=%d, size=%d", new Object[] {Integer.valueOf(this.field_149284_a), Integer.valueOf(this.field_149282_b), Boolean.valueOf(this.field_149279_g), Integer.valueOf(this.field_149283_c), Integer.valueOf(this.field_149280_d), Integer.valueOf(this.field_149285_h)});
    }

    @SideOnly(Side.CLIENT)
    public byte[] func_149272_d()
    {
        return this.field_149278_f;
    }

    public static S21PacketChunkData.Extracted func_149269_a(Chunk p_149269_0_, boolean p_149269_1_, int p_149269_2_)
    {
        int j = 0;
        ExtendedBlockStorage[] aextendedblockstorage = p_149269_0_.getBlockStorageArray();
        int k = 0;
        S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
        byte[] abyte = field_149286_i;

        if (p_149269_1_)
        {
            p_149269_0_.sendUpdates = true;
        }

        int l;

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!p_149269_1_ || !aextendedblockstorage[l].isEmpty()) && (p_149269_2_ & 1 << l) != 0)
            {
                extracted.field_150280_b |= 1 << l;

                if (aextendedblockstorage[l].getBlockMSBArray() != null)
                {
                    extracted.field_150281_c |= 1 << l;
                    ++k;
                }
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!p_149269_1_ || !aextendedblockstorage[l].isEmpty()) && (p_149269_2_ & 1 << l) != 0)
            {
                byte[] abyte1 = aextendedblockstorage[l].getBlockLSBArray();
                System.arraycopy(abyte1, 0, abyte, j, abyte1.length);
                j += abyte1.length;
            }
        }

        NibbleArray nibblearray;

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!p_149269_1_ || !aextendedblockstorage[l].isEmpty()) && (p_149269_2_ & 1 << l) != 0)
            {
                nibblearray = aextendedblockstorage[l].getMetadataArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!p_149269_1_ || !aextendedblockstorage[l].isEmpty()) && (p_149269_2_ & 1 << l) != 0)
            {
                nibblearray = aextendedblockstorage[l].getBlocklightArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        if (!p_149269_0_.worldObj.provider.hasNoSky)
        {
            for (l = 0; l < aextendedblockstorage.length; ++l)
            {
                if (aextendedblockstorage[l] != null && (!p_149269_1_ || !aextendedblockstorage[l].isEmpty()) && (p_149269_2_ & 1 << l) != 0)
                {
                    nibblearray = aextendedblockstorage[l].getSkylightArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if (k > 0)
        {
            for (l = 0; l < aextendedblockstorage.length; ++l)
            {
                if (aextendedblockstorage[l] != null && (!p_149269_1_ || !aextendedblockstorage[l].isEmpty()) && aextendedblockstorage[l].getBlockMSBArray() != null && (p_149269_2_ & 1 << l) != 0)
                {
                    nibblearray = aextendedblockstorage[l].getBlockMSBArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if (p_149269_1_)
        {
            byte[] abyte2 = p_149269_0_.getBiomeArray();
            System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
            j += abyte2.length;
        }

        extracted.field_150282_a = new byte[j];
        System.arraycopy(abyte, 0, extracted.field_150282_a, 0, j);
        return extracted;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public int func_149273_e()
    {
        return this.field_149284_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149271_f()
    {
        return this.field_149282_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149276_g()
    {
        return this.field_149283_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_149270_h()
    {
        return this.field_149280_d;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149274_i()
    {
        return this.field_149279_g;
    }

    public static class Extracted
        {
            public byte[] field_150282_a;
            public int field_150280_b;
            public int field_150281_c;
            private static final String __OBFID = "CL_00001305";
        }
}