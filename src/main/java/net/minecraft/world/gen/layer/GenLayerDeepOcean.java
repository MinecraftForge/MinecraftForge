package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerDeepOcean extends GenLayer
{
    private static final String __OBFID = "CL_00000546";

    public GenLayerDeepOcean(long p_i45472_1_, GenLayer p_i45472_3_)
    {
        super(p_i45472_1_);
        this.parent = p_i45472_3_;
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int i1 = par1 - 1;
        int j1 = par2 - 1;
        int k1 = par3 + 2;
        int l1 = par4 + 2;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i2 = 0; i2 < par4; ++i2)
        {
            for (int j2 = 0; j2 < par3; ++j2)
            {
                int k2 = aint[j2 + 1 + (i2 + 1 - 1) * (par3 + 2)];
                int l2 = aint[j2 + 1 + 1 + (i2 + 1) * (par3 + 2)];
                int i3 = aint[j2 + 1 - 1 + (i2 + 1) * (par3 + 2)];
                int j3 = aint[j2 + 1 + (i2 + 1 + 1) * (par3 + 2)];
                int k3 = aint[j2 + 1 + (i2 + 1) * k1];
                int l3 = 0;

                if (k2 == 0)
                {
                    ++l3;
                }

                if (l2 == 0)
                {
                    ++l3;
                }

                if (i3 == 0)
                {
                    ++l3;
                }

                if (j3 == 0)
                {
                    ++l3;
                }

                if (k3 == 0 && l3 > 3)
                {
                    aint1[j2 + i2 * par3] = BiomeGenBase.field_150575_M.biomeID;
                }
                else
                {
                    aint1[j2 + i2 * par3] = k3;
                }
            }
        }

        return aint1;
    }
}