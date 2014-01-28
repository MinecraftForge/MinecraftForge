package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity extends Packet
{
    protected int field_149074_a;
    protected byte field_149072_b;
    protected byte field_149073_c;
    protected byte field_149070_d;
    protected byte field_149071_e;
    protected byte field_149068_f;
    protected boolean field_149069_g;
    private static final String __OBFID = "CL_00001312";

    public S14PacketEntity() {}

    public S14PacketEntity(int p_i45206_1_)
    {
        this.field_149074_a = p_i45206_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149074_a = p_148837_1_.readInt();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149074_a);
    }

    public void func_148833_a(INetHandlerPlayClient p_149067_1_)
    {
        p_149067_1_.func_147259_a(this);
    }

    public String func_148835_b()
    {
        return String.format("id=%d", new Object[] {Integer.valueOf(this.field_149074_a)});
    }

    public String toString()
    {
        return "Entity_" + super.toString();
    }

    @SideOnly(Side.CLIENT)
    public Entity func_149065_a(World p_149065_1_)
    {
        return p_149065_1_.getEntityByID(this.field_149074_a);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public byte func_149062_c()
    {
        return this.field_149072_b;
    }

    @SideOnly(Side.CLIENT)
    public byte func_149061_d()
    {
        return this.field_149073_c;
    }

    @SideOnly(Side.CLIENT)
    public byte func_149064_e()
    {
        return this.field_149070_d;
    }

    @SideOnly(Side.CLIENT)
    public byte func_149066_f()
    {
        return this.field_149071_e;
    }

    @SideOnly(Side.CLIENT)
    public byte func_149063_g()
    {
        return this.field_149068_f;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149060_h()
    {
        return this.field_149069_g;
    }

    public static class S16PacketEntityLook extends S14PacketEntity
        {
            private static final String __OBFID = "CL_00001315";

            public S16PacketEntityLook()
            {
                this.field_149069_g = true;
            }

            public S16PacketEntityLook(int p_i45205_1_, byte p_i45205_2_, byte p_i45205_3_)
            {
                super(p_i45205_1_);
                this.field_149071_e = p_i45205_2_;
                this.field_149068_f = p_i45205_3_;
                this.field_149069_g = true;
            }

            public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
            {
                super.func_148837_a(p_148837_1_);
                this.field_149071_e = p_148837_1_.readByte();
                this.field_149068_f = p_148837_1_.readByte();
            }

            public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
            {
                super.func_148840_b(p_148840_1_);
                p_148840_1_.writeByte(this.field_149071_e);
                p_148840_1_.writeByte(this.field_149068_f);
            }

            public String func_148835_b()
            {
                return super.func_148835_b() + String.format(", yRot=%d, xRot=%d", new Object[] {Byte.valueOf(this.field_149071_e), Byte.valueOf(this.field_149068_f)});
            }

            public void func_148833_a(INetHandler p_148833_1_)
            {
                super.func_148833_a((INetHandlerPlayClient)p_148833_1_);
            }
        }

    public static class S15PacketEntityRelMove extends S14PacketEntity
        {
            private static final String __OBFID = "CL_00001313";

            public S15PacketEntityRelMove() {}

            public S15PacketEntityRelMove(int p_i45203_1_, byte p_i45203_2_, byte p_i45203_3_, byte p_i45203_4_)
            {
                super(p_i45203_1_);
                this.field_149072_b = p_i45203_2_;
                this.field_149073_c = p_i45203_3_;
                this.field_149070_d = p_i45203_4_;
            }

            public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
            {
                super.func_148837_a(p_148837_1_);
                this.field_149072_b = p_148837_1_.readByte();
                this.field_149073_c = p_148837_1_.readByte();
                this.field_149070_d = p_148837_1_.readByte();
            }

            public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
            {
                super.func_148840_b(p_148840_1_);
                p_148840_1_.writeByte(this.field_149072_b);
                p_148840_1_.writeByte(this.field_149073_c);
                p_148840_1_.writeByte(this.field_149070_d);
            }

            public String func_148835_b()
            {
                return super.func_148835_b() + String.format(", xa=%d, ya=%d, za=%d", new Object[] {Byte.valueOf(this.field_149072_b), Byte.valueOf(this.field_149073_c), Byte.valueOf(this.field_149070_d)});
            }

            public void func_148833_a(INetHandler p_148833_1_)
            {
                super.func_148833_a((INetHandlerPlayClient)p_148833_1_);
            }
        }

    public static class S17PacketEntityLookMove extends S14PacketEntity
        {
            private static final String __OBFID = "CL_00001314";

            public S17PacketEntityLookMove()
            {
                this.field_149069_g = true;
            }

            public S17PacketEntityLookMove(int p_i45204_1_, byte p_i45204_2_, byte p_i45204_3_, byte p_i45204_4_, byte p_i45204_5_, byte p_i45204_6_)
            {
                super(p_i45204_1_);
                this.field_149072_b = p_i45204_2_;
                this.field_149073_c = p_i45204_3_;
                this.field_149070_d = p_i45204_4_;
                this.field_149071_e = p_i45204_5_;
                this.field_149068_f = p_i45204_6_;
                this.field_149069_g = true;
            }

            public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
            {
                super.func_148837_a(p_148837_1_);
                this.field_149072_b = p_148837_1_.readByte();
                this.field_149073_c = p_148837_1_.readByte();
                this.field_149070_d = p_148837_1_.readByte();
                this.field_149071_e = p_148837_1_.readByte();
                this.field_149068_f = p_148837_1_.readByte();
            }

            public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
            {
                super.func_148840_b(p_148840_1_);
                p_148840_1_.writeByte(this.field_149072_b);
                p_148840_1_.writeByte(this.field_149073_c);
                p_148840_1_.writeByte(this.field_149070_d);
                p_148840_1_.writeByte(this.field_149071_e);
                p_148840_1_.writeByte(this.field_149068_f);
            }

            public String func_148835_b()
            {
                return super.func_148835_b() + String.format(", xa=%d, ya=%d, za=%d, yRot=%d, xRot=%d", new Object[] {Byte.valueOf(this.field_149072_b), Byte.valueOf(this.field_149073_c), Byte.valueOf(this.field_149070_d), Byte.valueOf(this.field_149071_e), Byte.valueOf(this.field_149068_f)});
            }

            public void func_148833_a(INetHandler p_148833_1_)
            {
                super.func_148833_a((INetHandlerPlayClient)p_148833_1_);
            }
        }
}