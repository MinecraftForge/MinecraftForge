package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenGlowStone2 extends WorldGenerator
{
    private static final String __OBFID = "CL_00000413";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        if (!par1World.func_147437_c(par3, par4, par5))
        {
            return false;
        }
        else if (par1World.func_147439_a(par3, par4 + 1, par5) != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            par1World.func_147465_d(par3, par4, par5, Blocks.glowstone, 0, 2);

            for (int l = 0; l < 1500; ++l)
            {
                int i1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
                int j1 = par4 - par2Random.nextInt(12);
                int k1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

                if (par1World.func_147439_a(i1, j1, k1).func_149688_o() == Material.field_151579_a)
                {
                    int l1 = 0;

                    for (int i2 = 0; i2 < 6; ++i2)
                    {
                        Block block = null;

                        if (i2 == 0)
                        {
                            block = par1World.func_147439_a(i1 - 1, j1, k1);
                        }

                        if (i2 == 1)
                        {
                            block = par1World.func_147439_a(i1 + 1, j1, k1);
                        }

                        if (i2 == 2)
                        {
                            block = par1World.func_147439_a(i1, j1 - 1, k1);
                        }

                        if (i2 == 3)
                        {
                            block = par1World.func_147439_a(i1, j1 + 1, k1);
                        }

                        if (i2 == 4)
                        {
                            block = par1World.func_147439_a(i1, j1, k1 - 1);
                        }

                        if (i2 == 5)
                        {
                            block = par1World.func_147439_a(i1, j1, k1 + 1);
                        }

                        if (block == Blocks.glowstone)
                        {
                            ++l1;
                        }
                    }

                    if (l1 == 1)
                    {
                        par1World.func_147465_d(i1, j1, k1, Blocks.glowstone, 0, 2);
                    }
                }
            }

            return true;
        }
    }
}