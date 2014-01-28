package net.minecraft.world.gen.layer;

public class GenLayerIsland extends GenLayer
{
    private static final String __OBFID = "CL_00000558";

    public GenLayerIsland(long par1)
    {
        super(par1);
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] aint = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1)
        {
            for (int j1 = 0; j1 < par3; ++j1)
            {
                this.initChunkSeed((long)(par1 + j1), (long)(par2 + i1));
                aint[j1 + i1 * par3] = this.nextInt(10) == 0 ? 1 : 0;
            }
        }

        if (par1 > -par3 && par1 <= 0 && par2 > -par4 && par2 <= 0)
        {
            aint[-par1 + -par2 * par3] = 1;
        }

        return aint;
    }
}