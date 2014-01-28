package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator
{
    private Block field_150520_a;
    private static final String __OBFID = "CL_00000433";

    public WorldGenSpikes(Block p_i45464_1_)
    {
        this.field_150520_a = p_i45464_1_;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        if (par1World.func_147437_c(par3, par4, par5) && par1World.func_147439_a(par3, par4 - 1, par5) == this.field_150520_a)
        {
            int l = par2Random.nextInt(32) + 6;
            int i1 = par2Random.nextInt(4) + 1;
            int j1;
            int k1;
            int l1;
            int i2;

            for (j1 = par3 - i1; j1 <= par3 + i1; ++j1)
            {
                for (k1 = par5 - i1; k1 <= par5 + i1; ++k1)
                {
                    l1 = j1 - par3;
                    i2 = k1 - par5;

                    if (l1 * l1 + i2 * i2 <= i1 * i1 + 1 && par1World.func_147439_a(j1, par4 - 1, k1) != this.field_150520_a)
                    {
                        return false;
                    }
                }
            }

            for (j1 = par4; j1 < par4 + l && j1 < 256; ++j1)
            {
                for (k1 = par3 - i1; k1 <= par3 + i1; ++k1)
                {
                    for (l1 = par5 - i1; l1 <= par5 + i1; ++l1)
                    {
                        i2 = k1 - par3;
                        int j2 = l1 - par5;

                        if (i2 * i2 + j2 * j2 <= i1 * i1 + 1)
                        {
                            par1World.func_147465_d(k1, j1, l1, Blocks.obsidian, 0, 2);
                        }
                    }
                }
            }

            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(par1World);
            entityendercrystal.setLocationAndAngles((double)((float)par3 + 0.5F), (double)(par4 + l), (double)((float)par5 + 0.5F), par2Random.nextFloat() * 360.0F, 0.0F);
            par1World.spawnEntityInWorld(entityendercrystal);
            par1World.func_147465_d(par3, par4 + l, par5, Blocks.bedrock, 0, 2);
            return true;
        }
        else
        {
            return false;
        }
    }
}