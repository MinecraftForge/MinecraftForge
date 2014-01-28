package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S2CPacketSpawnGlobalEntity extends Packet
{
    private int field_149059_a;
    private int field_149057_b;
    private int field_149058_c;
    private int field_149055_d;
    private int field_149056_e;
    private static final String __OBFID = "CL_00001278";

    public S2CPacketSpawnGlobalEntity() {}

    public S2CPacketSpawnGlobalEntity(Entity p_i45191_1_)
    {
        this.field_149059_a = p_i45191_1_.func_145782_y();
        this.field_149057_b = MathHelper.floor_double(p_i45191_1_.posX * 32.0D);
        this.field_149058_c = MathHelper.floor_double(p_i45191_1_.posY * 32.0D);
        this.field_149055_d = MathHelper.floor_double(p_i45191_1_.posZ * 32.0D);

        if (p_i45191_1_ instanceof EntityLightningBolt)
        {
            this.field_149056_e = 1;
        }
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149059_a = p_148837_1_.func_150792_a();
        this.field_149056_e = p_148837_1_.readByte();
        this.field_149057_b = p_148837_1_.readInt();
        this.field_149058_c = p_148837_1_.readInt();
        this.field_149055_d = p_148837_1_.readInt();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150787_b(this.field_149059_a);
        p_148840_1_.writeByte(this.field_149056_e);
        p_148840_1_.writeInt(this.field_149057_b);
        p_148840_1_.writeInt(this.field_149058_c);
        p_148840_1_.writeInt(this.field_149055_d);
    }

    public void func_148833_a(INetHandlerPlayClient p_149054_1_)
    {
        p_149054_1_.func_147292_a(this);
    }

    public String func_148835_b()
    {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", new Object[] {Integer.valueOf(this.field_149059_a), Integer.valueOf(this.field_149056_e), Float.valueOf((float)this.field_149057_b / 32.0F), Float.valueOf((float)this.field_149058_c / 32.0F), Float.valueOf((float)this.field_149055_d / 32.0F)});
    }

    @SideOnly(Side.CLIENT)
    public int func_149052_c()
    {
        return this.field_149059_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149051_d()
    {
        return this.field_149057_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149050_e()
    {
        return this.field_149058_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_149049_f()
    {
        return this.field_149055_d;
    }

    @SideOnly(Side.CLIENT)
    public int func_149053_g()
    {
        return this.field_149056_e;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}