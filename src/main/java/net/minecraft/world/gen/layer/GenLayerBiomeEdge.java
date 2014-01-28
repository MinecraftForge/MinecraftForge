package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiomeEdge extends GenLayer
{
    private static final String __OBFID = "CL_00000554";

    public GenLayerBiomeEdge(long p_i45475_1_, GenLayer p_i45475_3_)
    {
        super(p_i45475_1_);
        this.parent = p_i45475_3_;
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] aint = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1)
        {
            for (int j1 = 0; j1 < par3; ++j1)
            {
                this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                int k1 = aint[j1 + 1 + (i1 + 1) * (par3 + 2)];

                if (!this.func_151636_a(aint, aint1, j1, i1, par3, k1, BiomeGenBase.extremeHills.biomeID, BiomeGenBase.extremeHillsEdge.biomeID) && !this.func_151635_b(aint, aint1, j1, i1, par3, k1, BiomeGenBase.field_150607_aa.biomeID, BiomeGenBase.field_150589_Z.biomeID) && !this.func_151635_b(aint, aint1, j1, i1, par3, k1, BiomeGenBase.field_150608_ab.biomeID, BiomeGenBase.field_150589_Z.biomeID) && !this.func_151635_b(aint, aint1, j1, i1, par3, k1, BiomeGenBase.field_150578_U.biomeID, BiomeGenBase.taiga.biomeID))
                {
                    int l1;
                    int i2;
                    int j2;
                    int k2;

                    if (k1 == BiomeGenBase.desert.biomeID)
                    {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)];

                        if (l1 != BiomeGenBase.icePlains.biomeID && i2 != BiomeGenBase.icePlains.biomeID && j2 != BiomeGenBase.icePlains.biomeID && k2 != BiomeGenBase.icePlains.biomeID)
                        {
                            aint1[j1 + i1 * par3] = k1;
                        }
                        else
                        {
                            aint1[j1 + i1 * par3] = BiomeGenBase.field_150580_W.biomeID;
                        }
                    }
                    else if (k1 == BiomeGenBase.swampland.biomeID)
                    {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)];

                        if (l1 != BiomeGenBase.desert.biomeID && i2 != BiomeGenBase.desert.biomeID && j2 != BiomeGenBase.desert.biomeID && k2 != BiomeGenBase.desert.biomeID && l1 != BiomeGenBase.field_150584_S.biomeID && i2 != BiomeGenBase.field_150584_S.biomeID && j2 != BiomeGenBase.field_150584_S.biomeID && k2 != BiomeGenBase.field_150584_S.biomeID && l1 != BiomeGenBase.icePlains.biomeID && i2 != BiomeGenBase.icePlains.biomeID && j2 != BiomeGenBase.icePlains.biomeID && k2 != BiomeGenBase.icePlains.biomeID)
                        {
                            if (l1 != BiomeGenBase.jungle.biomeID && k2 != BiomeGenBase.jungle.biomeID && i2 != BiomeGenBase.jungle.biomeID && j2 != BiomeGenBase.jungle.biomeID)
                            {
                                aint1[j1 + i1 * par3] = k1;
                            }
                            else
                            {
                                aint1[j1 + i1 * par3] = BiomeGenBase.field_150574_L.biomeID;
                            }
                        }
                        else
                        {
                            aint1[j1 + i1 * par3] = BiomeGenBase.plains.biomeID;
                        }
                    }
                    else
                    {
                        aint1[j1 + i1 * par3] = k1;
                    }
                }
            }
        }

        return aint1;
    }

    private boolean func_151636_a(int[] p_151636_1_, int[] p_151636_2_, int p_151636_3_, int p_151636_4_, int p_151636_5_, int p_151636_6_, int p_151636_7_, int p_151636_8_)
    {
        if (!func_151616_a(p_151636_6_, p_151636_7_))
        {
            return false;
        }
        else
        {
            int k1 = p_151636_1_[p_151636_3_ + 1 + (p_151636_4_ + 1 - 1) * (p_151636_5_ + 2)];
            int l1 = p_151636_1_[p_151636_3_ + 1 + 1 + (p_151636_4_ + 1) * (p_151636_5_ + 2)];
            int i2 = p_151636_1_[p_151636_3_ + 1 - 1 + (p_151636_4_ + 1) * (p_151636_5_ + 2)];
            int j2 = p_151636_1_[p_151636_3_ + 1 + (p_151636_4_ + 1 + 1) * (p_151636_5_ + 2)];

            if (this.func_151634_b(k1, p_151636_7_) && this.func_151634_b(l1, p_151636_7_) && this.func_151634_b(i2, p_151636_7_) && this.func_151634_b(j2, p_151636_7_))
            {
                p_151636_2_[p_151636_3_ + p_151636_4_ * p_151636_5_] = p_151636_6_;
            }
            else
            {
                p_151636_2_[p_151636_3_ + p_151636_4_ * p_151636_5_] = p_151636_8_;
            }

            return true;
        }
    }

    private boolean func_151635_b(int[] p_151635_1_, int[] p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_)
    {
        if (p_151635_6_ != p_151635_7_)
        {
            return false;
        }
        else
        {
            int k1 = p_151635_1_[p_151635_3_ + 1 + (p_151635_4_ + 1 - 1) * (p_151635_5_ + 2)];
            int l1 = p_151635_1_[p_151635_3_ + 1 + 1 + (p_151635_4_ + 1) * (p_151635_5_ + 2)];
            int i2 = p_151635_1_[p_151635_3_ + 1 - 1 + (p_151635_4_ + 1) * (p_151635_5_ + 2)];
            int j2 = p_151635_1_[p_151635_3_ + 1 + (p_151635_4_ + 1 + 1) * (p_151635_5_ + 2)];

            if (func_151616_a(k1, p_151635_7_) && func_151616_a(l1, p_151635_7_) && func_151616_a(i2, p_151635_7_) && func_151616_a(j2, p_151635_7_))
            {
                p_151635_2_[p_151635_3_ + p_151635_4_ * p_151635_5_] = p_151635_6_;
            }
            else
            {
                p_151635_2_[p_151635_3_ + p_151635_4_ * p_151635_5_] = p_151635_8_;
            }

            return true;
        }
    }

    private boolean func_151634_b(int p_151634_1_, int p_151634_2_)
    {
        if (func_151616_a(p_151634_1_, p_151634_2_))
        {
            return true;
        }
        else if (BiomeGenBase.func_150568_d(p_151634_1_) != null && BiomeGenBase.func_150568_d(p_151634_2_) != null)
        {
            BiomeGenBase.TempCategory tempcategory = BiomeGenBase.func_150568_d(p_151634_1_).func_150561_m();
            BiomeGenBase.TempCategory tempcategory1 = BiomeGenBase.func_150568_d(p_151634_2_).func_150561_m();
            return tempcategory == tempcategory1 || tempcategory == BiomeGenBase.TempCategory.MEDIUM || tempcategory1 == BiomeGenBase.TempCategory.MEDIUM;
        }
        else
        {
            return false;
        }
    }
}