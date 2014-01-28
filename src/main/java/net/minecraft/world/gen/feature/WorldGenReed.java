package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator
{
    private static final String __OBFID = "CL_00000429";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        for (int l = 0; l < 20; ++l)
        {
            int i1 = par3 + par2Random.nextInt(4) - par2Random.nextInt(4);
            int j1 = par4;
            int k1 = par5 + par2Random.nextInt(4) - par2Random.nextInt(4);

            if (par1World.func_147437_c(i1, par4, k1) && (par1World.func_147439_a(i1 - 1, par4 - 1, k1).func_149688_o() == Material.field_151586_h || par1World.func_147439_a(i1 + 1, par4 - 1, k1).func_149688_o() == Material.field_151586_h || par1World.func_147439_a(i1, par4 - 1, k1 - 1).func_149688_o() == Material.field_151586_h || par1World.func_147439_a(i1, par4 - 1, k1 + 1).func_149688_o() == Material.field_151586_h))
            {
                int l1 = 2 + par2Random.nextInt(par2Random.nextInt(3) + 1);

                for (int i2 = 0; i2 < l1; ++i2)
                {
                    if (Blocks.reeds.func_149718_j(par1World, i1, j1 + i2, k1))
                    {
                        par1World.func_147465_d(i1, j1 + i2, k1, Blocks.reeds, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}