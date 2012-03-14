package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ChunkProviderEnd implements IChunkProvider
{
    private Random endRNG;
    private NoiseGeneratorOctaves field_40393_j;
    private NoiseGeneratorOctaves field_40394_k;
    private NoiseGeneratorOctaves field_40391_l;
    public NoiseGeneratorOctaves field_40388_a;
    public NoiseGeneratorOctaves field_40386_b;
    private World endWorld;
    private double[] densities;

    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] field_40387_c;
    double[] field_40384_d;
    double[] field_40385_e;
    double[] field_40382_f;
    double[] field_40383_g;
    int[][] field_40395_h = new int[32][32];

    public ChunkProviderEnd(World par1World, long par2)
    {
        this.endWorld = par1World;
        this.endRNG = new Random(par2);
        this.field_40393_j = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.field_40394_k = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.field_40391_l = new NoiseGeneratorOctaves(this.endRNG, 8);
        this.field_40388_a = new NoiseGeneratorOctaves(this.endRNG, 10);
        this.field_40386_b = new NoiseGeneratorOctaves(this.endRNG, 16);
    }

    public void func_40380_a(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        byte var5 = 2;
        int var6 = var5 + 1;
        byte var7 = 33;
        int var8 = var5 + 1;
        this.densities = this.func_40379_a(this.densities, par1 * var5, 0, par2 * var5, var6, var7, var8);

        for (int var9 = 0; var9 < var5; ++var9)
        {
            for (int var10 = 0; var10 < var5; ++var10)
            {
                for (int var11 = 0; var11 < 32; ++var11)
                {
                    double var12 = 0.25D;
                    double var14 = this.densities[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var16 = this.densities[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var18 = this.densities[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var20 = this.densities[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var22 = (this.densities[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
                    double var24 = (this.densities[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
                    double var26 = (this.densities[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
                    double var28 = (this.densities[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

                    for (int var30 = 0; var30 < 4; ++var30)
                    {
                        double var31 = 0.125D;
                        double var33 = var14;
                        double var35 = var16;
                        double var37 = (var18 - var14) * var31;
                        double var39 = (var20 - var16) * var31;

                        for (int var41 = 0; var41 < 8; ++var41)
                        {
                            int var42 = var41 + var9 * 8 << 11 | 0 + var10 * 8 << 7 | var11 * 4 + var30;
                            short var43 = 128;
                            double var44 = 0.125D;
                            double var46 = var33;
                            double var48 = (var35 - var33) * var44;

                            for (int var50 = 0; var50 < 8; ++var50)
                            {
                                int var51 = 0;

                                if (var46 > 0.0D)
                                {
                                    var51 = Block.whiteStone.blockID;
                                }

                                par3ArrayOfByte[var42] = (byte)var51;
                                var42 += var43;
                                var46 += var48;
                            }

                            var33 += var37;
                            var35 += var39;
                        }

                        var14 += var22;
                        var16 += var24;
                        var18 += var26;
                        var20 += var28;
                    }
                }
            }
        }
    }

    public void func_40381_b(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        for (int var5 = 0; var5 < 16; ++var5)
        {
            for (int var6 = 0; var6 < 16; ++var6)
            {
                byte var7 = 1;
                int var8 = -1;
                byte var9 = (byte)Block.whiteStone.blockID;
                byte var10 = (byte)Block.whiteStone.blockID;

                for (int var11 = 127; var11 >= 0; --var11)
                {
                    int var12 = (var6 * 16 + var5) * 128 + var11;
                    byte var13 = par3ArrayOfByte[var12];

                    if (var13 == 0)
                    {
                        var8 = -1;
                    }
                    else if (var13 == Block.stone.blockID)
                    {
                        if (var8 == -1)
                        {
                            if (var7 <= 0)
                            {
                                var9 = 0;
                                var10 = (byte)Block.whiteStone.blockID;
                            }

                            var8 = var7;

                            if (var11 >= 0)
                            {
                                par3ArrayOfByte[var12] = var9;
                            }
                            else
                            {
                                par3ArrayOfByte[var12] = var10;
                            }
                        }
                        else if (var8 > 0)
                        {
                            --var8;
                            par3ArrayOfByte[var12] = var10;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates an empty chunk ready to put data from the server in
     */
    public Chunk loadChunk(int par1, int par2)
    {
        return this.provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int par1, int par2)
    {
        this.endRNG.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
        byte[] var3 = new byte[32768];
        this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
        this.func_40380_a(par1, par2, var3, this.biomesForGeneration);
        this.func_40381_b(par1, par2, var3, this.biomesForGeneration);
        Chunk var4 = new Chunk(this.endWorld, var3, par1, par2);
        var4.generateSkylightMap();
        return var4;
    }

    private double[] func_40379_a(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        double var8 = 684.412D;
        double var10 = 684.412D;
        this.field_40382_f = this.field_40388_a.generateNoiseOctaves(this.field_40382_f, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
        this.field_40383_g = this.field_40386_b.generateNoiseOctaves(this.field_40383_g, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
        var8 *= 2.0D;
        this.field_40387_c = this.field_40391_l.generateNoiseOctaves(this.field_40387_c, par2, par3, par4, par5, par6, par7, var8 / 80.0D, var10 / 160.0D, var8 / 80.0D);
        this.field_40384_d = this.field_40393_j.generateNoiseOctaves(this.field_40384_d, par2, par3, par4, par5, par6, par7, var8, var10, var8);
        this.field_40385_e = this.field_40394_k.generateNoiseOctaves(this.field_40385_e, par2, par3, par4, par5, par6, par7, var8, var10, var8);
        int var12 = 0;
        int var13 = 0;

        for (int var14 = 0; var14 < par5; ++var14)
        {
            for (int var15 = 0; var15 < par7; ++var15)
            {
                double var16 = (this.field_40382_f[var13] + 256.0D) / 512.0D;

                if (var16 > 1.0D)
                {
                    var16 = 1.0D;
                }

                double var18 = this.field_40383_g[var13] / 8000.0D;

                if (var18 < 0.0D)
                {
                    var18 = -var18 * 0.3D;
                }

                var18 = var18 * 3.0D - 2.0D;
                float var20 = (float)(var14 + par2 - 0) / 1.0F;
                float var21 = (float)(var15 + par4 - 0) / 1.0F;
                float var22 = 100.0F - MathHelper.sqrt_float(var20 * var20 + var21 * var21) * 8.0F;

                if (var22 > 80.0F)
                {
                    var22 = 80.0F;
                }

                if (var22 < -100.0F)
                {
                    var22 = -100.0F;
                }

                if (var18 > 1.0D)
                {
                    var18 = 1.0D;
                }

                var18 /= 8.0D;
                var18 = 0.0D;

                if (var16 < 0.0D)
                {
                    var16 = 0.0D;
                }

                var16 += 0.5D;
                var18 = var18 * (double)par6 / 16.0D;
                ++var13;
                double var23 = (double)par6 / 2.0D;

                for (int var25 = 0; var25 < par6; ++var25)
                {
                    double var26 = 0.0D;
                    double var28 = ((double)var25 - var23) * 8.0D / var16;

                    if (var28 < 0.0D)
                    {
                        var28 *= -1.0D;
                    }

                    double var30 = this.field_40384_d[var12] / 512.0D;
                    double var32 = this.field_40385_e[var12] / 512.0D;
                    double var34 = (this.field_40387_c[var12] / 10.0D + 1.0D) / 2.0D;

                    if (var34 < 0.0D)
                    {
                        var26 = var30;
                    }
                    else if (var34 > 1.0D)
                    {
                        var26 = var32;
                    }
                    else
                    {
                        var26 = var30 + (var32 - var30) * var34;
                    }

                    var26 -= 8.0D;
                    var26 += (double)var22;
                    byte var36 = 2;
                    double var37;

                    if (var25 > par6 / 2 - var36)
                    {
                        var37 = (double)((float)(var25 - (par6 / 2 - var36)) / 64.0F);

                        if (var37 < 0.0D)
                        {
                            var37 = 0.0D;
                        }

                        if (var37 > 1.0D)
                        {
                            var37 = 1.0D;
                        }

                        var26 = var26 * (1.0D - var37) + -3000.0D * var37;
                    }

                    var36 = 8;

                    if (var25 < var36)
                    {
                        var37 = (double)((float)(var36 - var25) / ((float)var36 - 1.0F));
                        var26 = var26 * (1.0D - var37) + -30.0D * var37;
                    }

                    par1ArrayOfDouble[var12] = var26;
                    ++var12;
                }
            }
        }

        return par1ArrayOfDouble;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        BlockSand.fallInstantly = true;
        int var4 = par2 * 16;
        int var5 = par3 * 16;
        BiomeGenBase var6 = this.endWorld.func_48454_a(var4 + 16, var5 + 16);
        var6.decorate(this.endWorld, this.endWorld.rand, var4, var5);
        BlockSand.fallInstantly = false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Unloads the 100 oldest chunks from memory, due to a bug with chunkSet.add() never being called it thinks the list
     * is always empty and will not remove any chunks.
     */
    public boolean unload100OldestChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "RandomLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        BiomeGenBase var5 = this.endWorld.func_48454_a(par2, par4);
        return var5 == null ? null : var5.getSpawnableList(par1EnumCreatureType);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5)
    {
        return null;
    }
}
