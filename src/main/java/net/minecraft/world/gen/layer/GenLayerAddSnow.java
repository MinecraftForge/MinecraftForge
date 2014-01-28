package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer
{
    private static final String __OBFID = "CL_00000553";

    public GenLayerAddSnow(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
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
                int k2 = aint[j2 + 1 + (i2 + 1) * k1];
                this.initChunkSeed((long)(j2 + par1), (long)(i2 + par2));

                if (k2 == 0)
                {
                    aint1[j2 + i2 * par3] = 0;
                }
                else
                {
                    int l2 = this.nextInt(6);
                    byte b0;

                    if (l2 == 0)
                    {
                        b0 = 4;
                    }
                    else if (l2 <= 1)
                    {
                        b0 = 3;
                    }
                    else
                    {
                        b0 = 1;
                    }

                    aint1[j2 + i2 * par3] = b0;
                }
            }
        }

        return aint1;
    }
}