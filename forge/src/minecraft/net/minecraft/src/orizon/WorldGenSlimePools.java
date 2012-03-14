package net.minecraft.src.orizon;
import net.minecraft.src.*;
import java.util.Random;

public class WorldGenSlimePools extends WorldGenerator
{
    private int blockIndex;

    public WorldGenSlimePools(int par1)
    {
        this.blockIndex = par1;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        par3 -= 8;

        for (par5 -= 8; par4 > 5 && par1World.isAirBlock(par3, par4, par5); --par4)
        {
            ;
        }

        if (par4 <= 4)
        {
            return false;
        }
        else
        {
            par4 -= 4;
            boolean[] var6 = new boolean[2048];
            int var7 = par2Random.nextInt(4) + 4;
            int var8;

            for (var8 = 0; var8 < var7; ++var8)
            {
                double var9 = par2Random.nextDouble() * 6.0D + 3.0D;
                double var11 = par2Random.nextDouble() * 4.0D + 2.0D;
                double var13 = par2Random.nextDouble() * 6.0D + 3.0D;
                double var15 = par2Random.nextDouble() * (16.0D - var9 - 2.0D) + 1.0D + var9 / 2.0D;
                double var17 = par2Random.nextDouble() * (8.0D - var11 - 4.0D) + 2.0D + var11 / 2.0D;
                double var19 = par2Random.nextDouble() * (16.0D - var13 - 2.0D) + 1.0D + var13 / 2.0D;

                for (int var21 = 1; var21 < 15; ++var21)
                {
                    for (int var22 = 1; var22 < 15; ++var22)
                    {
                        for (int var23 = 1; var23 < 7; ++var23)
                        {
                            double var24 = ((double)var21 - var15) / (var9 / 2.0D);
                            double var26 = ((double)var23 - var17) / (var11 / 2.0D);
                            double var28 = ((double)var22 - var19) / (var13 / 2.0D);
                            double var30 = var24 * var24 + var26 * var26 + var28 * var28;

                            if (var30 < 1.0D)
                            {
                                var6[(var21 * 16 + var22) * 8 + var23] = true;
                            }
                        }
                    }
                }
            }

            int var10;
            int var32;
            boolean var33;

            for (var8 = 0; var8 < 16; ++var8)
            {
                for (var32 = 0; var32 < 16; ++var32)
                {
                    for (var10 = 0; var10 < 8; ++var10)
                    {
                        var33 = !var6[(var8 * 16 + var32) * 8 + var10] && (var8 < 15 && var6[((var8 + 1) * 16 + var32) * 8 + var10] || var8 > 0 && var6[((var8 - 1) * 16 + var32) * 8 + var10] || var32 < 15 && var6[(var8 * 16 + var32 + 1) * 8 + var10] || var32 > 0 && var6[(var8 * 16 + (var32 - 1)) * 8 + var10] || var10 < 7 && var6[(var8 * 16 + var32) * 8 + var10 + 1] || var10 > 0 && var6[(var8 * 16 + var32) * 8 + (var10 - 1)]);

                        if (var33)
                        {
                            Material var12 = par1World.getBlockMaterial(par3 + var8, par4 + var10, par5 + var32);

                            if (var10 >= 4 && var12.isLiquid())
                            {
                                return false;
                            }

                            if (var10 < 4 && !var12.isSolid() && par1World.getBlockId(par3 + var8, par4 + var10, par5 + var32) != this.blockIndex)
                            {
                                return false;
                            }
                        }
                    }
                }
            }

            for (var8 = 0; var8 < 16; ++var8)
            {
                for (var32 = 0; var32 < 16; ++var32)
                {
                    for (var10 = 0; var10 < 8; ++var10)
                    {
                        if (var6[(var8 * 16 + var32) * 8 + var10])
                        {
                            par1World.setBlock(par3 + var8, par4 + var10, par5 + var32, var10 >= 4 ? 0 : this.blockIndex);
                        }
                    }
                }
            }

            for (var8 = 0; var8 < 16; ++var8)
            {
                for (var32 = 0; var32 < 16; ++var32)
                {
                    for (var10 = 4; var10 < 8; ++var10)
                    {
                        if (var6[(var8 * 16 + var32) * 8 + var10] && par1World.getBlockId(par3 + var8, par4 + var10 - 1, par5 + var32) == Block.dirt.blockID && par1World.getSavedLightValue(EnumSkyBlock.Sky, par3 + var8, par4 + var10, par5 + var32) > 0)
                        {
                            BiomeGenBase var35 = par1World.func_48454_a(par3 + var8, par5 + var32);

                            if (var35.topBlock == Block.mycelium.blockID)
                            {
                                par1World.setBlock(par3 + var8, par4 + var10 - 1, par5 + var32, Block.mycelium.blockID);
                            }
                            else
                            {
                                par1World.setBlock(par3 + var8, par4 + var10 - 1, par5 + var32, Block.grass.blockID);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
