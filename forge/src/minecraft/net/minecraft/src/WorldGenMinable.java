package net.minecraft.src;

import java.util.Random;

public class WorldGenMinable extends WorldGenerator
{
    /** The block ID of the ore to be placed using this generator. */
    private int minableBlockId;

    /** The number of blocks to generate. */
    private int numberOfBlocks;

    public WorldGenMinable(int par1, int par2)
    {
        this.minableBlockId = par1;
        this.numberOfBlocks = par2;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        float var6 = par2Random.nextFloat() * (float)Math.PI;
        double var7 = (double)((float)(par3 + 8) + MathHelper.sin(var6) * (float)this.numberOfBlocks / 8.0F);
        double var9 = (double)((float)(par3 + 8) - MathHelper.sin(var6) * (float)this.numberOfBlocks / 8.0F);
        double var11 = (double)((float)(par5 + 8) + MathHelper.cos(var6) * (float)this.numberOfBlocks / 8.0F);
        double var13 = (double)((float)(par5 + 8) - MathHelper.cos(var6) * (float)this.numberOfBlocks / 8.0F);
        double var15 = (double)(par4 + par2Random.nextInt(3) - 2);
        double var17 = (double)(par4 + par2Random.nextInt(3) - 2);

        for (int var19 = 0; var19 <= this.numberOfBlocks; ++var19)
        {
            double var20 = var7 + (var9 - var7) * (double)var19 / (double)this.numberOfBlocks;
            double var22 = var15 + (var17 - var15) * (double)var19 / (double)this.numberOfBlocks;
            double var24 = var11 + (var13 - var11) * (double)var19 / (double)this.numberOfBlocks;
            double var26 = par2Random.nextDouble() * (double)this.numberOfBlocks / 16.0D;
            double var28 = (double)(MathHelper.sin((float)var19 * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * var26 + 1.0D;
            double var30 = (double)(MathHelper.sin((float)var19 * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * var26 + 1.0D;
            int var32 = MathHelper.floor_double(var20 - var28 / 2.0D);
            int var33 = MathHelper.floor_double(var22 - var30 / 2.0D);
            int var34 = MathHelper.floor_double(var24 - var28 / 2.0D);
            int var35 = MathHelper.floor_double(var20 + var28 / 2.0D);
            int var36 = MathHelper.floor_double(var22 + var30 / 2.0D);
            int var37 = MathHelper.floor_double(var24 + var28 / 2.0D);

            for (int var38 = var32; var38 <= var35; ++var38)
            {
                double var39 = ((double)var38 + 0.5D - var20) / (var28 / 2.0D);

                if (var39 * var39 < 1.0D)
                {
                    for (int var41 = var33; var41 <= var36; ++var41)
                    {
                        double var42 = ((double)var41 + 0.5D - var22) / (var30 / 2.0D);

                        if (var39 * var39 + var42 * var42 < 1.0D)
                        {
                            for (int var44 = var34; var44 <= var37; ++var44)
                            {
                                double var45 = ((double)var44 + 0.5D - var24) / (var28 / 2.0D);

                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && par1World.getBlockId(var38, var41, var44) == Block.stone.blockID)
                                {
                                    par1World.setBlock(var38, var41, var44, this.minableBlockId);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
