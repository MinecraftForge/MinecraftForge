package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer
{
    private GenLayer biomePatternGeneratorChain;
    private GenLayer riverPatternGeneratorChain;
    private static final String __OBFID = "CL_00000567";

    public GenLayerRiverMix(long par1, GenLayer par3GenLayer, GenLayer par4GenLayer)
    {
        super(par1);
        this.biomePatternGeneratorChain = par3GenLayer;
        this.riverPatternGeneratorChain = par4GenLayer;
    }

    // JAVADOC METHOD $$ func_75905_a
    public void initWorldGenSeed(long par1)
    {
        this.biomePatternGeneratorChain.initWorldGenSeed(par1);
        this.riverPatternGeneratorChain.initWorldGenSeed(par1);
        super.initWorldGenSeed(par1);
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] aint = this.biomePatternGeneratorChain.getInts(par1, par2, par3, par4);
        int[] aint1 = this.riverPatternGeneratorChain.getInts(par1, par2, par3, par4);
        int[] aint2 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par3 * par4; ++i1)
        {
            if (aint[i1] != BiomeGenBase.ocean.biomeID && aint[i1] != BiomeGenBase.field_150575_M.biomeID)
            {
                if (aint1[i1] == BiomeGenBase.river.biomeID)
                {
                    if (aint[i1] == BiomeGenBase.icePlains.biomeID)
                    {
                        aint2[i1] = BiomeGenBase.frozenRiver.biomeID;
                    }
                    else if (aint[i1] != BiomeGenBase.mushroomIsland.biomeID && aint[i1] != BiomeGenBase.mushroomIslandShore.biomeID)
                    {
                        aint2[i1] = aint1[i1] & 255;
                    }
                    else
                    {
                        aint2[i1] = BiomeGenBase.mushroomIslandShore.biomeID;
                    }
                }
                else
                {
                    aint2[i1] = aint[i1];
                }
            }
            else
            {
                aint2[i1] = aint[i1];
            }
        }

        return aint2;
    }
}