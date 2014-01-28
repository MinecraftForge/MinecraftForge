package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer
{
    private static final String __OBFID = "CL_00000551";

    public GenLayerAddIsland(long par1, GenLayer par3GenLayer)
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
                int k2 = aint[j2 + 0 + (i2 + 0) * k1];
                int l2 = aint[j2 + 2 + (i2 + 0) * k1];
                int i3 = aint[j2 + 0 + (i2 + 2) * k1];
                int j3 = aint[j2 + 2 + (i2 + 2) * k1];
                int k3 = aint[j2 + 1 + (i2 + 1) * k1];
                this.initChunkSeed((long)(j2 + par1), (long)(i2 + par2));

                if (k3 == 0 && (k2 != 0 || l2 != 0 || i3 != 0 || j3 != 0))
                {
                    int l3 = 1;
                    int i4 = 1;

                    if (k2 != 0 && this.nextInt(l3++) == 0)
                    {
                        i4 = k2;
                    }

                    if (l2 != 0 && this.nextInt(l3++) == 0)
                    {
                        i4 = l2;
                    }

                    if (i3 != 0 && this.nextInt(l3++) == 0)
                    {
                        i4 = i3;
                    }

                    if (j3 != 0 && this.nextInt(l3++) == 0)
                    {
                        i4 = j3;
                    }

                    if (this.nextInt(3) == 0)
                    {
                        aint1[j2 + i2 * par3] = i4;
                    }
                    else if (i4 == 4)
                    {
                        aint1[j2 + i2 * par3] = 4;
                    }
                    else
                    {
                        aint1[j2 + i2 * par3] = 0;
                    }
                }
                else if (k3 > 0 && (k2 == 0 || l2 == 0 || i3 == 0 || j3 == 0))
                {
                    if (this.nextInt(5) == 0)
                    {
                        if (k3 == 4)
                        {
                            aint1[j2 + i2 * par3] = 4;
                        }
                        else
                        {
                            aint1[j2 + i2 * par3] = 0;
                        }
                    }
                    else
                    {
                        aint1[j2 + i2 * par3] = k3;
                    }
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