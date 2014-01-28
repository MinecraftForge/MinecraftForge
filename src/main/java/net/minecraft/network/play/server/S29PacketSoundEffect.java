package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.Validate;

public class S29PacketSoundEffect extends Packet
{
    private String field_149219_a;
    private int field_149217_b;
    private int field_149218_c = Integer.MAX_VALUE;
    private int field_149215_d;
    private float field_149216_e;
    private int field_149214_f;
    private static final String __OBFID = "CL_00001309";

    public S29PacketSoundEffect() {}

    public S29PacketSoundEffect(String p_i45200_1_, double p_i45200_2_, double p_i45200_4_, double p_i45200_6_, float p_i45200_8_, float p_i45200_9_)
    {
        Validate.notNull(p_i45200_1_, "name", new Object[0]);
        this.field_149219_a = p_i45200_1_;
        this.field_149217_b = (int)(p_i45200_2_ * 8.0D);
        this.field_149218_c = (int)(p_i45200_4_ * 8.0D);
        this.field_149215_d = (int)(p_i45200_6_ * 8.0D);
        this.field_149216_e = p_i45200_8_;
        this.field_149214_f = (int)(p_i45200_9_ * 63.0F);

        if (this.field_149214_f < 0)
        {
            this.field_149214_f = 0;
        }

        if (this.field_149214_f > 255)
        {
            this.field_149214_f = 255;
        }
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149219_a = p_148837_1_.func_150789_c(256);
        this.field_149217_b = p_148837_1_.readInt();
        this.field_149218_c = p_148837_1_.readInt();
        this.field_149215_d = p_148837_1_.readInt();
        this.field_149216_e = p_148837_1_.readFloat();
        this.field_149214_f = p_148837_1_.readUnsignedByte();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(this.field_149219_a);
        p_148840_1_.writeInt(this.field_149217_b);
        p_148840_1_.writeInt(this.field_149218_c);
        p_148840_1_.writeInt(this.field_149215_d);
        p_148840_1_.writeFloat(this.field_149216_e);
        p_148840_1_.writeByte(this.field_149214_f);
    }

    @SideOnly(Side.CLIENT)
    public String func_149212_c()
    {
        return this.field_149219_a;
    }

    @SideOnly(Side.CLIENT)
    public double func_149207_d()
    {
        return (double)((float)this.field_149217_b / 8.0F);
    }

    @SideOnly(Side.CLIENT)
    public double func_149211_e()
    {
        return (double)((float)this.field_149218_c / 8.0F);
    }

    @SideOnly(Side.CLIENT)
    public double func_149210_f()
    {
        return (double)((float)this.field_149215_d / 8.0F);
    }

    @SideOnly(Side.CLIENT)
    public float func_149208_g()
    {
        return this.field_149216_e;
    }

    @SideOnly(Side.CLIENT)
    public float func_149209_h()
    {
        return (float)this.field_149214_f / 63.0F;
    }

    public void func_148833_a(INetHandlerPlayClient p_149213_1_)
    {
        p_149213_1_.func_147255_a(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}