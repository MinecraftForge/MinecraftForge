package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockTripWireHook extends Block
{
    private static final String __OBFID = "CL_00000329";

    public BlockTripWireHook()
    {
        super(Material.field_151594_q);
        this.func_149647_a(CreativeTabs.tabRedstone);
        this.func_149675_a(true);
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public int func_149645_b()
    {
        return 29;
    }

    public int func_149738_a(World p_149738_1_)
    {
        return 10;
    }

    public boolean func_149707_d(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(p_149707_5_);
        return (dir == NORTH && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_, p_149707_4_ + 1, NORTH)) ||
               (dir == SOUTH && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_, p_149707_4_ - 1, SOUTH)) ||
               (dir == WEST  && p_149707_1_.isSideSolid(p_149707_2_ + 1, p_149707_3_, p_149707_4_, WEST )) ||
               (dir == EAST  && p_149707_1_.isSideSolid(p_149707_2_ - 1, p_149707_3_, p_149707_4_, EAST ));
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return p_149742_1_.isSideSolid(p_149742_2_ - 1, p_149742_3_, p_149742_4_, EAST ) ||
               p_149742_1_.isSideSolid(p_149742_2_ + 1, p_149742_3_, p_149742_4_, WEST ) ||
               p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_, p_149742_4_ - 1, SOUTH) ||
               p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_, p_149742_4_ + 1, NORTH);
    }

    public int func_149660_a(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        byte b0 = 0;

        if (p_149660_5_ == 2 && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_, p_149660_4_ + 1, NORTH, true))
        {
            b0 = 2;
        }

        if (p_149660_5_ == 3 && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_, p_149660_4_ - 1, SOUTH, true))
        {
            b0 = 0;
        }

        if (p_149660_5_ == 4 && p_149660_1_.isSideSolid(p_149660_2_ + 1, p_149660_3_, p_149660_4_, WEST, true))
        {
            b0 = 1;
        }

        if (p_149660_5_ == 5 && p_149660_1_.isSideSolid(p_149660_2_ - 1, p_149660_3_, p_149660_4_, EAST, true))
        {
            b0 = 3;
        }

        return b0;
    }

    public void func_149714_e(World p_149714_1_, int p_149714_2_, int p_149714_3_, int p_149714_4_, int p_149714_5_)
    {
        this.func_150136_a(p_149714_1_, p_149714_2_, p_149714_3_, p_149714_4_, false, p_149714_5_, false, -1, 0);
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (p_149695_5_ != this)
        {
            if (this.func_150137_e(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_))
            {
                int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_);
                int i1 = l & 3;
                boolean flag = false;

                if (!p_149695_1_.isSideSolid(p_149695_2_ - 1, p_149695_3_, p_149695_4_, EAST) && i1 == 3)
                {
                    flag = true;
                }

                if (!p_149695_1_.isSideSolid(p_149695_2_ + 1, p_149695_3_, p_149695_4_, WEST) && i1 == 1)
                {
                    flag = true;
                }

                if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_, p_149695_4_ - 1, SOUTH) && i1 == 0)
                {
                    flag = true;
                }

                if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_, p_149695_4_ + 1, NORTH) && i1 == 2)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, l, 0);
                    p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
                }
            }
        }
    }

    public void func_150136_a(World p_150136_1_, int p_150136_2_, int p_150136_3_, int p_150136_4_, boolean p_150136_5_, int p_150136_6_, boolean p_150136_7_, int p_150136_8_, int p_150136_9_)
    {
        int k1 = p_150136_6_ & 3;
        boolean flag2 = (p_150136_6_ & 4) == 4;
        boolean flag3 = (p_150136_6_ & 8) == 8;
        boolean flag4 = !p_150136_5_;
        boolean flag5 = false;
        boolean flag6 = !p_150136_1_.isSideSolid(p_150136_2_, p_150136_3_ - 1, p_150136_4_, UP);
        int l1 = Direction.offsetX[k1];
        int i2 = Direction.offsetZ[k1];
        int j2 = 0;
        int[] aint = new int[42];
        int l2;
        int k2;
        int i3;
        int j3;

        for (k2 = 1; k2 < 42; ++k2)
        {
            l2 = p_150136_2_ + l1 * k2;
            i3 = p_150136_4_ + i2 * k2;
            Block block = p_150136_1_.func_147439_a(l2, p_150136_3_, i3);

            if (block == Blocks.tripwire_hook)
            {
                j3 = p_150136_1_.getBlockMetadata(l2, p_150136_3_, i3);

                if ((j3 & 3) == Direction.rotateOpposite[k1])
                {
                    j2 = k2;
                }

                break;
            }

            if (block != Blocks.tripwire && k2 != p_150136_8_)
            {
                aint[k2] = -1;
                flag4 = false;
            }
            else
            {
                j3 = k2 == p_150136_8_ ? p_150136_9_ : p_150136_1_.getBlockMetadata(l2, p_150136_3_, i3);
                boolean flag7 = (j3 & 8) != 8;
                boolean flag8 = (j3 & 1) == 1;
                boolean flag9 = (j3 & 2) == 2;
                flag4 &= flag9 == flag6;
                flag5 |= flag7 && flag8;
                aint[k2] = j3;

                if (k2 == p_150136_8_)
                {
                    p_150136_1_.func_147464_a(p_150136_2_, p_150136_3_, p_150136_4_, this, this.func_149738_a(p_150136_1_));
                    flag4 &= flag7;
                }
            }
        }

        flag4 &= j2 > 1;
        flag5 &= flag4;
        k2 = (flag4 ? 4 : 0) | (flag5 ? 8 : 0);
        p_150136_6_ = k1 | k2;
        int k3;

        if (j2 > 0)
        {
            l2 = p_150136_2_ + l1 * j2;
            i3 = p_150136_4_ + i2 * j2;
            k3 = Direction.rotateOpposite[k1];
            p_150136_1_.setBlockMetadataWithNotify(l2, p_150136_3_, i3, k3 | k2, 3);
            this.func_150134_a(p_150136_1_, l2, p_150136_3_, i3, k3);
            this.func_150135_a(p_150136_1_, l2, p_150136_3_, i3, flag4, flag5, flag2, flag3);
        }

        this.func_150135_a(p_150136_1_, p_150136_2_, p_150136_3_, p_150136_4_, flag4, flag5, flag2, flag3);

        if (!p_150136_5_)
        {
            p_150136_1_.setBlockMetadataWithNotify(p_150136_2_, p_150136_3_, p_150136_4_, p_150136_6_, 3);

            if (p_150136_7_)
            {
                this.func_150134_a(p_150136_1_, p_150136_2_, p_150136_3_, p_150136_4_, k1);
            }
        }

        if (flag2 != flag4)
        {
            for (l2 = 1; l2 < j2; ++l2)
            {
                i3 = p_150136_2_ + l1 * l2;
                k3 = p_150136_4_ + i2 * l2;
                j3 = aint[l2];

                if (j3 >= 0)
                {
                    if (flag4)
                    {
                        j3 |= 4;
                    }
                    else
                    {
                        j3 &= -5;
                    }

                    p_150136_1_.setBlockMetadataWithNotify(i3, p_150136_3_, k3, j3, 3);
                }
            }
        }
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        this.func_150136_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, false, p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_), true, -1, 0);
    }

    private void func_150135_a(World p_150135_1_, int p_150135_2_, int p_150135_3_, int p_150135_4_, boolean p_150135_5_, boolean p_150135_6_, boolean p_150135_7_, boolean p_150135_8_)
    {
        if (p_150135_6_ && !p_150135_8_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.click", 0.4F, 0.6F);
        }
        else if (!p_150135_6_ && p_150135_8_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.click", 0.4F, 0.5F);
        }
        else if (p_150135_5_ && !p_150135_7_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.click", 0.4F, 0.7F);
        }
        else if (!p_150135_5_ && p_150135_7_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.bowhit", 0.4F, 1.2F / (p_150135_1_.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void func_150134_a(World p_150134_1_, int p_150134_2_, int p_150134_3_, int p_150134_4_, int p_150134_5_)
    {
        p_150134_1_.func_147459_d(p_150134_2_, p_150134_3_, p_150134_4_, this);

        if (p_150134_5_ == 3)
        {
            p_150134_1_.func_147459_d(p_150134_2_ - 1, p_150134_3_, p_150134_4_, this);
        }
        else if (p_150134_5_ == 1)
        {
            p_150134_1_.func_147459_d(p_150134_2_ + 1, p_150134_3_, p_150134_4_, this);
        }
        else if (p_150134_5_ == 0)
        {
            p_150134_1_.func_147459_d(p_150134_2_, p_150134_3_, p_150134_4_ - 1, this);
        }
        else if (p_150134_5_ == 2)
        {
            p_150134_1_.func_147459_d(p_150134_2_, p_150134_3_, p_150134_4_ + 1, this);
        }
    }

    private boolean func_150137_e(World p_150137_1_, int p_150137_2_, int p_150137_3_, int p_150137_4_)
    {
        if (!this.func_149742_c(p_150137_1_, p_150137_2_, p_150137_3_, p_150137_4_))
        {
            this.func_149697_b(p_150137_1_, p_150137_2_, p_150137_3_, p_150137_4_, p_150137_1_.getBlockMetadata(p_150137_2_, p_150137_3_, p_150137_4_), 0);
            p_150137_1_.func_147468_f(p_150137_2_, p_150137_3_, p_150137_4_);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_) & 3;
        float f = 0.1875F;

        if (l == 3)
        {
            this.func_149676_a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        }
        else if (l == 1)
        {
            this.func_149676_a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        }
        else if (l == 0)
        {
            this.func_149676_a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        }
        else if (l == 2)
        {
            this.func_149676_a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        boolean flag = (p_149749_6_ & 4) == 4;
        boolean flag1 = (p_149749_6_ & 8) == 8;

        if (flag || flag1)
        {
            this.func_150136_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, true, p_149749_6_, false, -1, 0);
        }

        if (flag1)
        {
            p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_, p_149749_4_, this);
            int i1 = p_149749_6_ & 3;

            if (i1 == 3)
            {
                p_149749_1_.func_147459_d(p_149749_2_ - 1, p_149749_3_, p_149749_4_, this);
            }
            else if (i1 == 1)
            {
                p_149749_1_.func_147459_d(p_149749_2_ + 1, p_149749_3_, p_149749_4_, this);
            }
            else if (i1 == 0)
            {
                p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_, p_149749_4_ - 1, this);
            }
            else if (i1 == 2)
            {
                p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_, p_149749_4_ + 1, this);
            }
        }

        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return (p_149709_1_.getBlockMetadata(p_149709_2_, p_149709_3_, p_149709_4_) & 8) == 8 ? 15 : 0;
    }

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        int i1 = p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_);

        if ((i1 & 8) != 8)
        {
            return 0;
        }
        else
        {
            int j1 = i1 & 3;
            return j1 == 2 && p_149748_5_ == 2 ? 15 : (j1 == 0 && p_149748_5_ == 3 ? 15 : (j1 == 1 && p_149748_5_ == 4 ? 15 : (j1 == 3 && p_149748_5_ == 5 ? 15 : 0)));
        }
    }

    public boolean func_149744_f()
    {
        return true;
    }
}