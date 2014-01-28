package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator
{
    private static final String __OBFID = "CL_00000428";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        for (int l = 0; l < 64; ++l)
        {
            int i1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
            int j1 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
            int k1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

            if (par1World.func_147437_c(i1, j1, k1) && par1World.func_147439_a(i1, j1 - 1, k1) == Blocks.grass && Blocks.pumpkin.func_149742_c(par1World, i1, j1, k1))
            {
                par1World.func_147465_d(i1, j1, k1, Blocks.pumpkin, par2Random.nextInt(4), 2);
            }
        }

        return true;
    }
}