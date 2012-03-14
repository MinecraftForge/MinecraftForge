package net.minecraft.src;

import java.util.Random;

public class WorldGenBigMushroom extends WorldGenerator
{
    /** The mushroom type. 0 for brown, 1 for red. */
    private int mushroomType = -1;

    public WorldGenBigMushroom(int par1)
    {
        super(true);
        this.mushroomType = par1;
    }

    public WorldGenBigMushroom()
    {
        super(false);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int var6 = par2Random.nextInt(2);

        if (this.mushroomType >= 0)
        {
            var6 = this.mushroomType;
        }

        int var7 = par2Random.nextInt(3) + 4;
        boolean var8 = true;

        if (par4 >= 1 && par4 + var7 + 1 < 256)
        {
            int var9;
            int var11;
            int var12;
            int var13;

            for (var9 = par4; var9 <= par4 + 1 + var7; ++var9)
            {
                byte var10 = 3;

                if (var9 == par4)
                {
                    var10 = 0;
                }

                for (var11 = par3 - var10; var11 <= par3 + var10 && var8; ++var11)
                {
                    for (var12 = par5 - var10; var12 <= par5 + var10 && var8; ++var12)
                    {
                        if (var9 >= 0 && var9 < 256)
                        {
                            var13 = par1World.getBlockId(var11, var9, var12);

                            if (var13 != 0 && var13 != Block.leaves.blockID)
                            {
                                var8 = false;
                            }
                        }
                        else
                        {
                            var8 = false;
                        }
                    }
                }
            }

            if (!var8)
            {
                return false;
            }
            else
            {
                var9 = par1World.getBlockId(par3, par4 - 1, par5);

                if (var9 != Block.dirt.blockID && var9 != Block.grass.blockID && var9 != Block.mycelium.blockID)
                {
                    return false;
                }
                else if (!Block.mushroomBrown.canPlaceBlockAt(par1World, par3, par4, par5))
                {
                    return false;
                }
                else
                {
                    this.setBlockAndMetadata(par1World, par3, par4 - 1, par5, Block.dirt.blockID, 0);
                    int var16 = par4 + var7;

                    if (var6 == 1)
                    {
                        var16 = par4 + var7 - 3;
                    }

                    for (var11 = var16; var11 <= par4 + var7; ++var11)
                    {
                        var12 = 1;

                        if (var11 < par4 + var7)
                        {
                            ++var12;
                        }

                        if (var6 == 0)
                        {
                            var12 = 3;
                        }

                        for (var13 = par3 - var12; var13 <= par3 + var12; ++var13)
                        {
                            for (int var14 = par5 - var12; var14 <= par5 + var12; ++var14)
                            {
                                int var15 = 5;

                                if (var13 == par3 - var12)
                                {
                                    --var15;
                                }

                                if (var13 == par3 + var12)
                                {
                                    ++var15;
                                }

                                if (var14 == par5 - var12)
                                {
                                    var15 -= 3;
                                }

                                if (var14 == par5 + var12)
                                {
                                    var15 += 3;
                                }

                                if (var6 == 0 || var11 < par4 + var7)
                                {
                                    if ((var13 == par3 - var12 || var13 == par3 + var12) && (var14 == par5 - var12 || var14 == par5 + var12))
                                    {
                                        continue;
                                    }

                                    if (var13 == par3 - (var12 - 1) && var14 == par5 - var12)
                                    {
                                        var15 = 1;
                                    }

                                    if (var13 == par3 - var12 && var14 == par5 - (var12 - 1))
                                    {
                                        var15 = 1;
                                    }

                                    if (var13 == par3 + (var12 - 1) && var14 == par5 - var12)
                                    {
                                        var15 = 3;
                                    }

                                    if (var13 == par3 + var12 && var14 == par5 - (var12 - 1))
                                    {
                                        var15 = 3;
                                    }

                                    if (var13 == par3 - (var12 - 1) && var14 == par5 + var12)
                                    {
                                        var15 = 7;
                                    }

                                    if (var13 == par3 - var12 && var14 == par5 + (var12 - 1))
                                    {
                                        var15 = 7;
                                    }

                                    if (var13 == par3 + (var12 - 1) && var14 == par5 + var12)
                                    {
                                        var15 = 9;
                                    }

                                    if (var13 == par3 + var12 && var14 == par5 + (var12 - 1))
                                    {
                                        var15 = 9;
                                    }
                                }

                                if (var15 == 5 && var11 < par4 + var7)
                                {
                                    var15 = 0;
                                }

                                if ((var15 != 0 || par4 >= par4 + var7 - 1) && !Block.opaqueCubeLookup[par1World.getBlockId(var13, var11, var14)])
                                {
                                    this.setBlockAndMetadata(par1World, var13, var11, var14, Block.mushroomCapBrown.blockID + var6, var15);
                                }
                            }
                        }
                    }

                    for (var11 = 0; var11 < var7; ++var11)
                    {
                        var12 = par1World.getBlockId(par3, par4 + var11, par5);

                        if (!Block.opaqueCubeLookup[var12])
                        {
                            this.setBlockAndMetadata(par1World, par3, par4 + var11, par5, Block.mushroomCapBrown.blockID + var6, 10);
                        }
                    }

                    return true;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
