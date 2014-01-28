package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S39PacketPlayerAbilities extends Packet
{
    private boolean field_149119_a;
    private boolean field_149117_b;
    private boolean field_149118_c;
    private boolean field_149115_d;
    private float field_149116_e;
    private float field_149114_f;
    private static final String __OBFID = "CL_00001317";

    public S39PacketPlayerAbilities() {}

    public S39PacketPlayerAbilities(PlayerCapabilities p_i45208_1_)
    {
        this.func_149108_a(p_i45208_1_.disableDamage);
        this.func_149102_b(p_i45208_1_.isFlying);
        this.func_149109_c(p_i45208_1_.allowFlying);
        this.func_149111_d(p_i45208_1_.isCreativeMode);
        this.func_149104_a(p_i45208_1_.getFlySpeed());
        this.func_149110_b(p_i45208_1_.getWalkSpeed());
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        byte b0 = p_148837_1_.readByte();
        this.func_149108_a((b0 & 1) > 0);
        this.func_149102_b((b0 & 2) > 0);
        this.func_149109_c((b0 & 4) > 0);
        this.func_149111_d((b0 & 8) > 0);
        this.func_149104_a(p_148837_1_.readFloat());
        this.func_149110_b(p_148837_1_.readFloat());
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        byte b0 = 0;

        if (this.func_149112_c())
        {
            b0 = (byte)(b0 | 1);
        }

        if (this.func_149106_d())
        {
            b0 = (byte)(b0 | 2);
        }

        if (this.func_149105_e())
        {
            b0 = (byte)(b0 | 4);
        }

        if (this.func_149103_f())
        {
            b0 = (byte)(b0 | 8);
        }

        p_148840_1_.writeByte(b0);
        p_148840_1_.writeFloat(this.field_149116_e);
        p_148840_1_.writeFloat(this.field_149114_f);
    }

    public void func_148833_a(INetHandlerPlayClient p_149113_1_)
    {
        p_149113_1_.func_147270_a(this);
    }

    public String func_148835_b()
    {
        return String.format("invuln=%b, flying=%b, canfly=%b, instabuild=%b, flyspeed=%.4f, walkspped=%.4f", new Object[] {Boolean.valueOf(this.func_149112_c()), Boolean.valueOf(this.func_149106_d()), Boolean.valueOf(this.func_149105_e()), Boolean.valueOf(this.func_149103_f()), Float.valueOf(this.func_149101_g()), Float.valueOf(this.func_149107_h())});
    }

    public boolean func_149112_c()
    {
        return this.field_149119_a;
    }

    public void func_149108_a(boolean p_149108_1_)
    {
        this.field_149119_a = p_149108_1_;
    }

    public boolean func_149106_d()
    {
        return this.field_149117_b;
    }

    public void func_149102_b(boolean p_149102_1_)
    {
        this.field_149117_b = p_149102_1_;
    }

    public boolean func_149105_e()
    {
        return this.field_149118_c;
    }

    public void func_149109_c(boolean p_149109_1_)
    {
        this.field_149118_c = p_149109_1_;
    }

    public boolean func_149103_f()
    {
        return this.field_149115_d;
    }

    public void func_149111_d(boolean p_149111_1_)
    {
        this.field_149115_d = p_149111_1_;
    }

    public float func_149101_g()
    {
        return this.field_149116_e;
    }

    public void func_149104_a(float p_149104_1_)
    {
        this.field_149116_e = p_149104_1_;
    }

    public float func_149107_h()
    {
        return this.field_149114_f;
    }

    public void func_149110_b(float p_149110_1_)
    {
        this.field_149114_f = p_149110_1_;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}