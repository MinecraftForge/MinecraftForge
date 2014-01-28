package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator
{
    private static final String __OBFID = "CL_00000407";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        while (par1World.func_147437_c(par3, par4, par5) && par4 > 2)
        {
            --par4;
        }

        if (par1World.func_147439_a(par3, par4, par5) != Blocks.sand)
        {
            return false;
        }
        else
        {
            int l;
            int i1;

            for (l = -2; l <= 2; ++l)
            {
                for (i1 = -2; i1 <= 2; ++i1)
                {
                    if (par1World.func_147437_c(par3 + l, par4 - 1, par5 + i1) && par1World.func_147437_c(par3 + l, par4 - 2, par5 + i1))
                    {
                        return false;
                    }
                }
            }

            for (l = -1; l <= 0; ++l)
            {
                for (i1 = -2; i1 <= 2; ++i1)
                {
                    for (int j1 = -2; j1 <= 2; ++j1)
                    {
                        par1World.func_147465_d(par3 + i1, par4 + l, par5 + j1, Blocks.sandstone, 0, 2);
                    }
                }
            }

            par1World.func_147465_d(par3, par4, par5, Blocks.flowing_water, 0, 2);
            par1World.func_147465_d(par3 - 1, par4, par5, Blocks.flowing_water, 0, 2);
            par1World.func_147465_d(par3 + 1, par4, par5, Blocks.flowing_water, 0, 2);
            par1World.func_147465_d(par3, par4, par5 - 1, Blocks.flowing_water, 0, 2);
            par1World.func_147465_d(par3, par4, par5 + 1, Blocks.flowing_water, 0, 2);

            for (l = -2; l <= 2; ++l)
            {
                for (i1 = -2; i1 <= 2; ++i1)
                {
                    if (l == -2 || l == 2 || i1 == -2 || i1 == 2)
                    {
                        par1World.func_147465_d(par3 + l, par4 + 1, par5 + i1, Blocks.sandstone, 0, 2);
                    }
                }
            }

            par1World.func_147465_d(par3 + 2, par4 + 1, par5, Blocks.stone_slab, 1, 2);
            par1World.func_147465_d(par3 - 2, par4 + 1, par5, Blocks.stone_slab, 1, 2);
            par1World.func_147465_d(par3, par4 + 1, par5 + 2, Blocks.stone_slab, 1, 2);
            par1World.func_147465_d(par3, par4 + 1, par5 - 2, Blocks.stone_slab, 1, 2);

            for (l = -1; l <= 1; ++l)
            {
                for (i1 = -1; i1 <= 1; ++i1)
                {
                    if (l == 0 && i1 == 0)
                    {
                        par1World.func_147465_d(par3 + l, par4 + 4, par5 + i1, Blocks.sandstone, 0, 2);
                    }
                    else
                    {
                        par1World.func_147465_d(par3 + l, par4 + 4, par5 + i1, Blocks.stone_slab, 1, 2);
                    }
                }
            }

            for (l = 1; l <= 3; ++l)
            {
                par1World.func_147465_d(par3 - 1, par4 + l, par5 - 1, Blocks.sandstone, 0, 2);
                par1World.func_147465_d(par3 - 1, par4 + l, par5 + 1, Blocks.sandstone, 0, 2);
                par1World.func_147465_d(par3 + 1, par4 + l, par5 - 1, Blocks.sandstone, 0, 2);
                par1World.func_147465_d(par3 + 1, par4 + l, par5 + 1, Blocks.sandstone, 0, 2);
            }

            return true;
        }
    }
}