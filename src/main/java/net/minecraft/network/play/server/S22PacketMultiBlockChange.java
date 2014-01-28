package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S22PacketMultiBlockChange extends Packet
{
    private static final Logger field_148927_a = LogManager.getLogger();
    private ChunkCoordIntPair field_148925_b;
    private byte[] field_148926_c;
    private int field_148924_d;
    private static final String __OBFID = "CL_00001290";

    public S22PacketMultiBlockChange() {}

    public S22PacketMultiBlockChange(int p_i45181_1_, short[] p_i45181_2_, Chunk p_i45181_3_)
    {
        this.field_148925_b = new ChunkCoordIntPair(p_i45181_3_.xPosition, p_i45181_3_.zPosition);
        this.field_148924_d = p_i45181_1_;
        int j = 4 * p_i45181_1_;

        try
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(j);
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

            for (int k = 0; k < p_i45181_1_; ++k)
            {
                int l = p_i45181_2_[k] >> 12 & 15;
                int i1 = p_i45181_2_[k] >> 8 & 15;
                int j1 = p_i45181_2_[k] & 255;
                dataoutputstream.writeShort(p_i45181_2_[k]);
                dataoutputstream.writeShort((short)((Block.func_149682_b(p_i45181_3_.func_150810_a(l, j1, i1)) & 4095) << 4 | p_i45181_3_.getBlockMetadata(l, j1, i1) & 15));
            }

            this.field_148926_c = bytearrayoutputstream.toByteArray();

            if (this.field_148926_c.length != j)
            {
                throw new RuntimeException("Expected length " + j + " doesn\'t match received length " + this.field_148926_c.length);
            }
        }
        catch (IOException ioexception)
        {
            field_148927_a.error("Couldn\'t create bulk block update packet", ioexception);
            this.field_148926_c = null;
        }
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_148925_b = new ChunkCoordIntPair(p_148837_1_.readInt(), p_148837_1_.readInt());
        this.field_148924_d = p_148837_1_.readShort() & 65535;
        int i = p_148837_1_.readInt();

        if (i > 0)
        {
            this.field_148926_c = new byte[i];
            p_148837_1_.readBytes(this.field_148926_c);
        }
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_148925_b.chunkXPos);
        p_148840_1_.writeInt(this.field_148925_b.chunkZPos);
        p_148840_1_.writeShort((short)this.field_148924_d);

        if (this.field_148926_c != null)
        {
            p_148840_1_.writeInt(this.field_148926_c.length);
            p_148840_1_.writeBytes(this.field_148926_c);
        }
        else
        {
            p_148840_1_.writeInt(0);
        }
    }

    public void func_148833_a(INetHandlerPlayClient p_148923_1_)
    {
        p_148923_1_.func_147287_a(this);
    }

    public String func_148835_b()
    {
        return String.format("xc=%d, zc=%d, count=%d", new Object[] {Integer.valueOf(this.field_148925_b.chunkXPos), Integer.valueOf(this.field_148925_b.chunkZPos), Integer.valueOf(this.field_148924_d)});
    }

    @SideOnly(Side.CLIENT)
    public ChunkCoordIntPair func_148920_c()
    {
        return this.field_148925_b;
    }

    @SideOnly(Side.CLIENT)
    public byte[] func_148921_d()
    {
        return this.field_148926_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_148922_e()
    {
        return this.field_148924_d;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}