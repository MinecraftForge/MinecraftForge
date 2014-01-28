package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator
{
    // JAVADOC FIELD $$ field_76523_a
    private int mushroomType = -1;
    private static final String __OBFID = "CL_00000415";

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
        int l = par2Random.nextInt(2);

        if (this.mushroomType >= 0)
        {
            l = this.mushroomType;
        }

        int i1 = par2Random.nextInt(3) + 4;
        boolean flag = true;

        if (par4 >= 1 && par4 + i1 + 1 < 256)
        {
            int k1;
            int l1;

            for (int j1 = par4; j1 <= par4 + 1 + i1; ++j1)
            {
                byte b0 = 3;

                if (j1 <= par4 + 3)
                {
                    b0 = 0;
                }

                for (k1 = par3 - b0; k1 <= par3 + b0 && flag; ++k1)
                {
                    for (l1 = par5 - b0; l1 <= par5 + b0 && flag; ++l1)
                    {
                        if (j1 >= 0 && j1 < 256)
                        {
                            Block block = par1World.func_147439_a(k1, j1, l1);

                            if (!block.isAir(par1World, k1, j1, l1) && !block.isLeaves(par1World, k1, j1, l1))
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

                if (block1 != Blocks.dirt && block1 != Blocks.grass && block1 != Blocks.mycelium)
                {
                    return false;
                }
                else
                {
                    int k2 = par4 + i1;

                    if (l == 1)
                    {
                        k2 = par4 + i1 - 3;
                    }

                    for (k1 = k2; k1 <= par4 + i1; ++k1)
                    {
                        l1 = 1;

                        if (k1 < par4 + i1)
                        {
                            ++l1;
                        }

                        if (l == 0)
                        {
                            l1 = 3;
                        }

                        for (int l2 = par3 - l1; l2 <= par3 + l1; ++l2)
                        {
                            for (int i2 = par5 - l1; i2 <= par5 + l1; ++i2)
                            {
                                int j2 = 5;

                                if (l2 == par3 - l1)
                                {
                                    --j2;
                                }

                                if (l2 == par3 + l1)
                                {
                                    ++j2;
                                }

                                if (i2 == par5 - l1)
                                {
                                    j2 -= 3;
                                }

                                if (i2 == par5 + l1)
                                {
                                    j2 += 3;
                                }

                                if (l == 0 || k1 < par4 + i1)
                                {
                                    if ((l2 == par3 - l1 || l2 == par3 + l1) && (i2 == par5 - l1 || i2 == par5 + l1))
                                    {
                                        continue;
                                    }

                                    if (l2 == par3 - (l1 - 1) && i2 == par5 - l1)
                                    {
                                        j2 = 1;
                                    }

                                    if (l2 == par3 - l1 && i2 == par5 - (l1 - 1))
                                    {
                                        j2 = 1;
                                    }

                                    if (l2 == par3 + (l1 - 1) && i2 == par5 - l1)
                                    {
                                        j2 = 3;
                                    }

                                    if (l2 == par3 + l1 && i2 == par5 - (l1 - 1))
                                    {
                                        j2 = 3;
                                    }

                                    if (l2 == par3 - (l1 - 1) && i2 == par5 + l1)
                                    {
                                        j2 = 7;
                                    }

                                    if (l2 == par3 - l1 && i2 == par5 + (l1 - 1))
                                    {
                                        j2 = 7;
                                    }

                                    if (l2 == par3 + (l1 - 1) && i2 == par5 + l1)
                                    {
                                        j2 = 9;
                                    }

                                    if (l2 == par3 + l1 && i2 == par5 + (l1 - 1))
                                    {
                                        j2 = 9;
                                    }
                                }

                                if (j2 == 5 && k1 < par4 + i1)
                                {
                                    j2 = 0;
                                }

                                if ((j2 != 0 || par4 >= par4 + i1 - 1) && par1World.func_147439_a(l2, k1, i2).canBeReplacedByLeaves(par1World, l2, k1, i2))
                                {
                                    this.func_150516_a(par1World, l2, k1, i2, Block.func_149729_e(Block.func_149682_b(Blocks.brown_mushroom_block) + l), j2);
                                }
                            }
                        }
                    }

                    for (k1 = 0; k1 < i1; ++k1)
                    {
                        Block block2 = par1World.func_147439_a(par3, par4 + k1, par5);

                        if (block2.canBeReplacedByLeaves(par1World, par3, par4 + k1, par5))
                        {
                            this.func_150516_a(par1World, par3, par4 + k1, par5, Block.func_149729_e(Block.func_149682_b(Blocks.brown_mushroom_block) + l), 10);
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