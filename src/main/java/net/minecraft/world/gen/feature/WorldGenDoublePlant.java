package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator
{
    private int field_150549_a;
    private static final String __OBFID = "CL_00000408";

    public void func_150548_a(int p_150548_1_)
    {
        this.field_150549_a = p_150548_1_;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        boolean flag = false;

        for (int l = 0; l < 64; ++l)
        {
            int i1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
            int j1 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
            int k1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

            if (par1World.func_147437_c(i1, j1, k1) && (!par1World.provider.hasNoSky || j1 < 254) && Blocks.double_plant.func_149742_c(par1World, i1, j1, k1))
            {
                Blocks.double_plant.func_149889_c(par1World, i1, j1, k1, this.field_150549_a, 2);
                flag = true;
            }
        }

        return flag;
    }
}