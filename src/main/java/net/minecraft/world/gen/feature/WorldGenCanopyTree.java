package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenCanopyTree extends WorldGenAbstractTree
{
    private static final String __OBFID = "CL_00000430";

    public WorldGenCanopyTree(boolean p_i45461_1_)
    {
        super(p_i45461_1_);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int l = par2Random.nextInt(3) + par2Random.nextInt(2) + 6;
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
                    b0 = 2;
                }

                for (j1 = par3 - b0; j1 <= par3 + b0 && flag; ++j1)
                {
                    for (k1 = par5 - b0; k1 <= par5 + b0 && flag; ++k1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            Block block = par1World.func_147439_a(j1, i1, k1);

                            if (!this.isReplaceable(par1World, j1, i1, k1))
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
                Block block2 = par1World.func_147439_a(par3, par4 - 1, par5);

                boolean isSoil = block2.canSustainPlant(par1World, par3, par4 - 1, par5, ForgeDirection.UP, (BlockSapling)Blocks.sapling);
                if (isSoil && par4 < 256 - l - 1)
                {
                    onPlantGrow(par1World, par3,     par4 - 1, par5,     par3, par4, par5);
                    onPlantGrow(par1World, par3 + 1, par4 - 1, par5,     par3, par4, par5);
                    onPlantGrow(par1World, par3 + 1, par4 - 1, par5 + 1, par3, par4, par5);
                    onPlantGrow(par1World, par3,     par4 - 1, par5 + 1, par3, par4, par5);
                    int j3 = par2Random.nextInt(4);
                    j1 = l - par2Random.nextInt(4);
                    k1 = 2 - par2Random.nextInt(3);
                    int k3 = par3;
                    int l1 = par5;
                    int i2 = 0;
                    int j2;
                    int k2;

                    for (j2 = 0; j2 < l; ++j2)
                    {
                        k2 = par4 + j2;

                        if (j2 >= j1 && k1 > 0)
                        {
                            k3 += Direction.offsetX[j3];
                            l1 += Direction.offsetZ[j3];
                            --k1;
                        }

                        Block block1 = par1World.func_147439_a(k3, k2, l1);

                        if (block1.isAir(par1World, k3, k2, l1) || block1.isLeaves(par1World, k3, k2, l1))
                        {
                            this.func_150516_a(par1World, k3, k2, l1, Blocks.log2, 1);
                            this.func_150516_a(par1World, k3 + 1, k2, l1, Blocks.log2, 1);
                            this.func_150516_a(par1World, k3, k2, l1 + 1, Blocks.log2, 1);
                            this.func_150516_a(par1World, k3 + 1, k2, l1 + 1, Blocks.log2, 1);
                            i2 = k2;
                        }
                    }

                    for (j2 = -2; j2 <= 0; ++j2)
                    {
                        for (k2 = -2; k2 <= 0; ++k2)
                        {
                            byte b2 = -1;
                            this.func_150526_a(par1World, k3 + j2, i2 + b2, l1 + k2);
                            this.func_150526_a(par1World, 1 + k3 - j2, i2 + b2, l1 + k2);
                            this.func_150526_a(par1World, k3 + j2, i2 + b2, 1 + l1 - k2);
                            this.func_150526_a(par1World, 1 + k3 - j2, i2 + b2, 1 + l1 - k2);

                            if ((j2 > -2 || k2 > -1) && (j2 != -1 || k2 != -2))
                            {
                                byte b1 = 1;
                                this.func_150526_a(par1World, k3 + j2, i2 + b1, l1 + k2);
                                this.func_150526_a(par1World, 1 + k3 - j2, i2 + b1, l1 + k2);
                                this.func_150526_a(par1World, k3 + j2, i2 + b1, 1 + l1 - k2);
                                this.func_150526_a(par1World, 1 + k3 - j2, i2 + b1, 1 + l1 - k2);
                            }
                        }
                    }

                    if (par2Random.nextBoolean())
                    {
                        this.func_150526_a(par1World, k3, i2 + 2, l1);
                        this.func_150526_a(par1World, k3 + 1, i2 + 2, l1);
                        this.func_150526_a(par1World, k3 + 1, i2 + 2, l1 + 1);
                        this.func_150526_a(par1World, k3, i2 + 2, l1 + 1);
                    }

                    for (j2 = -3; j2 <= 4; ++j2)
                    {
                        for (k2 = -3; k2 <= 4; ++k2)
                        {
                            if ((j2 != -3 || k2 != -3) && (j2 != -3 || k2 != 4) && (j2 != 4 || k2 != -3) && (j2 != 4 || k2 != 4) && (Math.abs(j2) < 3 || Math.abs(k2) < 3))
                            {
                                this.func_150526_a(par1World, k3 + j2, i2, l1 + k2);
                            }
                        }
                    }

                    for (j2 = -1; j2 <= 2; ++j2)
                    {
                        for (k2 = -1; k2 <= 2; ++k2)
                        {
                            if ((j2 < 0 || j2 > 1 || k2 < 0 || k2 > 1) && par2Random.nextInt(3) <= 0)
                            {
                                int l3 = par2Random.nextInt(3) + 2;
                                int l2;

                                for (l2 = 0; l2 < l3; ++l2)
                                {
                                    this.func_150516_a(par1World, par3 + j2, i2 - l2 - 1, par5 + k2, Blocks.log2, 1);
                                }

                                int i3;

                                for (l2 = -1; l2 <= 1; ++l2)
                                {
                                    for (i3 = -1; i3 <= 1; ++i3)
                                    {
                                        this.func_150526_a(par1World, k3 + j2 + l2, i2 - 0, l1 + k2 + i3);
                                    }
                                }

                                for (l2 = -2; l2 <= 2; ++l2)
                                {
                                    for (i3 = -2; i3 <= 2; ++i3)
                                    {
                                        if (Math.abs(l2) != 2 || Math.abs(i3) != 2)
                                        {
                                            this.func_150526_a(par1World, k3 + j2 + l2, i2 - 1, l1 + k2 + i3);
                                        }
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

    private void func_150526_a(World p_150526_1_, int p_150526_2_, int p_150526_3_, int p_150526_4_)
    {
        Block block = p_150526_1_.func_147439_a(p_150526_2_, p_150526_3_, p_150526_4_);

        if (block.isAir(p_150526_1_, p_150526_2_, p_150526_3_, p_150526_4_))
        {
            this.func_150516_a(p_150526_1_, p_150526_2_, p_150526_3_, p_150526_4_, Blocks.leaves2, 1);
        }
    }

    //Just a helper macro
    private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
    {
        world.func_147439_a(x, y, z).onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
    }
}