package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase
{
    @SideOnly(Side.CLIENT)
    protected IIcon field_150059_b;
    private static final String __OBFID = "CL_00000288";

    protected BlockRailPowered()
    {
        super(true);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return (p_149691_2_ & 8) == 0 ? this.field_149761_L : this.field_150059_b;
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        super.func_149651_a(p_149651_1_);
        this.field_150059_b = p_149651_1_.registerIcon(this.func_149641_N() + "_powered");
    }

    protected boolean func_150058_a(World p_150058_1_, int p_150058_2_, int p_150058_3_, int p_150058_4_, int p_150058_5_, boolean p_150058_6_, int p_150058_7_)
    {
        if (p_150058_7_ >= 8)
        {
            return false;
        }
        else
        {
            int j1 = p_150058_5_ & 7;
            boolean flag1 = true;

            switch (j1)
            {
                case 0:
                    if (p_150058_6_)
                    {
                        ++p_150058_4_;
                    }
                    else
                    {
                        --p_150058_4_;
                    }

                    break;
                case 1:
                    if (p_150058_6_)
                    {
                        --p_150058_2_;
                    }
                    else
                    {
                        ++p_150058_2_;
                    }

                    break;
                case 2:
                    if (p_150058_6_)
                    {
                        --p_150058_2_;
                    }
                    else
                    {
                        ++p_150058_2_;
                        ++p_150058_3_;
                        flag1 = false;
                    }

                    j1 = 1;
                    break;
                case 3:
                    if (p_150058_6_)
                    {
                        --p_150058_2_;
                        ++p_150058_3_;
                        flag1 = false;
                    }
                    else
                    {
                        ++p_150058_2_;
                    }

                    j1 = 1;
                    break;
                case 4:
                    if (p_150058_6_)
                    {
                        ++p_150058_4_;
                    }
                    else
                    {
                        --p_150058_4_;
                        ++p_150058_3_;
                        flag1 = false;
                    }

                    j1 = 0;
                    break;
                case 5:
                    if (p_150058_6_)
                    {
                        ++p_150058_4_;
                        ++p_150058_3_;
                        flag1 = false;
                    }
                    else
                    {
                        --p_150058_4_;
                    }

                    j1 = 0;
            }

            return this.func_150057_a(p_150058_1_, p_150058_2_, p_150058_3_, p_150058_4_, p_150058_6_, p_150058_7_, j1) ? true : flag1 && this.func_150057_a(p_150058_1_, p_150058_2_, p_150058_3_ - 1, p_150058_4_, p_150058_6_, p_150058_7_, j1);
        }
    }

    protected boolean func_150057_a(World p_150057_1_, int p_150057_2_, int p_150057_3_, int p_150057_4_, boolean p_150057_5_, int p_150057_6_, int p_150057_7_)
    {
        Block block = p_150057_1_.func_147439_a(p_150057_2_, p_150057_3_, p_150057_4_);

        if (block == this)
        {
            int j1 = p_150057_1_.getBlockMetadata(p_150057_2_, p_150057_3_, p_150057_4_);
            int k1 = j1 & 7;

            if (p_150057_7_ == 1 && (k1 == 0 || k1 == 4 || k1 == 5))
            {
                return false;
            }

            if (p_150057_7_ == 0 && (k1 == 1 || k1 == 2 || k1 == 3))
            {
                return false;
            }

            if ((j1 & 8) != 0)
            {
                if (p_150057_1_.isBlockIndirectlyGettingPowered(p_150057_2_, p_150057_3_, p_150057_4_))
                {
                    return true;
                }

                return this.func_150058_a(p_150057_1_, p_150057_2_, p_150057_3_, p_150057_4_, j1, p_150057_5_, p_150057_6_ + 1);
            }
        }

        return false;
    }

    protected void func_150048_a(World p_150048_1_, int p_150048_2_, int p_150048_3_, int p_150048_4_, int p_150048_5_, int p_150048_6_, Block p_150048_7_)
    {
        boolean flag = p_150048_1_.isBlockIndirectlyGettingPowered(p_150048_2_, p_150048_3_, p_150048_4_);
        flag = flag || this.func_150058_a(p_150048_1_, p_150048_2_, p_150048_3_, p_150048_4_, p_150048_5_, true, 0) || this.func_150058_a(p_150048_1_, p_150048_2_, p_150048_3_, p_150048_4_, p_150048_5_, false, 0);
        boolean flag1 = false;

        if (flag && (p_150048_5_ & 8) == 0)
        {
            p_150048_1_.setBlockMetadataWithNotify(p_150048_2_, p_150048_3_, p_150048_4_, p_150048_6_ | 8, 3);
            flag1 = true;
        }
        else if (!flag && (p_150048_5_ & 8) != 0)
        {
            p_150048_1_.setBlockMetadataWithNotify(p_150048_2_, p_150048_3_, p_150048_4_, p_150048_6_, 3);
            flag1 = true;
        }

        if (flag1)
        {
            p_150048_1_.func_147459_d(p_150048_2_, p_150048_3_ - 1, p_150048_4_, this);

            if (p_150048_6_ == 2 || p_150048_6_ == 3 || p_150048_6_ == 4 || p_150048_6_ == 5)
            {
                p_150048_1_.func_147459_d(p_150048_2_, p_150048_3_ + 1, p_150048_4_, this);
            }
        }
    }
}