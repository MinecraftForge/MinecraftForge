package net.minecraft.src;

import java.util.Random;

public class WorldGenSwamp extends WorldGenerator
{
    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int var6;

        for (var6 = par2Random.nextInt(4) + 5; par1World.getBlockMaterial(par3, par4 - 1, par5) == Material.water; --par4)
        {
            ;
        }

        boolean var7 = true;

        if (par4 >= 1 && par4 + var6 + 1 <= 128)
        {
            int var8;
            int var10;
            int var11;
            int var12;

            for (var8 = par4; var8 <= par4 + 1 + var6; ++var8)
            {
                byte var9 = 1;

                if (var8 == par4)
                {
                    var9 = 0;
                }

                if (var8 >= par4 + 1 + var6 - 2)
                {
                    var9 = 3;
                }

                for (var10 = par3 - var9; var10 <= par3 + var9 && var7; ++var10)
                {
                    for (var11 = par5 - var9; var11 <= par5 + var9 && var7; ++var11)
                    {
                        if (var8 >= 0 && var8 < 128)
                        {
                            var12 = par1World.getBlockId(var10, var8, var11);

                            if (var12 != 0 && var12 != Block.leaves.blockID)
                            {
                                if (var12 != Block.waterStill.blockID && var12 != Block.waterMoving.blockID)
                                {
                                    var7 = false;
                                }
                                else if (var8 > par4)
                                {
                                    var7 = false;
                                }
                            }
                        }
                        else
                        {
                            var7 = false;
                        }
                    }
                }
            }

            if (!var7)
            {
                return false;
            }
            else
            {
                var8 = par1World.getBlockId(par3, par4 - 1, par5);

                if ((var8 == Block.grass.blockID || var8 == Block.dirt.blockID) && par4 < 128 - var6 - 1)
                {
                    par1World.setBlock(par3, par4 - 1, par5, Block.dirt.blockID);
                    int var13;
                    int var16;

                    for (var16 = par4 - 3 + var6; var16 <= par4 + var6; ++var16)
                    {
                        var10 = var16 - (par4 + var6);
                        var11 = 2 - var10 / 2;

                        for (var12 = par3 - var11; var12 <= par3 + var11; ++var12)
                        {
                            var13 = var12 - par3;

                            for (int var14 = par5 - var11; var14 <= par5 + var11; ++var14)
                            {
                                int var15 = var14 - par5;

                                if ((Math.abs(var13) != var11 || Math.abs(var15) != var11 || par2Random.nextInt(2) != 0 && var10 != 0) && !Block.opaqueCubeLookup[par1World.getBlockId(var12, var16, var14)])
                                {
                                    par1World.setBlock(var12, var16, var14, Block.leaves.blockID);
                                }
                            }
                        }
                    }

                    for (var16 = 0; var16 < var6; ++var16)
                    {
                        var10 = par1World.getBlockId(par3, par4 + var16, par5);

                        if (var10 == 0 || var10 == Block.leaves.blockID || var10 == Block.waterMoving.blockID || var10 == Block.waterStill.blockID)
                        {
                            par1World.setBlock(par3, par4 + var16, par5, Block.wood.blockID);
                        }
                    }

                    for (var16 = par4 - 3 + var6; var16 <= par4 + var6; ++var16)
                    {
                        var10 = var16 - (par4 + var6);
                        var11 = 2 - var10 / 2;

                        for (var12 = par3 - var11; var12 <= par3 + var11; ++var12)
                        {
                            for (var13 = par5 - var11; var13 <= par5 + var11; ++var13)
                            {
                                if (par1World.getBlockId(var12, var16, var13) == Block.leaves.blockID)
                                {
                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(var12 - 1, var16, var13) == 0)
                                    {
                                        this.generateVines(par1World, var12 - 1, var16, var13, 8);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(var12 + 1, var16, var13) == 0)
                                    {
                                        this.generateVines(par1World, var12 + 1, var16, var13, 2);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(var12, var16, var13 - 1) == 0)
                                    {
                                        this.generateVines(par1World, var12, var16, var13 - 1, 1);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(var12, var16, var13 + 1) == 0)
                                    {
                                        this.generateVines(par1World, var12, var16, var13 + 1, 4);
                                    }
                                }
                            }
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Generates vines at the given position until it hits a block.
     */
    private void generateVines(World par1World, int par2, int par3, int par4, int par5)
    {
        par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.vine.blockID, par5);
        int var6 = 4;

        while (true)
        {
            --par3;

            if (par1World.getBlockId(par2, par3, par4) != 0 || var6 <= 0)
            {
                return;
            }

            par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.vine.blockID, par5);
            --var6;
        }
    }
}
