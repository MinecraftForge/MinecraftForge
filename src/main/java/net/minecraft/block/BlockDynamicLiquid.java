package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid
{
    int field_149815_a;
    boolean[] field_149814_b = new boolean[4];
    int[] field_149816_M = new int[4];
    private static final String __OBFID = "CL_00000234";

    protected BlockDynamicLiquid(Material p_i45403_1_)
    {
        super(p_i45403_1_);
    }

    private void func_149811_n(World p_149811_1_, int p_149811_2_, int p_149811_3_, int p_149811_4_)
    {
        int l = p_149811_1_.getBlockMetadata(p_149811_2_, p_149811_3_, p_149811_4_);
        p_149811_1_.func_147465_d(p_149811_2_, p_149811_3_, p_149811_4_, Block.func_149729_e(Block.func_149682_b(this) + 1), l, 2);
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        int l = this.func_149804_e(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
        byte b0 = 1;

        if (this.field_149764_J == Material.field_151587_i && !p_149674_1_.provider.isHellWorld)
        {
            b0 = 2;
        }

        boolean flag = true;
        int i1 = this.func_149738_a(p_149674_1_);
        int j1;

        if (l > 0)
        {
            byte b1 = -100;
            this.field_149815_a = 0;
            int l1 = this.func_149810_a(p_149674_1_, p_149674_2_ - 1, p_149674_3_, p_149674_4_, b1);
            l1 = this.func_149810_a(p_149674_1_, p_149674_2_ + 1, p_149674_3_, p_149674_4_, l1);
            l1 = this.func_149810_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_ - 1, l1);
            l1 = this.func_149810_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_ + 1, l1);
            j1 = l1 + b0;

            if (j1 >= 8 || l1 < 0)
            {
                j1 = -1;
            }

            if (this.func_149804_e(p_149674_1_, p_149674_2_, p_149674_3_ + 1, p_149674_4_) >= 0)
            {
                int k1 = this.func_149804_e(p_149674_1_, p_149674_2_, p_149674_3_ + 1, p_149674_4_);

                if (k1 >= 8)
                {
                    j1 = k1;
                }
                else
                {
                    j1 = k1 + 8;
                }
            }

            if (this.field_149815_a >= 2 && this.field_149764_J == Material.field_151586_h)
            {
                if (p_149674_1_.func_147439_a(p_149674_2_, p_149674_3_ - 1, p_149674_4_).func_149688_o().isSolid())
                {
                    j1 = 0;
                }
                else if (p_149674_1_.func_147439_a(p_149674_2_, p_149674_3_ - 1, p_149674_4_).func_149688_o() == this.field_149764_J && p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_ - 1, p_149674_4_) == 0)
                {
                    j1 = 0;
                }
            }

            if (this.field_149764_J == Material.field_151587_i && l < 8 && j1 < 8 && j1 > l && p_149674_5_.nextInt(4) != 0)
            {
                i1 *= 4;
            }

            if (j1 == l)
            {
                if (flag)
                {
                    this.func_149811_n(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
                }
            }
            else
            {
                l = j1;

                if (j1 < 0)
                {
                    p_149674_1_.func_147468_f(p_149674_2_, p_149674_3_, p_149674_4_);
                }
                else
                {
                    p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, j1, 2);
                    p_149674_1_.func_147464_a(p_149674_2_, p_149674_3_, p_149674_4_, this, i1);
                    p_149674_1_.func_147459_d(p_149674_2_, p_149674_3_, p_149674_4_, this);
                }
            }
        }
        else
        {
            this.func_149811_n(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
        }

        if (this.func_149809_q(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_))
        {
            if (this.field_149764_J == Material.field_151587_i && p_149674_1_.func_147439_a(p_149674_2_, p_149674_3_ - 1, p_149674_4_).func_149688_o() == Material.field_151586_h)
            {
                p_149674_1_.func_147449_b(p_149674_2_, p_149674_3_ - 1, p_149674_4_, Blocks.stone);
                this.func_149799_m(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_);
                return;
            }

            if (l >= 8)
            {
                this.func_149813_h(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_, l);
            }
            else
            {
                this.func_149813_h(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_, l + 8);
            }
        }
        else if (l >= 0 && (l == 0 || this.func_149807_p(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_)))
        {
            boolean[] aboolean = this.func_149808_o(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
            j1 = l + b0;

            if (l >= 8)
            {
                j1 = 1;
            }

            if (j1 >= 8)
            {
                return;
            }

            if (aboolean[0])
            {
                this.func_149813_h(p_149674_1_, p_149674_2_ - 1, p_149674_3_, p_149674_4_, j1);
            }

            if (aboolean[1])
            {
                this.func_149813_h(p_149674_1_, p_149674_2_ + 1, p_149674_3_, p_149674_4_, j1);
            }

            if (aboolean[2])
            {
                this.func_149813_h(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_ - 1, j1);
            }

            if (aboolean[3])
            {
                this.func_149813_h(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_ + 1, j1);
            }
        }
    }

    private void func_149813_h(World p_149813_1_, int p_149813_2_, int p_149813_3_, int p_149813_4_, int p_149813_5_)
    {
        if (this.func_149809_q(p_149813_1_, p_149813_2_, p_149813_3_, p_149813_4_))
        {
            Block block = p_149813_1_.func_147439_a(p_149813_2_, p_149813_3_, p_149813_4_);

            if (this.field_149764_J == Material.field_151587_i)
            {
                this.func_149799_m(p_149813_1_, p_149813_2_, p_149813_3_, p_149813_4_);
            }
            else
            {
                block.func_149697_b(p_149813_1_, p_149813_2_, p_149813_3_, p_149813_4_, p_149813_1_.getBlockMetadata(p_149813_2_, p_149813_3_, p_149813_4_), 0);
            }

            p_149813_1_.func_147465_d(p_149813_2_, p_149813_3_, p_149813_4_, this, p_149813_5_, 3);
        }
    }

    private int func_149812_c(World p_149812_1_, int p_149812_2_, int p_149812_3_, int p_149812_4_, int p_149812_5_, int p_149812_6_)
    {
        int j1 = 1000;

        for (int k1 = 0; k1 < 4; ++k1)
        {
            if ((k1 != 0 || p_149812_6_ != 1) && (k1 != 1 || p_149812_6_ != 0) && (k1 != 2 || p_149812_6_ != 3) && (k1 != 3 || p_149812_6_ != 2))
            {
                int l1 = p_149812_2_;
                int i2 = p_149812_4_;

                if (k1 == 0)
                {
                    l1 = p_149812_2_ - 1;
                }

                if (k1 == 1)
                {
                    ++l1;
                }

                if (k1 == 2)
                {
                    i2 = p_149812_4_ - 1;
                }

                if (k1 == 3)
                {
                    ++i2;
                }

                if (!this.func_149807_p(p_149812_1_, l1, p_149812_3_, i2) && (p_149812_1_.func_147439_a(l1, p_149812_3_, i2).func_149688_o() != this.field_149764_J || p_149812_1_.getBlockMetadata(l1, p_149812_3_, i2) != 0))
                {
                    if (!this.func_149807_p(p_149812_1_, l1, p_149812_3_ - 1, i2))
                    {
                        return p_149812_5_;
                    }

                    if (p_149812_5_ < 4)
                    {
                        int j2 = this.func_149812_c(p_149812_1_, l1, p_149812_3_, i2, p_149812_5_ + 1, k1);

                        if (j2 < j1)
                        {
                            j1 = j2;
                        }
                    }
                }
            }
        }

        return j1;
    }

    private boolean[] func_149808_o(World p_149808_1_, int p_149808_2_, int p_149808_3_, int p_149808_4_)
    {
        int l;
        int i1;

        for (l = 0; l < 4; ++l)
        {
            this.field_149816_M[l] = 1000;
            i1 = p_149808_2_;
            int j1 = p_149808_4_;

            if (l == 0)
            {
                i1 = p_149808_2_ - 1;
            }

            if (l == 1)
            {
                ++i1;
            }

            if (l == 2)
            {
                j1 = p_149808_4_ - 1;
            }

            if (l == 3)
            {
                ++j1;
            }

            if (!this.func_149807_p(p_149808_1_, i1, p_149808_3_, j1) && (p_149808_1_.func_147439_a(i1, p_149808_3_, j1).func_149688_o() != this.field_149764_J || p_149808_1_.getBlockMetadata(i1, p_149808_3_, j1) != 0))
            {
                if (this.func_149807_p(p_149808_1_, i1, p_149808_3_ - 1, j1))
                {
                    this.field_149816_M[l] = this.func_149812_c(p_149808_1_, i1, p_149808_3_, j1, 1, l);
                }
                else
                {
                    this.field_149816_M[l] = 0;
                }
            }
        }

        l = this.field_149816_M[0];

        for (i1 = 1; i1 < 4; ++i1)
        {
            if (this.field_149816_M[i1] < l)
            {
                l = this.field_149816_M[i1];
            }
        }

        for (i1 = 0; i1 < 4; ++i1)
        {
            this.field_149814_b[i1] = this.field_149816_M[i1] == l;
        }

        return this.field_149814_b;
    }

    private boolean func_149807_p(World p_149807_1_, int p_149807_2_, int p_149807_3_, int p_149807_4_)
    {
        Block block = p_149807_1_.func_147439_a(p_149807_2_, p_149807_3_, p_149807_4_);
        return block != Blocks.wooden_door && block != Blocks.iron_door && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds ? (block.field_149764_J == Material.field_151567_E ? true : block.field_149764_J.blocksMovement()) : true;
    }

    protected int func_149810_a(World p_149810_1_, int p_149810_2_, int p_149810_3_, int p_149810_4_, int p_149810_5_)
    {
        int i1 = this.func_149804_e(p_149810_1_, p_149810_2_, p_149810_3_, p_149810_4_);

        if (i1 < 0)
        {
            return p_149810_5_;
        }
        else
        {
            if (i1 == 0)
            {
                ++this.field_149815_a;
            }

            if (i1 >= 8)
            {
                i1 = 0;
            }

            return p_149810_5_ >= 0 && i1 >= p_149810_5_ ? p_149810_5_ : i1;
        }
    }

    private boolean func_149809_q(World p_149809_1_, int p_149809_2_, int p_149809_3_, int p_149809_4_)
    {
        Material material = p_149809_1_.func_147439_a(p_149809_2_, p_149809_3_, p_149809_4_).func_149688_o();
        return material == this.field_149764_J ? false : (material == Material.field_151587_i ? false : !this.func_149807_p(p_149809_1_, p_149809_2_, p_149809_3_, p_149809_4_));
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.func_149726_b(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);

        if (p_149726_1_.func_147439_a(p_149726_2_, p_149726_3_, p_149726_4_) == this)
        {
            p_149726_1_.func_147464_a(p_149726_2_, p_149726_3_, p_149726_4_, this, this.func_149738_a(p_149726_1_));
        }
    }

    public boolean func_149698_L()
    {
        return true;
    }
}