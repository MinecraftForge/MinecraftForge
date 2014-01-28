package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

public class S27PacketExplosion extends Packet
{
    private double field_149158_a;
    private double field_149156_b;
    private double field_149157_c;
    private float field_149154_d;
    private List field_149155_e;
    private float field_149152_f;
    private float field_149153_g;
    private float field_149159_h;
    private static final String __OBFID = "CL_00001300";

    public S27PacketExplosion() {}

    public S27PacketExplosion(double p_i45193_1_, double p_i45193_3_, double p_i45193_5_, float p_i45193_7_, List p_i45193_8_, Vec3 p_i45193_9_)
    {
        this.field_149158_a = p_i45193_1_;
        this.field_149156_b = p_i45193_3_;
        this.field_149157_c = p_i45193_5_;
        this.field_149154_d = p_i45193_7_;
        this.field_149155_e = new ArrayList(p_i45193_8_);

        if (p_i45193_9_ != null)
        {
            this.field_149152_f = (float)p_i45193_9_.xCoord;
            this.field_149153_g = (float)p_i45193_9_.yCoord;
            this.field_149159_h = (float)p_i45193_9_.zCoord;
        }
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149158_a = (double)p_148837_1_.readFloat();
        this.field_149156_b = (double)p_148837_1_.readFloat();
        this.field_149157_c = (double)p_148837_1_.readFloat();
        this.field_149154_d = p_148837_1_.readFloat();
        int i = p_148837_1_.readInt();
        this.field_149155_e = new ArrayList(i);
        int j = (int)this.field_149158_a;
        int k = (int)this.field_149156_b;
        int l = (int)this.field_149157_c;

        for (int i1 = 0; i1 < i; ++i1)
        {
            int j1 = p_148837_1_.readByte() + j;
            int k1 = p_148837_1_.readByte() + k;
            int l1 = p_148837_1_.readByte() + l;
            this.field_149155_e.add(new ChunkPosition(j1, k1, l1));
        }

        this.field_149152_f = p_148837_1_.readFloat();
        this.field_149153_g = p_148837_1_.readFloat();
        this.field_149159_h = p_148837_1_.readFloat();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeFloat((float)this.field_149158_a);
        p_148840_1_.writeFloat((float)this.field_149156_b);
        p_148840_1_.writeFloat((float)this.field_149157_c);
        p_148840_1_.writeFloat(this.field_149154_d);
        p_148840_1_.writeInt(this.field_149155_e.size());
        int i = (int)this.field_149158_a;
        int j = (int)this.field_149156_b;
        int k = (int)this.field_149157_c;
        Iterator iterator = this.field_149155_e.iterator();

        while (iterator.hasNext())
        {
            ChunkPosition chunkposition = (ChunkPosition)iterator.next();
            int l = chunkposition.field_151329_a - i;
            int i1 = chunkposition.field_151327_b - j;
            int j1 = chunkposition.field_151328_c - k;
            p_148840_1_.writeByte(l);
            p_148840_1_.writeByte(i1);
            p_148840_1_.writeByte(j1);
        }

        p_148840_1_.writeFloat(this.field_149152_f);
        p_148840_1_.writeFloat(this.field_149153_g);
        p_148840_1_.writeFloat(this.field_149159_h);
    }

    public void func_148833_a(INetHandlerPlayClient p_149151_1_)
    {
        p_149151_1_.func_147283_a(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public float func_149149_c()
    {
        return this.field_149152_f;
    }

    @SideOnly(Side.CLIENT)
    public float func_149144_d()
    {
        return this.field_149153_g;
    }

    @SideOnly(Side.CLIENT)
    public float func_149147_e()
    {
        return this.field_149159_h;
    }

    @SideOnly(Side.CLIENT)
    public double func_149148_f()
    {
        return this.field_149158_a;
    }

    @SideOnly(Side.CLIENT)
    public double func_149143_g()
    {
        return this.field_149156_b;
    }

    @SideOnly(Side.CLIENT)
    public double func_149145_h()
    {
        return this.field_149157_c;
    }

    @SideOnly(Side.CLIENT)
    public float func_149146_i()
    {
        return this.field_149154_d;
    }

    @SideOnly(Side.CLIENT)
    public List func_149150_j()
    {
        return this.field_149155_e;
    }
}