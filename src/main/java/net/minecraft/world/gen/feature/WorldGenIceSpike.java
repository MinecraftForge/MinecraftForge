package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator
{
    private static final String __OBFID = "CL_00000417";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        while (par1World.func_147437_c(par3, par4, par5) && par4 > 2)
        {
            --par4;
        }

        if (par1World.func_147439_a(par3, par4, par5) != Blocks.snow)
        {
            return false;
        }
        else
        {
            par4 += par2Random.nextInt(4);
            int l = par2Random.nextInt(4) + 7;
            int i1 = l / 4 + par2Random.nextInt(2);

            if (i1 > 1 && par2Random.nextInt(60) == 0)
            {
                par4 += 10 + par2Random.nextInt(30);
            }

            int j1;
            int k1;
            int l1;

            for (j1 = 0; j1 < l; ++j1)
            {
                float f = (1.0F - (float)j1 / (float)l) * (float)i1;
                k1 = MathHelper.ceiling_float_int(f);

                for (l1 = -k1; l1 <= k1; ++l1)
                {
                    float f1 = (float)MathHelper.abs_int(l1) - 0.25F;

                    for (int i2 = -k1; i2 <= k1; ++i2)
                    {
                        float f2 = (float)MathHelper.abs_int(i2) - 0.25F;

                        if ((l1 == 0 && i2 == 0 || f1 * f1 + f2 * f2 <= f * f) && (l1 != -k1 && l1 != k1 && i2 != -k1 && i2 != k1 || par2Random.nextFloat() <= 0.75F))
                        {
                            Block block = par1World.func_147439_a(par3 + l1, par4 + j1, par5 + i2);

                            if (block.func_149688_o() == Material.field_151579_a || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice)
                            {
                                this.func_150515_a(par1World, par3 + l1, par4 + j1, par5 + i2, Blocks.packed_ice);
                            }

                            if (j1 != 0 && k1 > 1)
                            {
                                block = par1World.func_147439_a(par3 + l1, par4 - j1, par5 + i2);

                                if (block.func_149688_o() == Material.field_151579_a || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice)
                                {
                                    this.func_150515_a(par1World, par3 + l1, par4 - j1, par5 + i2, Blocks.packed_ice);
                                }
                            }
                        }
                    }
                }
            }

            j1 = i1 - 1;

            if (j1 < 0)
            {
                j1 = 0;
            }
            else if (j1 > 1)
            {
                j1 = 1;
            }

            for (int j2 = -j1; j2 <= j1; ++j2)
            {
                k1 = -j1;

                while (k1 <= j1)
                {
                    l1 = par4 - 1;
                    int k2 = 50;

                    if (Math.abs(j2) == 1 && Math.abs(k1) == 1)
                    {
                        k2 = par2Random.nextInt(5);
                    }

                    while (true)
                    {
                        if (l1 > 50)
                        {
                            Block block1 = par1World.func_147439_a(par3 + j2, l1, par5 + k1);

                            if (block1.func_149688_o() == Material.field_151579_a || block1 == Blocks.dirt || block1 == Blocks.snow || block1 == Blocks.ice || block1 == Blocks.packed_ice)
                            {
                                this.func_150515_a(par1World, par3 + j2, l1, par5 + k1, Blocks.packed_ice);
                                --l1;
                                --k2;

                                if (k2 <= 0)
                                {
                                    l1 -= par2Random.nextInt(5) + 1;
                                    k2 = par2Random.nextInt(5);
                                }

                                continue;
                            }
                        }

                        ++k1;
                        break;
                    }
                }
            }

            return true;
        }
    }
}