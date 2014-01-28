package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees
{
    private static final String __OBFID = "CL_00000420";

    public WorldGenMegaJungle(boolean p_i45456_1_, int p_i45456_2_, int p_i45456_3_, int p_i45456_4_, int p_i45456_5_)
    {
        super(p_i45456_1_, p_i45456_2_, p_i45456_3_, p_i45456_4_, p_i45456_5_);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int l = this.func_150533_a(par2Random);

        if (!this.func_150537_a(par1World, par2Random, par3, par4, par5, l))
        {
            return false;
        }
        else
        {
            this.func_150543_c(par1World, par3, par5, par4 + l, 2, par2Random);

            for (int i1 = par4 + l - 2 - par2Random.nextInt(4); i1 > par4 + l / 2; i1 -= 2 + par2Random.nextInt(4))
            {
                float f = par2Random.nextFloat() * (float)Math.PI * 2.0F;
                int j1 = par3 + (int)(0.5F + MathHelper.cos(f) * 4.0F);
                int k1 = par5 + (int)(0.5F + MathHelper.sin(f) * 4.0F);
                int l1;

                for (l1 = 0; l1 < 5; ++l1)
                {
                    j1 = par3 + (int)(1.5F + MathHelper.cos(f) * (float)l1);
                    k1 = par5 + (int)(1.5F + MathHelper.sin(f) * (float)l1);
                    this.func_150516_a(par1World, j1, i1 - 3 + l1 / 2, k1, Blocks.log, this.woodMetadata);
                }

                l1 = 1 + par2Random.nextInt(2);
                int i2 = i1;

                for (int j2 = i1 - l1; j2 <= i2; ++j2)
                {
                    int k2 = j2 - i2;
                    this.func_150534_b(par1World, j1, j2, k1, 1 - k2, par2Random);
                }
            }

            for (int l2 = 0; l2 < l; ++l2)
            {
                Block block = par1World.func_147439_a(par3, par4 + l2, par5);

                if (block.isAir(par1World, par3, par4 + l2, par5) || block.isLeaves(par1World, par3, par4 + l2, par5))
                {
                    this.func_150516_a(par1World, par3, par4 + l2, par5, Blocks.log, this.woodMetadata);

                    if (l2 > 0)
                    {
                        if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3 - 1, par4 + l2, par5))
                        {
                            this.func_150516_a(par1World, par3 - 1, par4 + l2, par5, Blocks.vine, 8);
                        }

                        if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3, par4 + l2, par5 - 1))
                        {
                            this.func_150516_a(par1World, par3, par4 + l2, par5 - 1, Blocks.vine, 1);
                        }
                    }
                }

                if (l2 < l - 1)
                {
                    block = par1World.func_147439_a(par3 + 1, par4 + l2, par5);

                    if (block.isAir(par1World, par3 + 1, par4 + l2, par5) || block.isLeaves(par1World, par3 + 1, par4 + l2, par5))
                    {
                        this.func_150516_a(par1World, par3 + 1, par4 + l2, par5, Blocks.log, this.woodMetadata);

                        if (l2 > 0)
                        {
                            if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3 + 2, par4 + l2, par5))
                            {
                                this.func_150516_a(par1World, par3 + 2, par4 + l2, par5, Blocks.vine, 2);
                            }

                            if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3 + 1, par4 + l2, par5 - 1))
                            {
                                this.func_150516_a(par1World, par3 + 1, par4 + l2, par5 - 1, Blocks.vine, 1);
                            }
                        }
                    }

                    block = par1World.func_147439_a(par3 + 1, par4 + l2, par5 + 1);

                    if (block.isAir(par1World, par3 + 1, par4 + l2, par5 + 1) || block.isLeaves(par1World, par3 + 1, par4 + l2, par5 + 1))
                    {
                        this.func_150516_a(par1World, par3 + 1, par4 + l2, par5 + 1, Blocks.log, this.woodMetadata);

                        if (l2 > 0)
                        {
                            if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3 + 2, par4 + l2, par5 + 1))
                            {
                                this.func_150516_a(par1World, par3 + 2, par4 + l2, par5 + 1, Blocks.vine, 2);
                            }

                            if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3 + 1, par4 + l2, par5 + 2))
                            {
                                this.func_150516_a(par1World, par3 + 1, par4 + l2, par5 + 2, Blocks.vine, 4);
                            }
                        }
                    }

                    block = par1World.func_147439_a(par3, par4 + l2, par5 + 1);

                    if (block.isAir(par1World, par3, par4 + l2, par5 + 1) || block.isLeaves(par1World, par3, par4 + l2, par5 + 1))
                    {
                        this.func_150516_a(par1World, par3, par4 + l2, par5 + 1, Blocks.log, this.woodMetadata);

                        if (l2 > 0)
                        {
                            if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3 - 1, par4 + l2, par5 + 1))
                            {
                                this.func_150516_a(par1World, par3 - 1, par4 + l2, par5 + 1, Blocks.vine, 8);
                            }

                            if (par2Random.nextInt(3) > 0 && par1World.func_147437_c(par3, par4 + l2, par5 + 2))
                            {
                                this.func_150516_a(par1World, par3, par4 + l2, par5 + 2, Blocks.vine, 4);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    private void func_150543_c(World p_150543_1_, int p_150543_2_, int p_150543_3_, int p_150543_4_, int p_150543_5_, Random p_150543_6_)
    {
        byte b0 = 2;

        for (int i1 = p_150543_4_ - b0; i1 <= p_150543_4_; ++i1)
        {
            int j1 = i1 - p_150543_4_;
            this.func_150535_a(p_150543_1_, p_150543_2_, i1, p_150543_3_, p_150543_5_ + 1 - j1, p_150543_6_);
        }
    }
}