package net.minecraft.src;

import java.util.Random;

public class WorldGenShrub extends WorldGenerator
{
    private int field_48408_a;
    private int field_48407_b;

    public WorldGenShrub(int par1, int par2)
    {
        this.field_48407_b = par1;
        this.field_48408_a = par2;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int var15;

        for (boolean var6 = false; ((var15 = par1World.getBlockId(par3, par4, par5)) == 0 || var15 == Block.leaves.blockID) && par4 > 0; --par4)
        {
            ;
        }

        int var7 = par1World.getBlockId(par3, par4, par5);

        if (var7 == Block.dirt.blockID || var7 == Block.grass.blockID)
        {
            ++par4;
            par1World.setBlockAndMetadata(par3, par4, par5, Block.wood.blockID, this.field_48407_b);

            for (int var8 = par4; var8 <= par4 + 2; ++var8)
            {
                int var9 = var8 - par4;
                int var10 = 2 - var9;

                for (int var11 = par3 - var10; var11 <= par3 + var10; ++var11)
                {
                    int var12 = var11 - par3;

                    for (int var13 = par5 - var10; var13 <= par5 + var10; ++var13)
                    {
                        int var14 = var13 - par5;

                        if ((Math.abs(var12) != var10 || Math.abs(var14) != var10 || par2Random.nextInt(2) != 0) && !Block.opaqueCubeLookup[par1World.getBlockId(var11, var8, var13)])
                        {
                            this.setBlockAndMetadata(par1World, var11, var8, var13, Block.leaves.blockID, this.field_48408_a);
                        }
                    }
                }
            }
        }

        return true;
    }
}
