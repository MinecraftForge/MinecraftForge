package net.minecraft.world.gen.layer;

public class GenLayerVoronoiZoom extends GenLayer
{
    private static final String __OBFID = "CL_00000571";

    public GenLayerVoronoiZoom(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        super.parent = par3GenLayer;
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        par1 -= 2;
        par2 -= 2;
        int i1 = par1 >> 2;
        int j1 = par2 >> 2;
        int k1 = (par3 >> 2) + 2;
        int l1 = (par4 >> 2) + 2;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int i2 = k1 - 1 << 2;
        int j2 = l1 - 1 << 2;
        int[] aint1 = IntCache.getIntCache(i2 * j2);
        int l2;

        for (int k2 = 0; k2 < l1 - 1; ++k2)
        {
            l2 = 0;
            int i3 = aint[l2 + 0 + (k2 + 0) * k1];

            for (int j3 = aint[l2 + 0 + (k2 + 1) * k1]; l2 < k1 - 1; ++l2)
            {
                double d0 = 3.6D;
                this.initChunkSeed((long)(l2 + i1 << 2), (long)(k2 + j1 << 2));
                double d1 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                double d2 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                this.initChunkSeed((long)(l2 + i1 + 1 << 2), (long)(k2 + j1 << 2));
                double d3 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                double d4 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                this.initChunkSeed((long)(l2 + i1 << 2), (long)(k2 + j1 + 1 << 2));
                double d5 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                double d6 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                this.initChunkSeed((long)(l2 + i1 + 1 << 2), (long)(k2 + j1 + 1 << 2));
                double d7 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                double d8 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                int k3 = aint[l2 + 1 + (k2 + 0) * k1] & 255;
                int l3 = aint[l2 + 1 + (k2 + 1) * k1] & 255;

                for (int i4 = 0; i4 < 4; ++i4)
                {
                    int j4 = ((k2 << 2) + i4) * i2 + (l2 << 2);

                    for (int k4 = 0; k4 < 4; ++k4)
                    {
                        double d9 = ((double)i4 - d2) * ((double)i4 - d2) + ((double)k4 - d1) * ((double)k4 - d1);
                        double d10 = ((double)i4 - d4) * ((double)i4 - d4) + ((double)k4 - d3) * ((double)k4 - d3);
                        double d11 = ((double)i4 - d6) * ((double)i4 - d6) + ((double)k4 - d5) * ((double)k4 - d5);
                        double d12 = ((double)i4 - d8) * ((double)i4 - d8) + ((double)k4 - d7) * ((double)k4 - d7);

                        if (d9 < d10 && d9 < d11 && d9 < d12)
                        {
                            aint1[j4++] = i3;
                        }
                        else if (d10 < d9 && d10 < d11 && d10 < d12)
                        {
                            aint1[j4++] = k3;
                        }
                        else if (d11 < d9 && d11 < d10 && d11 < d12)
                        {
                            aint1[j4++] = j3;
                        }
                        else
                        {
                            aint1[j4++] = l3;
                        }
                    }
                }

                i3 = k3;
                j3 = l3;
            }
        }

        int[] aint2 = IntCache.getIntCache(par3 * par4);

        for (l2 = 0; l2 < par4; ++l2)
        {
            System.arraycopy(aint1, (l2 + (par2 & 3)) * i2 + (par1 & 3), aint2, l2 * par3, par3);
        }

        return aint2;
    }
}