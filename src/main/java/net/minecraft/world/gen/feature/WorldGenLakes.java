package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator
{
    private Block field_150556_a;
    private static final String __OBFID = "CL_00000418";

    public WorldGenLakes(Block p_i45455_1_)
    {
        this.field_150556_a = p_i45455_1_;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        par3 -= 8;

        for (par5 -= 8; par4 > 5 && par1World.func_147437_c(par3, par4, par5); --par4)
        {
            ;
        }

        if (par4 <= 4)
        {
            return false;
        }
        else
        {
            par4 -= 4;
            boolean[] aboolean = new boolean[2048];
            int l = par2Random.nextInt(4) + 4;
            int i1;

            for (i1 = 0; i1 < l; ++i1)
            {
                double d0 = par2Random.nextDouble() * 6.0D + 3.0D;
                double d1 = par2Random.nextDouble() * 4.0D + 2.0D;
                double d2 = par2Random.nextDouble() * 6.0D + 3.0D;
                double d3 = par2Random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                double d4 = par2Random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                double d5 = par2Random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int k1 = 1; k1 < 15; ++k1)
                {
                    for (int l1 = 1; l1 < 15; ++l1)
                    {
                        for (int i2 = 1; i2 < 7; ++i2)
                        {
                            double d6 = ((double)k1 - d3) / (d0 / 2.0D);
                            double d7 = ((double)i2 - d4) / (d1 / 2.0D);
                            double d8 = ((double)l1 - d5) / (d2 / 2.0D);
                            double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                            if (d9 < 1.0D)
                            {
                                aboolean[(k1 * 16 + l1) * 8 + i2] = true;
                            }
                        }
                    }
                }
            }

            int j1;
            int j2;
            boolean flag;

            for (i1 = 0; i1 < 16; ++i1)
            {
                for (j2 = 0; j2 < 16; ++j2)
                {
                    for (j1 = 0; j1 < 8; ++j1)
                    {
                        flag = !aboolean[(i1 * 16 + j2) * 8 + j1] && (i1 < 15 && aboolean[((i1 + 1) * 16 + j2) * 8 + j1] || i1 > 0 && aboolean[((i1 - 1) * 16 + j2) * 8 + j1] || j2 < 15 && aboolean[(i1 * 16 + j2 + 1) * 8 + j1] || j2 > 0 && aboolean[(i1 * 16 + (j2 - 1)) * 8 + j1] || j1 < 7 && aboolean[(i1 * 16 + j2) * 8 + j1 + 1] || j1 > 0 && aboolean[(i1 * 16 + j2) * 8 + (j1 - 1)]);

                        if (flag)
                        {
                            Material material = par1World.func_147439_a(par3 + i1, par4 + j1, par5 + j2).func_149688_o();

                            if (j1 >= 4 && material.isLiquid())
                            {
                                return false;
                            }

                            if (j1 < 4 && !material.isSolid() && par1World.func_147439_a(par3 + i1, par4 + j1, par5 + j2) != this.field_150556_a)
                            {
                                return false;
                            }
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1)
            {
                for (j2 = 0; j2 < 16; ++j2)
                {
                    for (j1 = 0; j1 < 8; ++j1)
                    {
                        if (aboolean[(i1 * 16 + j2) * 8 + j1])
                        {
                            par1World.func_147465_d(par3 + i1, par4 + j1, par5 + j2, j1 >= 4 ? Blocks.air : this.field_150556_a, 0, 2);
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1)
            {
                for (j2 = 0; j2 < 16; ++j2)
                {
                    for (j1 = 4; j1 < 8; ++j1)
                    {
                        if (aboolean[(i1 * 16 + j2) * 8 + j1] && par1World.func_147439_a(par3 + i1, par4 + j1 - 1, par5 + j2) == Blocks.dirt && par1World.getSavedLightValue(EnumSkyBlock.Sky, par3 + i1, par4 + j1, par5 + j2) > 0)
                        {
                            BiomeGenBase biomegenbase = par1World.getBiomeGenForCoords(par3 + i1, par5 + j2);

                            if (biomegenbase.topBlock == Blocks.mycelium)
                            {
                                par1World.func_147465_d(par3 + i1, par4 + j1 - 1, par5 + j2, Blocks.mycelium, 0, 2);
                            }
                            else
                            {
                                par1World.func_147465_d(par3 + i1, par4 + j1 - 1, par5 + j2, Blocks.grass, 0, 2);
                            }
                        }
                    }
                }
            }

            if (this.field_150556_a.func_149688_o() == Material.field_151587_i)
            {
                for (i1 = 0; i1 < 16; ++i1)
                {
                    for (j2 = 0; j2 < 16; ++j2)
                    {
                        for (j1 = 0; j1 < 8; ++j1)
                        {
                            flag = !aboolean[(i1 * 16 + j2) * 8 + j1] && (i1 < 15 && aboolean[((i1 + 1) * 16 + j2) * 8 + j1] || i1 > 0 && aboolean[((i1 - 1) * 16 + j2) * 8 + j1] || j2 < 15 && aboolean[(i1 * 16 + j2 + 1) * 8 + j1] || j2 > 0 && aboolean[(i1 * 16 + (j2 - 1)) * 8 + j1] || j1 < 7 && aboolean[(i1 * 16 + j2) * 8 + j1 + 1] || j1 > 0 && aboolean[(i1 * 16 + j2) * 8 + (j1 - 1)]);

                            if (flag && (j1 < 4 || par2Random.nextInt(2) != 0) && par1World.func_147439_a(par3 + i1, par4 + j1, par5 + j2).func_149688_o().isSolid())
                            {
                                par1World.func_147465_d(par3 + i1, par4 + j1, par5 + j2, Blocks.stone, 0, 2);
                            }
                        }
                    }
                }
            }

            if (this.field_150556_a.func_149688_o() == Material.field_151586_h)
            {
                for (i1 = 0; i1 < 16; ++i1)
                {
                    for (j2 = 0; j2 < 16; ++j2)
                    {
                        byte b0 = 4;

                        if (par1World.isBlockFreezable(par3 + i1, par4 + b0, par5 + j2))
                        {
                            par1World.func_147465_d(par3 + i1, par4 + b0, par5 + j2, Blocks.ice, 0, 2);
                        }
                    }
                }
            }

            return true;
        }
    }
}