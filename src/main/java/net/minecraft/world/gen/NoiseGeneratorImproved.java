package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator
{
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;
    private static final String __OBFID = "CL_00000534";

    public NoiseGeneratorImproved()
    {
        this(new Random());
    }

    public NoiseGeneratorImproved(Random p_i45469_1_)
    {
        this.permutations = new int[512];
        this.xCoord = p_i45469_1_.nextDouble() * 256.0D;
        this.yCoord = p_i45469_1_.nextDouble() * 256.0D;
        this.zCoord = p_i45469_1_.nextDouble() * 256.0D;
        int i;

        for (i = 0; i < 256; this.permutations[i] = i++)
        {
            ;
        }

        for (i = 0; i < 256; ++i)
        {
            int j = p_i45469_1_.nextInt(256 - i) + i;
            int k = this.permutations[i];
            this.permutations[i] = this.permutations[j];
            this.permutations[j] = k;
            this.permutations[i + 256] = this.permutations[i];
        }
    }

    public final double lerp(double par1, double par3, double par5)
    {
        return par3 + par1 * (par5 - par3);
    }

    public final double func_76309_a(int par1, double par2, double par4)
    {
        int j = par1 & 15;
        double d2 = (double)(1 - ((j & 8) >> 3)) * par2;
        double d3 = j < 4 ? 0.0D : (j != 12 && j != 14 ? par4 : par2);
        return ((j & 1) == 0 ? d2 : -d2) + ((j & 2) == 0 ? d3 : -d3);
    }

    public final double grad(int par1, double par2, double par4, double par6)
    {
        int j = par1 & 15;
        double d3 = j < 8 ? par2 : par4;
        double d4 = j < 4 ? par4 : (j != 12 && j != 14 ? par6 : par2);
        return ((j & 1) == 0 ? d3 : -d3) + ((j & 2) == 0 ? d4 : -d4);
    }

    // JAVADOC METHOD $$ func_76308_a
    public void populateNoiseArray(double[] par1ArrayOfDouble, double par2, double par4, double par6, int par8, int par9, int par10, double par11, double par13, double par15, double par17)
    {
        int l;
        int i1;
        double d9;
        double d11;
        double d12;
        int l1;
        double d13;
        int i2;
        int j2;
        int j6;
        int l5;

        if (par9 == 1)
        {
            boolean flag8 = false;
            boolean flag7 = false;
            boolean flag = false;
            boolean flag9 = false;
            double d22 = 0.0D;
            double d21 = 0.0D;
            l5 = 0;
            double d23 = 1.0D / par17;

            for (int j1 = 0; j1 < par8; ++j1)
            {
                d9 = par2 + (double)j1 * par11 + this.xCoord;
                int i6 = (int)d9;

                if (d9 < (double)i6)
                {
                    --i6;
                }

                int k1 = i6 & 255;
                d9 -= (double)i6;
                d11 = d9 * d9 * d9 * (d9 * (d9 * 6.0D - 15.0D) + 10.0D);

                for (l1 = 0; l1 < par10; ++l1)
                {
                    d12 = par6 + (double)l1 * par15 + this.zCoord;
                    i2 = (int)d12;

                    if (d12 < (double)i2)
                    {
                        --i2;
                    }

                    j2 = i2 & 255;
                    d12 -= (double)i2;
                    d13 = d12 * d12 * d12 * (d12 * (d12 * 6.0D - 15.0D) + 10.0D);
                    l = this.permutations[k1] + 0;
                    int i4 = this.permutations[l] + j2;
                    int k4 = this.permutations[k1 + 1] + 0;
                    i1 = this.permutations[k4] + j2;
                    d22 = this.lerp(d11, this.func_76309_a(this.permutations[i4], d9, d12), this.grad(this.permutations[i1], d9 - 1.0D, 0.0D, d12));
                    d21 = this.lerp(d11, this.grad(this.permutations[i4 + 1], d9, 0.0D, d12 - 1.0D), this.grad(this.permutations[i1 + 1], d9 - 1.0D, 0.0D, d12 - 1.0D));
                    double d24 = this.lerp(d13, d22, d21);
                    j6 = l5++;
                    par1ArrayOfDouble[j6] += d24 * d23;
                }
            }
        }
        else
        {
            l = 0;
            double d7 = 1.0D / par17;
            i1 = -1;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            boolean flag5 = false;
            boolean flag6 = false;
            double d8 = 0.0D;
            d9 = 0.0D;
            double d10 = 0.0D;
            d11 = 0.0D;

            for (l1 = 0; l1 < par8; ++l1)
            {
                d12 = par2 + (double)l1 * par11 + this.xCoord;
                i2 = (int)d12;

                if (d12 < (double)i2)
                {
                    --i2;
                }

                j2 = i2 & 255;
                d12 -= (double)i2;
                d13 = d12 * d12 * d12 * (d12 * (d12 * 6.0D - 15.0D) + 10.0D);

                for (int k2 = 0; k2 < par10; ++k2)
                {
                    double d14 = par6 + (double)k2 * par15 + this.zCoord;
                    int l2 = (int)d14;

                    if (d14 < (double)l2)
                    {
                        --l2;
                    }

                    int i3 = l2 & 255;
                    d14 -= (double)l2;
                    double d15 = d14 * d14 * d14 * (d14 * (d14 * 6.0D - 15.0D) + 10.0D);

                    for (int j3 = 0; j3 < par9; ++j3)
                    {
                        double d16 = par4 + (double)j3 * par13 + this.yCoord;
                        int k3 = (int)d16;

                        if (d16 < (double)k3)
                        {
                            --k3;
                        }

                        int l3 = k3 & 255;
                        d16 -= (double)k3;
                        double d17 = d16 * d16 * d16 * (d16 * (d16 * 6.0D - 15.0D) + 10.0D);

                        if (j3 == 0 || l3 != i1)
                        {
                            i1 = l3;
                            int j4 = this.permutations[j2] + l3;
                            int i5 = this.permutations[j4] + i3;
                            int l4 = this.permutations[j4 + 1] + i3;
                            int k5 = this.permutations[j2 + 1] + l3;
                            l5 = this.permutations[k5] + i3;
                            int j5 = this.permutations[k5 + 1] + i3;
                            d8 = this.lerp(d13, this.grad(this.permutations[i5], d12, d16, d14), this.grad(this.permutations[l5], d12 - 1.0D, d16, d14));
                            d9 = this.lerp(d13, this.grad(this.permutations[l4], d12, d16 - 1.0D, d14), this.grad(this.permutations[j5], d12 - 1.0D, d16 - 1.0D, d14));
                            d10 = this.lerp(d13, this.grad(this.permutations[i5 + 1], d12, d16, d14 - 1.0D), this.grad(this.permutations[l5 + 1], d12 - 1.0D, d16, d14 - 1.0D));
                            d11 = this.lerp(d13, this.grad(this.permutations[l4 + 1], d12, d16 - 1.0D, d14 - 1.0D), this.grad(this.permutations[j5 + 1], d12 - 1.0D, d16 - 1.0D, d14 - 1.0D));
                        }

                        double d18 = this.lerp(d17, d8, d9);
                        double d19 = this.lerp(d17, d10, d11);
                        double d20 = this.lerp(d15, d18, d19);
                        j6 = l++;
                        par1ArrayOfDouble[j6] += d20 * d7;
                    }
                }
            }
        }
    }
}