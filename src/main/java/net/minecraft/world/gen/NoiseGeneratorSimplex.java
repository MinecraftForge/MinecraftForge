package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplex
{
    private static int[][] field_151611_e = new int[][] {{1, 1, 0}, { -1, 1, 0}, {1, -1, 0}, { -1, -1, 0}, {1, 0, 1}, { -1, 0, 1}, {1, 0, -1}, { -1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    public static final double field_151614_a = Math.sqrt(3.0D);
    private int[] field_151608_f;
    public double field_151612_b;
    public double field_151613_c;
    public double field_151610_d;
    private static final double field_151609_g = 0.5D * (field_151614_a - 1.0D);
    private static final double field_151615_h = (3.0D - field_151614_a) / 6.0D;
    private static final String __OBFID = "CL_00000537";

    public NoiseGeneratorSimplex()
    {
        this(new Random());
    }

    public NoiseGeneratorSimplex(Random p_i45471_1_)
    {
        this.field_151608_f = new int[512];
        this.field_151612_b = p_i45471_1_.nextDouble() * 256.0D;
        this.field_151613_c = p_i45471_1_.nextDouble() * 256.0D;
        this.field_151610_d = p_i45471_1_.nextDouble() * 256.0D;
        int i;

        for (i = 0; i < 256; this.field_151608_f[i] = i++)
        {
            ;
        }

        for (i = 0; i < 256; ++i)
        {
            int j = p_i45471_1_.nextInt(256 - i) + i;
            int k = this.field_151608_f[i];
            this.field_151608_f[i] = this.field_151608_f[j];
            this.field_151608_f[j] = k;
            this.field_151608_f[i + 256] = this.field_151608_f[i];
        }
    }

    private static int func_151607_a(double p_151607_0_)
    {
        return p_151607_0_ > 0.0D ? (int)p_151607_0_ : (int)p_151607_0_ - 1;
    }

    private static double func_151604_a(int[] p_151604_0_, double p_151604_1_, double p_151604_3_)
    {
        return (double)p_151604_0_[0] * p_151604_1_ + (double)p_151604_0_[1] * p_151604_3_;
    }

    public double func_151605_a(double p_151605_1_, double p_151605_3_)
    {
        double d5 = 0.5D * (field_151614_a - 1.0D);
        double d6 = (p_151605_1_ + p_151605_3_) * d5;
        int i = func_151607_a(p_151605_1_ + d6);
        int j = func_151607_a(p_151605_3_ + d6);
        double d7 = (3.0D - field_151614_a) / 6.0D;
        double d8 = (double)(i + j) * d7;
        double d9 = (double)i - d8;
        double d10 = (double)j - d8;
        double d11 = p_151605_1_ - d9;
        double d12 = p_151605_3_ - d10;
        byte b0;
        byte b1;

        if (d11 > d12)
        {
            b0 = 1;
            b1 = 0;
        }
        else
        {
            b0 = 0;
            b1 = 1;
        }

        double d13 = d11 - (double)b0 + d7;
        double d14 = d12 - (double)b1 + d7;
        double d15 = d11 - 1.0D + 2.0D * d7;
        double d16 = d12 - 1.0D + 2.0D * d7;
        int k = i & 255;
        int l = j & 255;
        int i1 = this.field_151608_f[k + this.field_151608_f[l]] % 12;
        int j1 = this.field_151608_f[k + b0 + this.field_151608_f[l + b1]] % 12;
        int k1 = this.field_151608_f[k + 1 + this.field_151608_f[l + 1]] % 12;
        double d17 = 0.5D - d11 * d11 - d12 * d12;
        double d2;

        if (d17 < 0.0D)
        {
            d2 = 0.0D;
        }
        else
        {
            d17 *= d17;
            d2 = d17 * d17 * func_151604_a(field_151611_e[i1], d11, d12);
        }

        double d18 = 0.5D - d13 * d13 - d14 * d14;
        double d3;

        if (d18 < 0.0D)
        {
            d3 = 0.0D;
        }
        else
        {
            d18 *= d18;
            d3 = d18 * d18 * func_151604_a(field_151611_e[j1], d13, d14);
        }

        double d19 = 0.5D - d15 * d15 - d16 * d16;
        double d4;

        if (d19 < 0.0D)
        {
            d4 = 0.0D;
        }
        else
        {
            d19 *= d19;
            d4 = d19 * d19 * func_151604_a(field_151611_e[k1], d15, d16);
        }

        return 70.0D * (d2 + d3 + d4);
    }

    public void func_151606_a(double[] p_151606_1_, double p_151606_2_, double p_151606_4_, int p_151606_6_, int p_151606_7_, double p_151606_8_, double p_151606_10_, double p_151606_12_)
    {
        int k = 0;

        for (int l = 0; l < p_151606_7_; ++l)
        {
            double d5 = (p_151606_4_ + (double)l) * p_151606_10_ + this.field_151613_c;

            for (int i1 = 0; i1 < p_151606_6_; ++i1)
            {
                double d6 = (p_151606_2_ + (double)i1) * p_151606_8_ + this.field_151612_b;
                double d10 = (d6 + d5) * field_151609_g;
                int j1 = func_151607_a(d6 + d10);
                int k1 = func_151607_a(d5 + d10);
                double d11 = (double)(j1 + k1) * field_151615_h;
                double d12 = (double)j1 - d11;
                double d13 = (double)k1 - d11;
                double d14 = d6 - d12;
                double d15 = d5 - d13;
                byte b1;
                byte b0;

                if (d14 > d15)
                {
                    b0 = 1;
                    b1 = 0;
                }
                else
                {
                    b0 = 0;
                    b1 = 1;
                }

                double d16 = d14 - (double)b0 + field_151615_h;
                double d17 = d15 - (double)b1 + field_151615_h;
                double d18 = d14 - 1.0D + 2.0D * field_151615_h;
                double d19 = d15 - 1.0D + 2.0D * field_151615_h;
                int l1 = j1 & 255;
                int i2 = k1 & 255;
                int j2 = this.field_151608_f[l1 + this.field_151608_f[i2]] % 12;
                int k2 = this.field_151608_f[l1 + b0 + this.field_151608_f[i2 + b1]] % 12;
                int l2 = this.field_151608_f[l1 + 1 + this.field_151608_f[i2 + 1]] % 12;
                double d20 = 0.5D - d14 * d14 - d15 * d15;
                double d7;

                if (d20 < 0.0D)
                {
                    d7 = 0.0D;
                }
                else
                {
                    d20 *= d20;
                    d7 = d20 * d20 * func_151604_a(field_151611_e[j2], d14, d15);
                }

                double d21 = 0.5D - d16 * d16 - d17 * d17;
                double d8;

                if (d21 < 0.0D)
                {
                    d8 = 0.0D;
                }
                else
                {
                    d21 *= d21;
                    d8 = d21 * d21 * func_151604_a(field_151611_e[k2], d16, d17);
                }

                double d22 = 0.5D - d18 * d18 - d19 * d19;
                double d9;

                if (d22 < 0.0D)
                {
                    d9 = 0.0D;
                }
                else
                {
                    d22 *= d22;
                    d9 = d22 * d22 * func_151604_a(field_151611_e[l2], d18, d19);
                }

                int i3 = k++;
                p_151606_1_[i3] += 70.0D * (d7 + d8 + d9) * p_151606_12_;
            }
        }
    }
}