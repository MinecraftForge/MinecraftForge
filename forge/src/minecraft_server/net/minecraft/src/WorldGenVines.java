package net.minecraft.src;

import java.util.Random;

public class WorldGenVines extends WorldGenerator
{
    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int var6 = par3;

        for (int var7 = par5; par4 < 128; ++par4)
        {
            if (par1World.isAirBlock(par3, par4, par5))
            {
                for (int var8 = 2; var8 <= 5; ++var8)
                {
                    if (Block.vine.canPlaceBlockOnSide(par1World, par3, par4, par5, var8))
                    {
                        par1World.setBlockAndMetadata(par3, par4, par5, Block.vine.blockID, 1 << Direction.vineGrowth[Facing.faceToSide[var8]]);
                        break;
                    }
                }
            }
            else
            {
                par3 = var6 + par2Random.nextInt(4) - par2Random.nextInt(4);
                par5 = var7 + par2Random.nextInt(4) - par2Random.nextInt(4);
            }
        }

        return true;
    }
}
