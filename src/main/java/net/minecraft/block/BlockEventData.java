package net.minecraft.block;

public class BlockEventData
{
    private int field_151348_a;
    private int field_151346_b;
    private int field_151347_c;
    private Block field_151344_d;
    private int field_151345_e;
    private int field_151343_f;
    private static final String __OBFID = "CL_00000131";

    public BlockEventData(int p_i45362_1_, int p_i45362_2_, int p_i45362_3_, Block p_i45362_4_, int p_i45362_5_, int p_i45362_6_)
    {
        this.field_151348_a = p_i45362_1_;
        this.field_151346_b = p_i45362_2_;
        this.field_151347_c = p_i45362_3_;
        this.field_151345_e = p_i45362_5_;
        this.field_151343_f = p_i45362_6_;
        this.field_151344_d = p_i45362_4_;
    }

    public int func_151340_a()
    {
        return this.field_151348_a;
    }

    public int func_151342_b()
    {
        return this.field_151346_b;
    }

    public int func_151341_c()
    {
        return this.field_151347_c;
    }

    public int func_151339_d()
    {
        return this.field_151345_e;
    }

    public int func_151338_e()
    {
        return this.field_151343_f;
    }

    public Block func_151337_f()
    {
        return this.field_151344_d;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof BlockEventData))
        {
            return false;
        }
        else
        {
            BlockEventData blockeventdata = (BlockEventData)par1Obj;
            return this.field_151348_a == blockeventdata.field_151348_a && this.field_151346_b == blockeventdata.field_151346_b && this.field_151347_c == blockeventdata.field_151347_c && this.field_151345_e == blockeventdata.field_151345_e && this.field_151343_f == blockeventdata.field_151343_f && this.field_151344_d == blockeventdata.field_151344_d;
        }
    }

    public String toString()
    {
        return "TE(" + this.field_151348_a + "," + this.field_151346_b + "," + this.field_151347_c + ")," + this.field_151345_e + "," + this.field_151343_f + "," + this.field_151344_d;
    }
}