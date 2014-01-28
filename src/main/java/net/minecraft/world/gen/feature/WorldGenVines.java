package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator
{
    private static final String __OBFID = "CL_00000439";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int l = par3;

        for (int i1 = par5; par4 < 128; ++par4)
        {
            if (par1World.func_147437_c(par3, par4, par5))
            {
                for (int j1 = 2; j1 <= 5; ++j1)
                {
                    if (Blocks.vine.func_149707_d(par1World, par3, par4, par5, j1))
                    {
                        par1World.func_147465_d(par3, par4, par5, Blocks.vine, 1 << Direction.facingToDirection[Facing.oppositeSide[j1]], 2);
                        break;
                    }
                }
            }
            else
            {
                par3 = l + par2Random.nextInt(4) - par2Random.nextInt(4);
                par5 = i1 + par2Random.nextInt(4) - par2Random.nextInt(4);
            }
        }

        return true;
    }
}