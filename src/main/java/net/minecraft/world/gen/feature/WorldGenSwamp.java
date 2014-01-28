package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenSwamp extends WorldGenAbstractTree
{
    private static final String __OBFID = "CL_00000436";

    public WorldGenSwamp()
    {
        super(false);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int l;

        for (l = par2Random.nextInt(4) + 5; par1World.func_147439_a(par3, par4 - 1, par5).func_149688_o() == Material.field_151586_h; --par4)
        {
            ;
        }

        boolean flag = true;

        if (par4 >= 1 && par4 + l + 1 <= 256)
        {
            int j1;
            int k1;

            for (int i1 = par4; i1 <= par4 + 1 + l; ++i1)
            {
                byte b0 = 1;

                if (i1 == par4)
                {
                    b0 = 0;
                }

                if (i1 >= par4 + 1 + l - 2)
                {
                    b0 = 3;
                }

                for (j1 = par3 - b0; j1 <= par3 + b0 && flag; ++j1)
                {
                    for (k1 = par5 - b0; k1 <= par5 + b0 && flag; ++k1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            Block block = par1World.func_147439_a(j1, i1, k1);

                            if (!(block.isAir(par1World, j1, i1, k1) || block.isLeaves(par1World, j1, i1, k1)))
                            {
                                if (block != Blocks.water && block != Blocks.flowing_water)
                                {
                                    flag = false;
                                }
                                else if (i1 > par4)
                                {
                                    flag = false;
                                }
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
                    int l1;
                    int l2;
                    int k2;

                    for (k2 = par4 - 3 + l; k2 <= par4 + l; ++k2)
                    {
                        j1 = k2 - (par4 + l);
                        k1 = 2 - j1 / 2;

                        for (l2 = par3 - k1; l2 <= par3 + k1; ++l2)
                        {
                            l1 = l2 - par3;

                            for (int i2 = par5 - k1; i2 <= par5 + k1; ++i2)
                            {
                                int j2 = i2 - par5;

                                if ((Math.abs(l1) != k1 || Math.abs(j2) != k1 || par2Random.nextInt(2) != 0 && j1 != 0) && par1World.func_147439_a(l2, k2, i2).canBeReplacedByLeaves(par1World, l2, k2, i2))
                                {
                                    this.func_150515_a(par1World, l2, k2, i2, Blocks.leaves);
                                }
                            }
                        }
                    }

                    for (k2 = 0; k2 < l; ++k2)
                    {
                        Block block2 = par1World.func_147439_a(par3, par4 + k2, par5);

                        if (block2.isAir(par1World, par3, par4 + k2, par5) || block2.isLeaves(par1World, par3, par4 + k2, par5) || block2 == Blocks.flowing_water || block2 == Blocks.water)
                        {
                            this.func_150515_a(par1World, par3, par4 + k2, par5, Blocks.log);
                        }
                    }

                    for (k2 = par4 - 3 + l; k2 <= par4 + l; ++k2)
                    {
                        j1 = k2 - (par4 + l);
                        k1 = 2 - j1 / 2;

                        for (l2 = par3 - k1; l2 <= par3 + k1; ++l2)
                        {
                            for (l1 = par5 - k1; l1 <= par5 + k1; ++l1)
                            {
                                if (par1World.func_147439_a(l2, k2, l1).isLeaves(par1World, l2, k2, l1))
                                {
                                    if (par2Random.nextInt(4) == 0 && par1World.func_147439_a(l2 - 1, k2, l1).isAir(par1World, l2 - 1, k2, l1))
                                    {
                                        this.generateVines(par1World, l2 - 1, k2, l1, 8);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.func_147439_a(l2 + 1, k2, l1).isAir(par1World, l2 + 1, k2, l1))
                                    {
                                        this.generateVines(par1World, l2 + 1, k2, l1, 2);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.func_147439_a(l2, k2, l1 - 1).isAir(par1World, l2, k2, l1 - 1))
                                    {
                                        this.generateVines(par1World, l2, k2, l1 - 1, 1);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.func_147439_a(l2, k2, l1 + 1).isAir(par1World, l2, k2, l1 + 1))
                                    {
                                        this.generateVines(par1World, l2, k2, l1 + 1, 4);
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

    // JAVADOC METHOD $$ func_76536_b
    private void generateVines(World par1World, int par2, int par3, int par4, int par5)
    {
        this.func_150516_a(par1World, par2, par3, par4, Blocks.vine, par5);
        int i1 = 4;

        while (true)
        {
            --par3;

            if (!(par1World.func_147439_a(par2, par3, par4).isAir(par1World, par2, par3, par4)) || i1 <= 0)
            {
                return;
            }

            this.func_150516_a(par1World, par2, par3, par4, Blocks.vine, par5);
            --i1;
        }
    }
}