package net.minecraft.client.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Comparator;

@SideOnly(Side.CLIENT)
public class QuadComparator implements Comparator
{
    private float field_147630_a;
    private float field_147628_b;
    private float field_147629_c;
    private int[] field_147627_d;
    private static final String __OBFID = "CL_00000958";

    public QuadComparator(int[] p_i45077_1_, float p_i45077_2_, float p_i45077_3_, float p_i45077_4_)
    {
        this.field_147627_d = p_i45077_1_;
        this.field_147630_a = p_i45077_2_;
        this.field_147628_b = p_i45077_3_;
        this.field_147629_c = p_i45077_4_;
    }

    public int compare(Integer p_147626_1_, Integer p_147626_2_)
    {
        float f = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue()]) - this.field_147630_a;
        float f1 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 1]) - this.field_147628_b;
        float f2 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 2]) - this.field_147629_c;
        float f3 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 8]) - this.field_147630_a;
        float f4 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 9]) - this.field_147628_b;
        float f5 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 10]) - this.field_147629_c;
        float f6 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 16]) - this.field_147630_a;
        float f7 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 17]) - this.field_147628_b;
        float f8 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 18]) - this.field_147629_c;
        float f9 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 24]) - this.field_147630_a;
        float f10 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 25]) - this.field_147628_b;
        float f11 = Float.intBitsToFloat(this.field_147627_d[p_147626_1_.intValue() + 26]) - this.field_147629_c;
        float f12 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue()]) - this.field_147630_a;
        float f13 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 1]) - this.field_147628_b;
        float f14 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 2]) - this.field_147629_c;
        float f15 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 8]) - this.field_147630_a;
        float f16 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 9]) - this.field_147628_b;
        float f17 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 10]) - this.field_147629_c;
        float f18 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 16]) - this.field_147630_a;
        float f19 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 17]) - this.field_147628_b;
        float f20 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 18]) - this.field_147629_c;
        float f21 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 24]) - this.field_147630_a;
        float f22 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 25]) - this.field_147628_b;
        float f23 = Float.intBitsToFloat(this.field_147627_d[p_147626_2_.intValue() + 26]) - this.field_147629_c;
        float f24 = (f + f3 + f6 + f9) * 0.25F;
        float f25 = (f1 + f4 + f7 + f10) * 0.25F;
        float f26 = (f2 + f5 + f8 + f11) * 0.25F;
        float f27 = (f12 + f15 + f18 + f21) * 0.25F;
        float f28 = (f13 + f16 + f19 + f22) * 0.25F;
        float f29 = (f14 + f17 + f20 + f23) * 0.25F;
        float f30 = f24 * f24 + f25 * f25 + f26 * f26;
        float f31 = f27 * f27 + f28 * f28 + f29 * f29;
        return Float.compare(f31, f30);
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.compare((Integer)par1Obj, (Integer)par2Obj);
    }
}