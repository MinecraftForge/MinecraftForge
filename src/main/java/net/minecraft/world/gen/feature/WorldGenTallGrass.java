package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class WorldGenTallGrass extends WorldGenerator
{
    private Block field_150522_a;
    private int tallGrassMetadata;
    private static final String __OBFID = "CL_00000437";

    public WorldGenTallGrass(Block p_i45466_1_, int p_i45466_2_)
    {
        this.field_150522_a = p_i45466_1_;
        this.tallGrassMetadata = p_i45466_2_;
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

        for (int l = 0; l < 128; ++l)
        {
            int i1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
            int j1 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
            int k1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

            if (par1World.func_147437_c(i1, j1, k1) && this.field_150522_a.func_149718_j(par1World, i1, j1, k1))
            {
                par1World.func_147465_d(i1, j1, k1, this.field_150522_a, this.tallGrassMetadata, 2);
            }
        }

        return true;
    }
}