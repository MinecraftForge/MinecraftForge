package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator
{
    private Block field_150553_a;
    private boolean field_94524_b;
    private static final String __OBFID = "CL_00000414";

    public WorldGenHellLava(Block p_i45453_1_, boolean p_i45453_2_)
    {
        this.field_150553_a = p_i45453_1_;
        this.field_94524_b = p_i45453_2_;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        if (par1World.func_147439_a(par3, par4 + 1, par5) != Blocks.netherrack)
        {
            return false;
        }
        else if (par1World.func_147439_a(par3, par4, par5).func_149688_o() != Material.field_151579_a && par1World.func_147439_a(par3, par4, par5) != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            int l = 0;

            if (par1World.func_147439_a(par3 - 1, par4, par5) == Blocks.netherrack)
            {
                ++l;
            }

            if (par1World.func_147439_a(par3 + 1, par4, par5) == Blocks.netherrack)
            {
                ++l;
            }

            if (par1World.func_147439_a(par3, par4, par5 - 1) == Blocks.netherrack)
            {
                ++l;
            }

            if (par1World.func_147439_a(par3, par4, par5 + 1) == Blocks.netherrack)
            {
                ++l;
            }

            if (par1World.func_147439_a(par3, par4 - 1, par5) == Blocks.netherrack)
            {
                ++l;
            }

            int i1 = 0;

            if (par1World.func_147437_c(par3 - 1, par4, par5))
            {
                ++i1;
            }

            if (par1World.func_147437_c(par3 + 1, par4, par5))
            {
                ++i1;
            }

            if (par1World.func_147437_c(par3, par4, par5 - 1))
            {
                ++i1;
            }

            if (par1World.func_147437_c(par3, par4, par5 + 1))
            {
                ++i1;
            }

            if (par1World.func_147437_c(par3, par4 - 1, par5))
            {
                ++i1;
            }

            if (!this.field_94524_b && l == 4 && i1 == 1 || l == 5)
            {
                par1World.func_147465_d(par3, par4, par5, this.field_150553_a, 0, 2);
                par1World.scheduledUpdatesAreImmediate = true;
                this.field_150553_a.func_149674_a(par1World, par3, par4, par5, par2Random);
                par1World.scheduledUpdatesAreImmediate = false;
            }

            return true;
        }
    }
}