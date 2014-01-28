package net.minecraft.world.gen.layer;

public class GenLayerZoom extends GenLayer
{
    private static final String __OBFID = "CL_00000572";

    public GenLayerZoom(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        super.parent = par3GenLayer;
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int i1 = par1 >> 1;
        int j1 = par2 >> 1;
        int k1 = (par3 >> 1) + 2;
        int l1 = (par4 >> 1) + 2;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int i2 = k1 - 1 << 1;
        int j2 = l1 - 1 << 1;
        int[] aint1 = IntCache.getIntCache(i2 * j2);
        int l2;

        for (int k2 = 0; k2 < l1 - 1; ++k2)
        {
            l2 = (k2 << 1) * i2;
            int i3 = 0;
            int j3 = aint[i3 + 0 + (k2 + 0) * k1];

            for (int k3 = aint[i3 + 0 + (k2 + 1) * k1]; i3 < k1 - 1; ++i3)
            {
                this.initChunkSeed((long)(i3 + i1 << 1), (long)(k2 + j1 << 1));
                int l3 = aint[i3 + 1 + (k2 + 0) * k1];
                int i4 = aint[i3 + 1 + (k2 + 1) * k1];
                aint1[l2] = j3;
                aint1[l2++ + i2] = this.func_151619_a(new int[] {j3, k3});
                aint1[l2] = this.func_151619_a(new int[] {j3, l3});
                aint1[l2++ + i2] = this.func_151617_b(j3, l3, k3, i4);
                j3 = l3;
                k3 = i4;
            }
        }

        int[] aint2 = IntCache.getIntCache(par3 * par4);

        for (l2 = 0; l2 < par4; ++l2)
        {
            System.arraycopy(aint1, (l2 + (par2 & 1)) * i2 + (par1 & 1), aint2, l2 * par3, par3);
        }

        return aint2;
    }

    // JAVADOC METHOD $$ func_75915_a
    public static GenLayer magnify(long par0, GenLayer par2GenLayer, int par3)
    {
        Object object = par2GenLayer;

        for (int k = 0; k < par3; ++k)
        {
            object = new GenLayerZoom(par0 + (long)k, (GenLayer)object);
        }

        return (GenLayer)object;
    }
}