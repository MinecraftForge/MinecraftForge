package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator
{
    // JAVADOC FIELD $$ field_76307_a
    private NoiseGeneratorImproved[] generatorCollection;
    private int octaves;
    private static final String __OBFID = "CL_00000535";

    public NoiseGeneratorOctaves(Random par1Random, int par2)
    {
        this.octaves = par2;
        this.generatorCollection = new NoiseGeneratorImproved[par2];

        for (int j = 0; j < par2; ++j)
        {
            this.generatorCollection[j] = new NoiseGeneratorImproved(par1Random);
        }
    }

    // JAVADOC METHOD $$ func_76304_a
    public double[] generateNoiseOctaves(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7, double par8, double par10, double par12)
    {
        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }
        else
        {
            for (int k1 = 0; k1 < par1ArrayOfDouble.length; ++k1)
            {
                par1ArrayOfDouble[k1] = 0.0D;
            }
        }

        double d6 = 1.0D;

        for (int l1 = 0; l1 < this.octaves; ++l1)
        {
            double d3 = (double)par2 * d6 * par8;
            double d4 = (double)par3 * d6 * par10;
            double d5 = (double)par4 * d6 * par12;
            long i2 = MathHelper.floor_double_long(d3);
            long j2 = MathHelper.floor_double_long(d5);
            d3 -= (double)i2;
            d5 -= (double)j2;
            i2 %= 16777216L;
            j2 %= 16777216L;
            d3 += (double)i2;
            d5 += (double)j2;
            this.generatorCollection[l1].populateNoiseArray(par1ArrayOfDouble, d3, d4, d5, par5, par6, par7, par8 * d6, par10 * d6, par12 * d6, d6);
            d6 /= 2.0D;
        }

        return par1ArrayOfDouble;
    }

    // JAVADOC METHOD $$ func_76305_a
    public double[] generateNoiseOctaves(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, double par6, double par8, double par10)
    {
        return this.generateNoiseOctaves(par1ArrayOfDouble, par2, 10, par3, par4, 1, par5, par6, 1.0D, par8);
    }
}