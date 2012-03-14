package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ChunkProviderHell implements IChunkProvider
{
    private Random hellRNG;
    private NoiseGeneratorOctaves field_4169_i;
    private NoiseGeneratorOctaves field_4168_j;
    private NoiseGeneratorOctaves field_4167_k;
    private NoiseGeneratorOctaves field_4166_l;
    private NoiseGeneratorOctaves field_4165_m;
    public NoiseGeneratorOctaves field_4177_a;
    public NoiseGeneratorOctaves field_4176_b;

    /** Is the world that the nether is getting generated. */
    private World worldObj;
    private double[] field_4163_o;
    public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
    private double[] field_4162_p = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] field_4160_r = new double[256];
    private MapGenBase netherCaveGenerator = new MapGenCavesHell();
    double[] field_4175_c;
    double[] field_4174_d;
    double[] field_4173_e;
    double[] field_4172_f;
    double[] field_4171_g;

    public ChunkProviderHell(World par1World, long par2)
    {
        this.worldObj = par1World;
        this.hellRNG = new Random(par2);
        this.field_4169_i = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.field_4168_j = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.field_4167_k = new NoiseGeneratorOctaves(this.hellRNG, 8);
        this.field_4166_l = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.field_4165_m = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.field_4177_a = new NoiseGeneratorOctaves(this.hellRNG, 10);
        this.field_4176_b = new NoiseGeneratorOctaves(this.hellRNG, 16);
    }

    /**
     * Generates the shape of the terrain in the nether.
     */
    public void generateNetherTerrain(int par1, int par2, byte[] par3ArrayOfByte)
    {
        byte var4 = 4;
        byte var5 = 32;
        int var6 = var4 + 1;
        byte var7 = 17;
        int var8 = var4 + 1;
        this.field_4163_o = this.func_4057_a(this.field_4163_o, par1 * var4, 0, par2 * var4, var6, var7, var8);

        for (int var9 = 0; var9 < var4; ++var9)
        {
            for (int var10 = 0; var10 < var4; ++var10)
            {
                for (int var11 = 0; var11 < 16; ++var11)
                {
                    double var12 = 0.125D;
                    double var14 = this.field_4163_o[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var16 = this.field_4163_o[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var18 = this.field_4163_o[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var20 = this.field_4163_o[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var22 = (this.field_4163_o[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
                    double var24 = (this.field_4163_o[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
                    double var26 = (this.field_4163_o[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
                    double var28 = (this.field_4163_o[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

                    for (int var30 = 0; var30 < 8; ++var30)
                    {
                        double var31 = 0.25D;
                        double var33 = var14;
                        double var35 = var16;
                        double var37 = (var18 - var14) * var31;
                        double var39 = (var20 - var16) * var31;

                        for (int var41 = 0; var41 < 4; ++var41)
                        {
                            int var42 = var41 + var9 * 4 << 11 | 0 + var10 * 4 << 7 | var11 * 8 + var30;
                            short var43 = 128;
                            double var44 = 0.25D;
                            double var46 = var33;
                            double var48 = (var35 - var33) * var44;

                            for (int var50 = 0; var50 < 4; ++var50)
                            {
                                int var51 = 0;

                                if (var11 * 8 + var30 < var5)
                                {
                                    var51 = Block.lavaStill.blockID;
                                }

                                if (var46 > 0.0D)
                                {
                                    var51 = Block.netherrack.blockID;
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

    public void func_4058_b(int par1, int par2, byte[] par3ArrayOfByte)
    {
        byte var4 = 64;
        double var5 = 0.03125D;
        this.field_4162_p = this.field_4166_l.generateNoiseOctaves(this.field_4162_p, par1 * 16, par2 * 16, 0, 16, 16, 1, var5, var5, 1.0D);
        this.gravelNoise = this.field_4166_l.generateNoiseOctaves(this.gravelNoise, par1 * 16, 109, par2 * 16, 16, 1, 16, var5, 1.0D, var5);
        this.field_4160_r = this.field_4165_m.generateNoiseOctaves(this.field_4160_r, par1 * 16, par2 * 16, 0, 16, 16, 1, var5 * 2.0D, var5 * 2.0D, var5 * 2.0D);

        for (int var7 = 0; var7 < 16; ++var7)
        {
            for (int var8 = 0; var8 < 16; ++var8)
            {
                boolean var9 = this.field_4162_p[var7 + var8 * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                boolean var10 = this.gravelNoise[var7 + var8 * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                int var11 = (int)(this.field_4160_r[var7 + var8 * 16] / 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D);
                int var12 = -1;
                byte var13 = (byte)Block.netherrack.blockID;
                byte var14 = (byte)Block.netherrack.blockID;

                for (int var15 = 127; var15 >= 0; --var15)
                {
                    int var16 = (var8 * 16 + var7) * 128 + var15;

                    if (var15 >= 127 - this.hellRNG.nextInt(5))
                    {
                        par3ArrayOfByte[var16] = (byte)Block.bedrock.blockID;
                    }
                    else if (var15 <= 0 + this.hellRNG.nextInt(5))
                    {
                        par3ArrayOfByte[var16] = (byte)Block.bedrock.blockID;
                    }
                    else
                    {
                        byte var17 = par3ArrayOfByte[var16];

                        if (var17 == 0)
                        {
                            var12 = -1;
                        }
                        else if (var17 == Block.netherrack.blockID)
                        {
                            if (var12 == -1)
                            {
                                if (var11 <= 0)
                                {
                                    var13 = 0;
                                    var14 = (byte)Block.netherrack.blockID;
                                }
                                else if (var15 >= var4 - 4 && var15 <= var4 + 1)
                                {
                                    var13 = (byte)Block.netherrack.blockID;
                                    var14 = (byte)Block.netherrack.blockID;

                                    if (var10)
                                    {
                                        var13 = (byte)Block.gravel.blockID;
                                    }

                                    if (var10)
                                    {
                                        var14 = (byte)Block.netherrack.blockID;
                                    }

                                    if (var9)
                                    {
                                        var13 = (byte)Block.slowSand.blockID;
                                    }

                                    if (var9)
                                    {
                                        var14 = (byte)Block.slowSand.blockID;
                                    }
                                }

                                if (var15 < var4 && var13 == 0)
                                {
                                    var13 = (byte)Block.lavaStill.blockID;
                                }

                                var12 = var11;

                                if (var15 >= var4 - 1)
                                {
                                    par3ArrayOfByte[var16] = var13;
                                }
                                else
                                {
                                    par3ArrayOfByte[var16] = var14;
                                }
                            }
                            else if (var12 > 0)
                            {
                                --var12;
                                par3ArrayOfByte[var16] = var14;
                            }
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
        this.hellRNG.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
        byte[] var3 = new byte[32768];
        this.generateNetherTerrain(par1, par2, var3);
        this.func_4058_b(par1, par2, var3);
        this.netherCaveGenerator.generate(this, this.worldObj, par1, par2, var3);
        this.genNetherBridge.generate(this, this.worldObj, par1, par2, var3);
        Chunk var4 = new Chunk(this.worldObj, var3, par1, par2);
        var4.func_48496_n();
        return var4;
    }

    private double[] func_4057_a(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        double var8 = 684.412D;
        double var10 = 2053.236D;
        this.field_4172_f = this.field_4177_a.generateNoiseOctaves(this.field_4172_f, par2, par3, par4, par5, 1, par7, 1.0D, 0.0D, 1.0D);
        this.field_4171_g = this.field_4176_b.generateNoiseOctaves(this.field_4171_g, par2, par3, par4, par5, 1, par7, 100.0D, 0.0D, 100.0D);
        this.field_4175_c = this.field_4167_k.generateNoiseOctaves(this.field_4175_c, par2, par3, par4, par5, par6, par7, var8 / 80.0D, var10 / 60.0D, var8 / 80.0D);
        this.field_4174_d = this.field_4169_i.generateNoiseOctaves(this.field_4174_d, par2, par3, par4, par5, par6, par7, var8, var10, var8);
        this.field_4173_e = this.field_4168_j.generateNoiseOctaves(this.field_4173_e, par2, par3, par4, par5, par6, par7, var8, var10, var8);
        int var12 = 0;
        int var13 = 0;
        double[] var14 = new double[par6];
        int var15;

        for (var15 = 0; var15 < par6; ++var15)
        {
            var14[var15] = Math.cos((double)var15 * Math.PI * 6.0D / (double)par6) * 2.0D;
            double var16 = (double)var15;

            if (var15 > par6 / 2)
            {
                var16 = (double)(par6 - 1 - var15);
            }

            if (var16 < 4.0D)
            {
                var16 = 4.0D - var16;
                var14[var15] -= var16 * var16 * var16 * 10.0D;
            }
        }

        for (var15 = 0; var15 < par5; ++var15)
        {
            for (int var36 = 0; var36 < par7; ++var36)
            {
                double var17 = (this.field_4172_f[var13] + 256.0D) / 512.0D;

                if (var17 > 1.0D)
                {
                    var17 = 1.0D;
                }

                double var19 = 0.0D;
                double var21 = this.field_4171_g[var13] / 8000.0D;

                if (var21 < 0.0D)
                {
                    var21 = -var21;
                }

                var21 = var21 * 3.0D - 3.0D;

                if (var21 < 0.0D)
                {
                    var21 /= 2.0D;

                    if (var21 < -1.0D)
                    {
                        var21 = -1.0D;
                    }

                    var21 /= 1.4D;
                    var21 /= 2.0D;
                    var17 = 0.0D;
                }
                else
                {
                    if (var21 > 1.0D)
                    {
                        var21 = 1.0D;
                    }

                    var21 /= 6.0D;
                }

                var17 += 0.5D;
                var21 = var21 * (double)par6 / 16.0D;
                ++var13;

                for (int var23 = 0; var23 < par6; ++var23)
                {
                    double var24 = 0.0D;
                    double var26 = var14[var23];
                    double var28 = this.field_4174_d[var12] / 512.0D;
                    double var30 = this.field_4173_e[var12] / 512.0D;
                    double var32 = (this.field_4175_c[var12] / 10.0D + 1.0D) / 2.0D;

                    if (var32 < 0.0D)
                    {
                        var24 = var28;
                    }
                    else if (var32 > 1.0D)
                    {
                        var24 = var30;
                    }
                    else
                    {
                        var24 = var28 + (var30 - var28) * var32;
                    }

                    var24 -= var26;
                    double var34;

                    if (var23 > par6 - 4)
                    {
                        var34 = (double)((float)(var23 - (par6 - 4)) / 3.0F);
                        var24 = var24 * (1.0D - var34) + -10.0D * var34;
                    }

                    if ((double)var23 < var19)
                    {
                        var34 = (var19 - (double)var23) / 4.0D;

                        if (var34 < 0.0D)
                        {
                            var34 = 0.0D;
                        }

                        if (var34 > 1.0D)
                        {
                            var34 = 1.0D;
                        }

                        var24 = var24 * (1.0D - var34) + -10.0D * var34;
                    }

                    par1ArrayOfDouble[var12] = var24;
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
        this.genNetherBridge.generateStructuresInChunk(this.worldObj, this.hellRNG, par2, par3);
        int var6;
        int var7;
        int var8;
        int var9;

        for (var6 = 0; var6 < 8; ++var6)
        {
            var7 = var4 + this.hellRNG.nextInt(16) + 8;
            var8 = this.hellRNG.nextInt(120) + 4;
            var9 = var5 + this.hellRNG.nextInt(16) + 8;
            (new WorldGenHellLava(Block.lavaMoving.blockID)).generate(this.worldObj, this.hellRNG, var7, var8, var9);
        }

        var6 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;
        int var10;

        for (var7 = 0; var7 < var6; ++var7)
        {
            var8 = var4 + this.hellRNG.nextInt(16) + 8;
            var9 = this.hellRNG.nextInt(120) + 4;
            var10 = var5 + this.hellRNG.nextInt(16) + 8;
            (new WorldGenFire()).generate(this.worldObj, this.hellRNG, var8, var9, var10);
        }

        var6 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

        for (var7 = 0; var7 < var6; ++var7)
        {
            var8 = var4 + this.hellRNG.nextInt(16) + 8;
            var9 = this.hellRNG.nextInt(120) + 4;
            var10 = var5 + this.hellRNG.nextInt(16) + 8;
            (new WorldGenGlowStone1()).generate(this.worldObj, this.hellRNG, var8, var9, var10);
        }

        for (var7 = 0; var7 < 10; ++var7)
        {
            var8 = var4 + this.hellRNG.nextInt(16) + 8;
            var9 = this.hellRNG.nextInt(128);
            var10 = var5 + this.hellRNG.nextInt(16) + 8;
            (new WorldGenGlowStone2()).generate(this.worldObj, this.hellRNG, var8, var9, var10);
        }

        if (this.hellRNG.nextInt(1) == 0)
        {
            var7 = var4 + this.hellRNG.nextInt(16) + 8;
            var8 = this.hellRNG.nextInt(128);
            var9 = var5 + this.hellRNG.nextInt(16) + 8;
            (new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.hellRNG, var7, var8, var9);
        }

        if (this.hellRNG.nextInt(1) == 0)
        {
            var7 = var4 + this.hellRNG.nextInt(16) + 8;
            var8 = this.hellRNG.nextInt(128);
            var9 = var5 + this.hellRNG.nextInt(16) + 8;
            (new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.hellRNG, var7, var8, var9);
        }

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
        return "HellRandomLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        if (par1EnumCreatureType == EnumCreatureType.monster && this.genNetherBridge.func_40483_a(par2, par3, par4))
        {
            return this.genNetherBridge.getSpawnList();
        }
        else
        {
            BiomeGenBase var5 = this.worldObj.func_48454_a(par2, par4);
            return var5 == null ? null : var5.getSpawnableList(par1EnumCreatureType);
        }
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5)
    {
        return null;
    }
}
