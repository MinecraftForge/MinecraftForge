package net.minecraft.src;

import java.util.Random;

public class MapGenBase
{
    /** The number of Chunks to gen-check in any given direction. */
    protected int range = 8;

    /** The RNG used by the MapGen classes. */
    protected Random rand = new Random();

    /** This world object. */
    protected World worldObj;

    public void generate(IChunkProvider par1IChunkProvider, World par2World, int par3, int par4, byte[] par5ArrayOfByte)
    {
        int var6 = this.range;
        this.worldObj = par2World;
        this.rand.setSeed(par2World.getSeed());
        long var7 = this.rand.nextLong();
        long var9 = this.rand.nextLong();

        for (int var11 = par3 - var6; var11 <= par3 + var6; ++var11)
        {
            for (int var12 = par4 - var6; var12 <= par4 + var6; ++var12)
            {
                long var13 = (long)var11 * var7;
                long var15 = (long)var12 * var9;
                this.rand.setSeed(var13 ^ var15 ^ par2World.getSeed());
                this.recursiveGenerate(par2World, var11, var12, par3, par4, par5ArrayOfByte);
            }
        }
    }

    protected void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte) {}
}
