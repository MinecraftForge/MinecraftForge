package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenTaiga2 extends WorldGenAbstractTree
{
    private static final String __OBFID = "CL_00000435";

    public WorldGenTaiga2(boolean par1)
    {
        super(par1);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int l = par2Random.nextInt(4) + 6;
        int i1 = 1 + par2Random.nextInt(2);
        int j1 = l - i1;
        int k1 = 2 + par2Random.nextInt(2);
        boolean flag = true;

        if (par4 >= 1 && par4 + l + 1 <= 256)
        {
            int i2;
            int l3;

            for (int l1 = par4; l1 <= par4 + 1 + l && flag; ++l1)
            {
                boolean flag1 = true;

                if (l1 - par4 < i1)
                {
                    l3 = 0;
                }
                else
                {
                    l3 = k1;
                }

                for (i2 = par3 - l3; i2 <= par3 + l3 && flag; ++i2)
                {
                    for (int j2 = par5 - l3; j2 <= par5 + l3 && flag; ++j2)
                    {
                        if (l1 >= 0 && l1 < 256)
                        {
                            Block block = par1World.func_147439_a(i2, l1, j2);

                            if (!block.isAir(par1World, i2, l1, j2) && !block.isLeaves(par1World, i2, l1, j2))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                Block block1 = par1World.func_147439_a(par3, par4 - 1, par5);

                boolean isSoil = block1.canSustainPlant(par1World, par3, par4 - 1, par5, ForgeDirection.UP, (BlockSapling)Blocks.sapling);
                if (isSoil && par4 < 256 - l - 1)
                {
                    block1.onPlantGrow(par1World, par3, par4 - 1, par5, par3, par4, par5);
                    l3 = par2Random.nextInt(2);
                    i2 = 1;
                    byte b0 = 0;
                    int k2;
                    int i4;

                    for (i4 = 0; i4 <= j1; ++i4)
                    {
                        k2 = par4 + l - i4;

                        for (int l2 = par3 - l3; l2 <= par3 + l3; ++l2)
                        {
                            int i3 = l2 - par3;

                            for (int j3 = par5 - l3; j3 <= par5 + l3; ++j3)
                            {
                                int k3 = j3 - par5;

                                if ((Math.abs(i3) != l3 || Math.abs(k3) != l3 || l3 <= 0) && par1World.func_147439_a(l2, k2, j3).canBeReplacedByLeaves(par1World, l2, k2, j3))
                                {
                                    this.func_150516_a(par1World, l2, k2, j3, Blocks.leaves, 1);
                                }
                            }
                        }

                        if (l3 >= i2)
                        {
                            l3 = b0;
                            b0 = 1;
                            ++i2;

                            if (i2 > k1)
                            {
                                i2 = k1;
                            }
                        }
                        else
                        {
                            ++l3;
                        }
                    }

                    i4 = par2Random.nextInt(3);

                    for (k2 = 0; k2 < l - i4; ++k2)
                    {
                        Block block2 = par1World.func_147439_a(par3, par4 + k2, par5);

                        if (block2.isAir(par1World, par3, par4 + k2, par5) || block2.isLeaves(par1World, par3, par4 + k2, par5))
                        {
                            this.func_150516_a(par1World, par3, par4 + k2, par5, Blocks.log, 1);
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
}