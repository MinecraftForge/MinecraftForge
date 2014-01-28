package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

public class MathHelper
{
    // JAVADOC FIELD $$ field_76144_a
    private static float[] SIN_TABLE = new float[65536];
    private static final int[] field_151242_b;
    private static final String __OBFID = "CL_00001496";

    // JAVADOC METHOD $$ func_76126_a
    public static final float sin(float par0)
    {
        return SIN_TABLE[(int)(par0 * 10430.378F) & 65535];
    }

    // JAVADOC METHOD $$ func_76134_b
    public static final float cos(float par0)
    {
        return SIN_TABLE[(int)(par0 * 10430.378F + 16384.0F) & 65535];
    }

    public static final float sqrt_float(float par0)
    {
        return (float)Math.sqrt((double)par0);
    }

    public static final float sqrt_double(double par0)
    {
        return (float)Math.sqrt(par0);
    }

    // JAVADOC METHOD $$ func_76141_d
    public static int floor_float(float par0)
    {
        int i = (int)par0;
        return par0 < (float)i ? i - 1 : i;
    }

    // JAVADOC METHOD $$ func_76140_b
    @SideOnly(Side.CLIENT)
    public static int truncateDoubleToInt(double par0)
    {
        return (int)(par0 + 1024.0D) - 1024;
    }

    // JAVADOC METHOD $$ func_76128_c
    public static int floor_double(double par0)
    {
        int i = (int)par0;
        return par0 < (double)i ? i - 1 : i;
    }

    // JAVADOC METHOD $$ func_76124_d
    public static long floor_double_long(double par0)
    {
        long i = (long)par0;
        return par0 < (double)i ? i - 1L : i;
    }

    public static float abs(float par0)
    {
        return par0 >= 0.0F ? par0 : -par0;
    }

    // JAVADOC METHOD $$ func_76130_a
    public static int abs_int(int par0)
    {
        return par0 >= 0 ? par0 : -par0;
    }

    public static int ceiling_float_int(float par0)
    {
        int i = (int)par0;
        return par0 > (float)i ? i + 1 : i;
    }

    public static int ceiling_double_int(double par0)
    {
        int i = (int)par0;
        return par0 > (double)i ? i + 1 : i;
    }

    // JAVADOC METHOD $$ func_76125_a
    public static int clamp_int(int par0, int par1, int par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    // JAVADOC METHOD $$ func_76131_a
    public static float clamp_float(float par0, float par1, float par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    public static double func_151237_a(double p_151237_0_, double p_151237_2_, double p_151237_4_)
    {
        return p_151237_0_ < p_151237_2_ ? p_151237_2_ : (p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_);
    }

    public static double func_151238_b(double p_151238_0_, double p_151238_2_, double p_151238_4_)
    {
        return p_151238_4_ < 0.0D ? p_151238_0_ : (p_151238_4_ > 1.0D ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
    }

    // JAVADOC METHOD $$ func_76132_a
    public static double abs_max(double par0, double par2)
    {
        if (par0 < 0.0D)
        {
            par0 = -par0;
        }

        if (par2 < 0.0D)
        {
            par2 = -par2;
        }

        return par0 > par2 ? par0 : par2;
    }

    // JAVADOC METHOD $$ func_76137_a
    @SideOnly(Side.CLIENT)
    public static int bucketInt(int par0, int par1)
    {
        return par0 < 0 ? -((-par0 - 1) / par1) - 1 : par0 / par1;
    }

    // JAVADOC METHOD $$ func_76139_a
    @SideOnly(Side.CLIENT)
    public static boolean stringNullOrLengthZero(String par0Str)
    {
        return par0Str == null || par0Str.length() == 0;
    }

    public static int getRandomIntegerInRange(Random par0Random, int par1, int par2)
    {
        return par1 >= par2 ? par1 : par0Random.nextInt(par2 - par1 + 1) + par1;
    }

    public static float func_151240_a(Random p_151240_0_, float p_151240_1_, float p_151240_2_)
    {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random par0Random, double par1, double par3)
    {
        return par1 >= par3 ? par1 : par0Random.nextDouble() * (par3 - par1) + par1;
    }

    public static double average(long[] par0ArrayOfLong)
    {
        long i = 0L;
        long[] along1 = par0ArrayOfLong;
        int j = par0ArrayOfLong.length;

        for (int k = 0; k < j; ++k)
        {
            long l = along1[k];
            i += l;
        }

        return (double)i / (double)par0ArrayOfLong.length;
    }

    // JAVADOC METHOD $$ func_76142_g
    public static float wrapAngleTo180_float(float par0)
    {
        par0 %= 360.0F;

        if (par0 >= 180.0F)
        {
            par0 -= 360.0F;
        }

        if (par0 < -180.0F)
        {
            par0 += 360.0F;
        }

        return par0;
    }

    // JAVADOC METHOD $$ func_76138_g
    public static double wrapAngleTo180_double(double par0)
    {
        par0 %= 360.0D;

        if (par0 >= 180.0D)
        {
            par0 -= 360.0D;
        }

        if (par0 < -180.0D)
        {
            par0 += 360.0D;
        }

        return par0;
    }

    // JAVADOC METHOD $$ func_82715_a
    public static int parseIntWithDefault(String par0Str, int par1)
    {
        int j = par1;

        try
        {
            j = Integer.parseInt(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        return j;
    }

    // JAVADOC METHOD $$ func_82714_a
    public static int parseIntWithDefaultAndMax(String par0Str, int par1, int par2)
    {
        int k = par1;

        try
        {
            k = Integer.parseInt(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        if (k < par2)
        {
            k = par2;
        }

        return k;
    }

    // JAVADOC METHOD $$ func_82712_a
    public static double parseDoubleWithDefault(String par0Str, double par1)
    {
        double d1 = par1;

        try
        {
            d1 = Double.parseDouble(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        return d1;
    }

    public static double func_82713_a(String par0Str, double par1, double par3)
    {
        double d2 = par1;

        try
        {
            d2 = Double.parseDouble(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        if (d2 < par3)
        {
            d2 = par3;
        }

        return d2;
    }

    @SideOnly(Side.CLIENT)
    public static int func_151236_b(int p_151236_0_)
    {
        int j = p_151236_0_ - 1;
        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    @SideOnly(Side.CLIENT)
    private static boolean func_151235_d(int p_151235_0_)
    {
        return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
    }

    @SideOnly(Side.CLIENT)
    private static int func_151241_e(int p_151241_0_)
    {
        p_151241_0_ = func_151235_d(p_151241_0_) ? p_151241_0_ : func_151236_b(p_151241_0_);
        return field_151242_b[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
    }

    @SideOnly(Side.CLIENT)
    public static int func_151239_c(int p_151239_0_)
    {
        return func_151241_e(p_151239_0_) - (func_151235_d(p_151239_0_) ? 0 : 1);
    }

    static
    {
        for (int var0 = 0; var0 < 65536; ++var0)
        {
            SIN_TABLE[var0] = (float)Math.sin((double)var0 * Math.PI * 2.0D / 65536.0D);
        }

        field_151242_b = new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    }
}