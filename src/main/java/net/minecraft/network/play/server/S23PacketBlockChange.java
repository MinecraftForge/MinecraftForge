package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S23PacketBlockChange extends Packet
{
    private int field_148887_a;
    private int field_148885_b;
    private int field_148886_c;
    public Block field_148883_d;
    public int field_148884_e;
    private static final String __OBFID = "CL_00001287";

    public S23PacketBlockChange() {}

    public S23PacketBlockChange(int p_i45177_1_, int p_i45177_2_, int p_i45177_3_, World p_i45177_4_)
    {
        this.field_148887_a = p_i45177_1_;
        this.field_148885_b = p_i45177_2_;
        this.field_148886_c = p_i45177_3_;
        this.field_148883_d = p_i45177_4_.func_147439_a(p_i45177_1_, p_i45177_2_, p_i45177_3_);
        this.field_148884_e = p_i45177_4_.getBlockMetadata(p_i45177_1_, p_i45177_2_, p_i45177_3_);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_148887_a = p_148837_1_.readInt();
        this.field_148885_b = p_148837_1_.readUnsignedByte();
        this.field_148886_c = p_148837_1_.readInt();
        this.field_148883_d = Block.func_149729_e(p_148837_1_.func_150792_a());
        this.field_148884_e = p_148837_1_.readUnsignedByte();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_148887_a);
        p_148840_1_.writeByte(this.field_148885_b);
        p_148840_1_.writeInt(this.field_148886_c);
        p_148840_1_.func_150787_b(Block.func_149682_b(this.field_148883_d));
        p_148840_1_.writeByte(this.field_148884_e);
    }

    public void func_148833_a(INetHandlerPlayClient p_148882_1_)
    {
        p_148882_1_.func_147234_a(this);
    }

    public String func_148835_b()
    {
        return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", new Object[] {Integer.valueOf(Block.func_149682_b(this.field_148883_d)), Integer.valueOf(this.field_148884_e), Integer.valueOf(this.field_148887_a), Integer.valueOf(this.field_148885_b), Integer.valueOf(this.field_148886_c)});
    }

    @SideOnly(Side.CLIENT)
    public Block func_148880_c()
    {
        return this.field_148883_d;
    }

    @SideOnly(Side.CLIENT)
    public int func_148879_d()
    {
        return this.field_148887_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_148878_e()
    {
        return this.field_148885_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_148877_f()
    {
        return this.field_148886_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_148881_g()
    {
        return this.field_148884_e;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}