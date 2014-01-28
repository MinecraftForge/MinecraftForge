package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees
{
    private int field_150528_a;
    private int field_150527_b;
    private static final String __OBFID = "CL_00000411";

    public WorldGenShrub(int par1, int par2)
    {
        super(false);
        this.field_150527_b = par1;
        this.field_150528_a = par2;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        Block block;

        do
        {
            block = par1World.func_147439_a(par3, par4, par5);
            if (!(block.isLeaves(par1World, par3, par4, par5) || block.isAir(par1World, par3, par4, par5)))
            {
                break;
            }
            --par4;
        } while (par4 > 0);

        Block block1 = par1World.func_147439_a(par3, par4, par5);

        if (block1 == Blocks.dirt || block1 == Blocks.grass)
        {
            ++par4;
            this.func_150516_a(par1World, par3, par4, par5, Blocks.log, this.field_150527_b);

            for (int l = par4; l <= par4 + 2; ++l)
            {
                int i1 = l - par4;
                int j1 = 2 - i1;

                for (int k1 = par3 - j1; k1 <= par3 + j1; ++k1)
                {
                    int l1 = k1 - par3;

                    for (int i2 = par5 - j1; i2 <= par5 + j1; ++i2)
                    {
                        int j2 = i2 - par5;

                        if ((Math.abs(l1) != j1 || Math.abs(j2) != j1 || par2Random.nextInt(2) != 0) && par1World.func_147439_a(k1, l, i2).canBeReplacedByLeaves(par1World, k1, l, i2))
                        {
                            this.func_150516_a(par1World, k1, l, i2, Blocks.leaves, this.field_150528_a);
                        }
                    }
                }
            }
        }

        return true;
    }
}