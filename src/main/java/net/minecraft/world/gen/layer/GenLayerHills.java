package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer
{
    private static final Logger field_151629_c = LogManager.getLogger();
    private GenLayer field_151628_d;
    private static final String __OBFID = "CL_00000563";

    public GenLayerHills(long p_i45479_1_, GenLayer p_i45479_3_, GenLayer p_i45479_4_)
    {
        super(p_i45479_1_);
        this.parent = p_i45479_3_;
        this.field_151628_d = p_i45479_4_;
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] aint = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] aint1 = this.field_151628_d.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] aint2 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1)
        {
            for (int j1 = 0; j1 < par3; ++j1)
            {
                this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                int k1 = aint[j1 + 1 + (i1 + 1) * (par3 + 2)];
                int l1 = aint1[j1 + 1 + (i1 + 1) * (par3 + 2)];
                boolean flag = (l1 - 2) % 29 == 0;

                if (k1 > 255)
                {
                    field_151629_c.debug("old! " + k1);
                }

                if (k1 != 0 && l1 >= 2 && (l1 - 2) % 29 == 1 && k1 < 128)
                {
                    if (BiomeGenBase.func_150568_d(k1 + 128) != null)
                    {
                        aint2[j1 + i1 * par3] = k1 + 128;
                    }
                    else
                    {
                        aint2[j1 + i1 * par3] = k1;
                    }
                }
                else if (this.nextInt(3) != 0 && !flag)
                {
                    aint2[j1 + i1 * par3] = k1;
                }
                else
                {
                    int i2 = k1;
                    int j2;

                    if (k1 == BiomeGenBase.desert.biomeID)
                    {
                        i2 = BiomeGenBase.desertHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.forest.biomeID)
                    {
                        i2 = BiomeGenBase.forestHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.field_150583_P.biomeID)
                    {
                        i2 = BiomeGenBase.field_150582_Q.biomeID;
                    }
                    else if (k1 == BiomeGenBase.field_150585_R.biomeID)
                    {
                        i2 = BiomeGenBase.plains.biomeID;
                    }
                    else if (k1 == BiomeGenBase.taiga.biomeID)
                    {
                        i2 = BiomeGenBase.taigaHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.field_150578_U.biomeID)
                    {
                        i2 = BiomeGenBase.field_150581_V.biomeID;
                    }
                    else if (k1 == BiomeGenBase.field_150584_S.biomeID)
                    {
                        i2 = BiomeGenBase.field_150579_T.biomeID;
                    }
                    else if (k1 == BiomeGenBase.plains.biomeID)
                    {
                        if (this.nextInt(3) == 0)
                        {
                            i2 = BiomeGenBase.forestHills.biomeID;
                        }
                        else
                        {
                            i2 = BiomeGenBase.forest.biomeID;
                        }
                    }
                    else if (k1 == BiomeGenBase.icePlains.biomeID)
                    {
                        i2 = BiomeGenBase.iceMountains.biomeID;
                    }
                    else if (k1 == BiomeGenBase.jungle.biomeID)
                    {
                        i2 = BiomeGenBase.jungleHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.ocean.biomeID)
                    {
                        i2 = BiomeGenBase.field_150575_M.biomeID;
                    }
                    else if (k1 == BiomeGenBase.extremeHills.biomeID)
                    {
                        i2 = BiomeGenBase.field_150580_W.biomeID;
                    }
                    else if (k1 == BiomeGenBase.field_150588_X.biomeID)
                    {
                        i2 = BiomeGenBase.field_150587_Y.biomeID;
                    }
                    else if (func_151616_a(k1, BiomeGenBase.field_150607_aa.biomeID))
                    {
                        i2 = BiomeGenBase.field_150589_Z.biomeID;
                    }
                    else if (k1 == BiomeGenBase.field_150575_M.biomeID && this.nextInt(3) == 0)
                    {
                        j2 = this.nextInt(2);

                        if (j2 == 0)
                        {
                            i2 = BiomeGenBase.plains.biomeID;
                        }
                        else
                        {
                            i2 = BiomeGenBase.forest.biomeID;
                        }
                    }

                    if (flag && i2 != k1)
                    {
                        if (BiomeGenBase.func_150568_d(i2 + 128) != null)
                        {
                            i2 += 128;
                        }
                        else
                        {
                            i2 = k1;
                        }
                    }

                    if (i2 == k1)
                    {
                        aint2[j1 + i1 * par3] = k1;
                    }
                    else
                    {
                        j2 = aint[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)];
                        int k2 = aint[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)];
                        int l2 = aint[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)];
                        int i3 = aint[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)];
                        int j3 = 0;

                        if (func_151616_a(j2, k1))
                        {
                            ++j3;
                        }

                        if (func_151616_a(k2, k1))
                        {
                            ++j3;
                        }

                        if (func_151616_a(l2, k1))
                        {
                            ++j3;
                        }

                        if (func_151616_a(i3, k1))
                        {
                            ++j3;
                        }

                        if (j3 >= 3)
                        {
                            aint2[j1 + i1 * par3] = i2;
                        }
                        else
                        {
                            aint2[j1 + i1 * par3] = k1;
                        }
                    }
                }
            }
        }

        return aint2;
    }
}