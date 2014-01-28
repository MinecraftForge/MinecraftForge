package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S28PacketEffect extends Packet
{
    private int field_149251_a;
    private int field_149249_b;
    private int field_149250_c;
    private int field_149247_d;
    private int field_149248_e;
    private boolean field_149246_f;
    private static final String __OBFID = "CL_00001307";

    public S28PacketEffect() {}

    public S28PacketEffect(int p_i45198_1_, int p_i45198_2_, int p_i45198_3_, int p_i45198_4_, int p_i45198_5_, boolean p_i45198_6_)
    {
        this.field_149251_a = p_i45198_1_;
        this.field_149250_c = p_i45198_2_;
        this.field_149247_d = p_i45198_3_;
        this.field_149248_e = p_i45198_4_;
        this.field_149249_b = p_i45198_5_;
        this.field_149246_f = p_i45198_6_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149251_a = p_148837_1_.readInt();
        this.field_149250_c = p_148837_1_.readInt();
        this.field_149247_d = p_148837_1_.readByte() & 255;
        this.field_149248_e = p_148837_1_.readInt();
        this.field_149249_b = p_148837_1_.readInt();
        this.field_149246_f = p_148837_1_.readBoolean();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149251_a);
        p_148840_1_.writeInt(this.field_149250_c);
        p_148840_1_.writeByte(this.field_149247_d & 255);
        p_148840_1_.writeInt(this.field_149248_e);
        p_148840_1_.writeInt(this.field_149249_b);
        p_148840_1_.writeBoolean(this.field_149246_f);
    }

    public void func_148833_a(INetHandlerPlayClient p_149245_1_)
    {
        p_149245_1_.func_147277_a(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149244_c()
    {
        return this.field_149246_f;
    }

    @SideOnly(Side.CLIENT)
    public int func_149242_d()
    {
        return this.field_149251_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149241_e()
    {
        return this.field_149249_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149240_f()
    {
        return this.field_149250_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_149243_g()
    {
        return this.field_149247_d;
    }

    @SideOnly(Side.CLIENT)
    public int func_149239_h()
    {
        return this.field_149248_e;
    }
}