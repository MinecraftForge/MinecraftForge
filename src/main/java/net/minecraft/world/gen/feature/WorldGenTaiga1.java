package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenTaiga1 extends WorldGenAbstractTree
{
    private static final String __OBFID = "CL_00000427";

    public WorldGenTaiga1()
    {
        super(false);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int l = par2Random.nextInt(5) + 7;
        int i1 = l - par2Random.nextInt(2) - 3;
        int j1 = l - i1;
        int k1 = 1 + par2Random.nextInt(j1 + 1);
        boolean flag = true;

        if (par4 >= 1 && par4 + l + 1 <= 256)
        {
            int i2;
            int j2;
            int i3;

            for (int l1 = par4; l1 <= par4 + 1 + l && flag; ++l1)
            {
                boolean flag1 = true;

                if (l1 - par4 < i1)
                {
                    i3 = 0;
                }
                else
                {
                    i3 = k1;
                }

                for (i2 = par3 - i3; i2 <= par3 + i3 && flag; ++i2)
                {
                    for (j2 = par5 - i3; j2 <= par5 + i3 && flag; ++j2)
                    {
                        if (l1 >= 0 && l1 < 256)
                        {
                            Block block = par1World.func_147439_a(i2, l1, j2);

                            if (!this.isReplaceable(par1World, i2, l1, j2))
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
                    i3 = 0;

                    for (i2 = par4 + l; i2 >= par4 + i1; --i2)
                    {
                        for (j2 = par3 - i3; j2 <= par3 + i3; ++j2)
                        {
                            int j3 = j2 - par3;

                            for (int k2 = par5 - i3; k2 <= par5 + i3; ++k2)
                            {
                                int l2 = k2 - par5;

                                if ((Math.abs(j3) != i3 || Math.abs(l2) != i3 || i3 <= 0) && par1World.func_147439_a(j2, i2, k2).canBeReplacedByLeaves(par1World, j2, i2, k2))
                                {
                                    this.func_150516_a(par1World, j2, i2, k2, Blocks.leaves, 1);
                                }
                            }
                        }

                        if (i3 >= 1 && i2 == par4 + i1 + 1)
                        {
                            --i3;
                        }
                        else if (i3 < k1)
                        {
                            ++i3;
                        }
                    }

                    for (i2 = 0; i2 < l - 1; ++i2)
                    {
                        Block block2 = par1World.func_147439_a(par3, par4 + i2, par5);

                        if (block2.isAir(par1World, par3, par4 + i2, par5) || block2.isLeaves(par1World, par3, par4 + i2, par5))
                        {
                            this.func_150516_a(par1World, par3, par4 + i2, par5, Blocks.log, 1);
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