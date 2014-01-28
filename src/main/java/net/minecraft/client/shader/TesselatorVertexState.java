package net.minecraft.client.shader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TesselatorVertexState
{
    private int[] field_147583_a;
    private int field_147581_b;
    private int field_147582_c;
    private boolean field_147579_d;
    private boolean field_147580_e;
    private boolean field_147577_f;
    private boolean field_147578_g;
    private static final String __OBFID = "CL_00000961";

    public TesselatorVertexState(int[] p_i45079_1_, int p_i45079_2_, int p_i45079_3_, boolean p_i45079_4_, boolean p_i45079_5_, boolean p_i45079_6_, boolean p_i45079_7_)
    {
        this.field_147583_a = p_i45079_1_;
        this.field_147581_b = p_i45079_2_;
        this.field_147582_c = p_i45079_3_;
        this.field_147579_d = p_i45079_4_;
        this.field_147580_e = p_i45079_5_;
        this.field_147577_f = p_i45079_6_;
        this.field_147578_g = p_i45079_7_;
    }

    public int[] func_147572_a()
    {
        return this.field_147583_a;
    }

    public int func_147576_b()
    {
        return this.field_147581_b;
    }

    public int func_147575_c()
    {
        return this.field_147582_c;
    }

    public boolean func_147573_d()
    {
        return this.field_147579_d;
    }

    public boolean func_147571_e()
    {
        return this.field_147580_e;
    }

    public boolean func_147570_f()
    {
        return this.field_147577_f;
    }

    public boolean func_147574_g()
    {
        return this.field_147578_g;
    }
}