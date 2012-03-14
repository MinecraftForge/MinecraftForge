package net.minecraft.src;

import java.util.Random;

public class MapGenCavesHell extends MapGenBase
{
    /**
     * Generates a larger initial cave node than usual. Called 25% of the time.
     */
    protected void generateLargeCaveNode(int par1, int par2, byte[] par3ArrayOfByte, double par4, double par6, double par8)
    {
        this.generateCaveNode(par1, par2, par3ArrayOfByte, par4, par6, par8, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    /**
     * Generates a node in the current cave system recursion tree.
     */
    protected void generateCaveNode(int par1, int par2, byte[] par3ArrayOfByte, double par4, double par6, double par8, float par10, float par11, float par12, int par13, int par14, double par15)
    {
        double var17 = (double)(par1 * 16 + 8);
        double var19 = (double)(par2 * 16 + 8);
        float var21 = 0.0F;
        float var22 = 0.0F;
        Random var23 = new Random(this.rand.nextLong());

        if (par14 <= 0)
        {
            int var24 = this.range * 16 - 16;
            par14 = var24 - var23.nextInt(var24 / 4);
        }

        boolean var51 = false;

        if (par13 == -1)
        {
            par13 = par14 / 2;
            var51 = true;
        }

        int var25 = var23.nextInt(par14 / 2) + par14 / 4;

        for (boolean var26 = var23.nextInt(6) == 0; par13 < par14; ++par13)
        {
            double var27 = 1.5D + (double)(MathHelper.sin((float)par13 * (float)Math.PI / (float)par14) * par10 * 1.0F);
            double var29 = var27 * par15;
            float var31 = MathHelper.cos(par12);
            float var32 = MathHelper.sin(par12);
            par4 += (double)(MathHelper.cos(par11) * var31);
            par6 += (double)var32;
            par8 += (double)(MathHelper.sin(par11) * var31);

            if (var26)
            {
                par12 *= 0.92F;
            }
            else
            {
                par12 *= 0.7F;
            }

            par12 += var22 * 0.1F;
            par11 += var21 * 0.1F;
            var22 *= 0.9F;
            var21 *= 0.75F;
            var22 += (var23.nextFloat() - var23.nextFloat()) * var23.nextFloat() * 2.0F;
            var21 += (var23.nextFloat() - var23.nextFloat()) * var23.nextFloat() * 4.0F;

            if (!var51 && par13 == var25 && par10 > 1.0F)
            {
                this.generateCaveNode(par1, par2, par3ArrayOfByte, par4, par6, par8, var23.nextFloat() * 0.5F + 0.5F, par11 - ((float)Math.PI / 2F), par12 / 3.0F, par13, par14, 1.0D);
                this.generateCaveNode(par1, par2, par3ArrayOfByte, par4, par6, par8, var23.nextFloat() * 0.5F + 0.5F, par11 + ((float)Math.PI / 2F), par12 / 3.0F, par13, par14, 1.0D);
                return;
            }

            if (var51 || var23.nextInt(4) != 0)
            {
                double var33 = par4 - var17;
                double var35 = par8 - var19;
                double var37 = (double)(par14 - par13);
                double var39 = (double)(par10 + 2.0F + 16.0F);

                if (var33 * var33 + var35 * var35 - var37 * var37 > var39 * var39)
                {
                    return;
                }

                if (par4 >= var17 - 16.0D - var27 * 2.0D && par8 >= var19 - 16.0D - var27 * 2.0D && par4 <= var17 + 16.0D + var27 * 2.0D && par8 <= var19 + 16.0D + var27 * 2.0D)
                {
                    int var52 = MathHelper.floor_double(par4 - var27) - par1 * 16 - 1;
                    int var34 = MathHelper.floor_double(par4 + var27) - par1 * 16 + 1;
                    int var53 = MathHelper.floor_double(par6 - var29) - 1;
                    int var36 = MathHelper.floor_double(par6 + var29) + 1;
                    int var55 = MathHelper.floor_double(par8 - var27) - par2 * 16 - 1;
                    int var38 = MathHelper.floor_double(par8 + var27) - par2 * 16 + 1;

                    if (var52 < 0)
                    {
                        var52 = 0;
                    }

                    if (var34 > 16)
                    {
                        var34 = 16;
                    }

                    if (var53 < 1)
                    {
                        var53 = 1;
                    }

                    if (var36 > 120)
                    {
                        var36 = 120;
                    }

                    if (var55 < 0)
                    {
                        var55 = 0;
                    }

                    if (var38 > 16)
                    {
                        var38 = 16;
                    }

                    boolean var54 = false;
                    int var43;
                    int var40;

                    for (var40 = var52; !var54 && var40 < var34; ++var40)
                    {
                        for (int var41 = var55; !var54 && var41 < var38; ++var41)
                        {
                            for (int var42 = var36 + 1; !var54 && var42 >= var53 - 1; --var42)
                            {
                                var43 = (var40 * 16 + var41) * 128 + var42;

                                if (var42 >= 0 && var42 < 128)
                                {
                                    if (par3ArrayOfByte[var43] == Block.lavaMoving.blockID || par3ArrayOfByte[var43] == Block.lavaStill.blockID)
                                    {
                                        var54 = true;
                                    }

                                    if (var42 != var53 - 1 && var40 != var52 && var40 != var34 - 1 && var41 != var55 && var41 != var38 - 1)
                                    {
                                        var42 = var53;
                                    }
                                }
                            }
                        }
                    }

                    if (!var54)
                    {
                        for (var40 = var52; var40 < var34; ++var40)
                        {
                            double var56 = ((double)(var40 + par1 * 16) + 0.5D - par4) / var27;

                            for (var43 = var55; var43 < var38; ++var43)
                            {
                                double var44 = ((double)(var43 + par2 * 16) + 0.5D - par8) / var27;
                                int var46 = (var40 * 16 + var43) * 128 + var36;

                                for (int var47 = var36 - 1; var47 >= var53; --var47)
                                {
                                    double var48 = ((double)var47 + 0.5D - par6) / var29;

                                    if (var48 > -0.7D && var56 * var56 + var48 * var48 + var44 * var44 < 1.0D)
                                    {
                                        byte var50 = par3ArrayOfByte[var46];

                                        if (var50 == Block.netherrack.blockID || var50 == Block.dirt.blockID || var50 == Block.grass.blockID)
                                        {
                                            par3ArrayOfByte[var46] = 0;
                                        }
                                    }

                                    --var46;
                                }
                            }
                        }

                        if (var51)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte)
    {
        int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);

        if (this.rand.nextInt(5) != 0)
        {
            var7 = 0;
        }

        for (int var8 = 0; var8 < var7; ++var8)
        {
            double var9 = (double)(par2 * 16 + this.rand.nextInt(16));
            double var11 = (double)this.rand.nextInt(128);
            double var13 = (double)(par3 * 16 + this.rand.nextInt(16));
            int var15 = 1;

            if (this.rand.nextInt(4) == 0)
            {
                this.generateLargeCaveNode(par4, par5, par6ArrayOfByte, var9, var11, var13);
                var15 += this.rand.nextInt(4);
            }

            for (int var16 = 0; var16 < var15; ++var16)
            {
                float var17 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float var18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float var19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                this.generateCaveNode(par4, par5, par6ArrayOfByte, var9, var11, var13, var19 * 2.0F, var17, var18, 0, 0, 0.5D);
            }
        }
    }
}
