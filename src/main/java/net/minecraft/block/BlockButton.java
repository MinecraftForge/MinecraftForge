package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public abstract class BlockButton extends Block
{
    private final boolean field_150047_a;
    private static final String __OBFID = "CL_00000209";

    protected BlockButton(boolean p_i45396_1_)
    {
        super(Material.field_151594_q);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.tabRedstone);
        this.field_150047_a = p_i45396_1_;
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public int func_149738_a(World p_149738_1_)
    {
        return this.field_150047_a ? 30 : 20;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public boolean func_149707_d(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(p_149707_5_);
        return (dir == NORTH && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_, p_149707_4_ + 1, NORTH)) ||
               (dir == SOUTH && p_149707_1_.isSideSolid(p_149707_2_, p_149707_3_, p_149707_4_ - 1, SOUTH)) ||
               (dir == WEST  && p_149707_1_.isSideSolid(p_149707_2_ + 1, p_149707_3_, p_149707_4_, WEST)) ||
               (dir == EAST  && p_149707_1_.isSideSolid(p_149707_2_ - 1, p_149707_3_, p_149707_4_, EAST));
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return (p_149742_1_.isSideSolid(p_149742_2_ - 1, p_149742_3_, p_149742_4_, EAST)) ||
               (p_149742_1_.isSideSolid(p_149742_2_ + 1, p_149742_3_, p_149742_4_, WEST)) ||
               (p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_, p_149742_4_ - 1, SOUTH)) ||
               (p_149742_1_.isSideSolid(p_149742_2_, p_149742_3_, p_149742_4_ + 1, NORTH));
    }

    public int func_149660_a(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        int j1 = p_149660_1_.getBlockMetadata(p_149660_2_, p_149660_3_, p_149660_4_);
        int k1 = j1 & 8;
        j1 &= 7;

        ForgeDirection dir = ForgeDirection.getOrientation(p_149660_5_);

        if (dir == NORTH && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_, p_149660_4_ + 1, NORTH))
        {
            j1 = 4;
        }
        else if (dir == SOUTH && p_149660_1_.isSideSolid(p_149660_2_, p_149660_3_, p_149660_4_ - 1, SOUTH))
        {
            j1 = 3;
        }
        else if (dir == WEST && p_149660_1_.isSideSolid(p_149660_2_ + 1, p_149660_3_, p_149660_4_, WEST))
        {
            j1 = 2;
        }
        else if (dir == EAST && p_149660_1_.isSideSolid(p_149660_2_ - 1, p_149660_3_, p_149660_4_, EAST))
        {
            j1 = 1;
        }
        else
        {
            j1 = this.func_150045_e(p_149660_1_, p_149660_2_, p_149660_3_, p_149660_4_);
        }

        return j1 + k1;
    }

    private int func_150045_e(World p_150045_1_, int p_150045_2_, int p_150045_3_, int p_150045_4_)
    {
        if (p_150045_1_.isSideSolid(p_150045_2_ - 1, p_150045_3_, p_150045_4_, EAST)) return 1;
        if (p_150045_1_.isSideSolid(p_150045_2_ + 1, p_150045_3_, p_150045_4_, WEST)) return 2;
        if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_, p_150045_4_ - 1, SOUTH)) return 3;
        if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_, p_150045_4_ + 1, NORTH)) return 4;
        return 1;
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (this.func_150044_m(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_))
        {
            int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_) & 7;
            boolean flag = false;

            if (!p_149695_1_.isSideSolid(p_149695_2_ - 1, p_149695_3_, p_149695_4_, EAST) && l == 1)
            {
                flag = true;
            }

            if (!p_149695_1_.isSideSolid(p_149695_2_ + 1, p_149695_3_, p_149695_4_, WEST) && l == 2)
            {
                flag = true;
            }

            if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_, p_149695_4_ - 1, SOUTH) && l == 3)
            {
                flag = true;
            }

            if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_, p_149695_4_ + 1, NORTH) && l == 4)
            {
                flag = true;
            }

            if (flag)
            {
                this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
                p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
            }
        }
    }

    private boolean func_150044_m(World p_150044_1_, int p_150044_2_, int p_150044_3_, int p_150044_4_)
    {
        if (!this.func_149742_c(p_150044_1_, p_150044_2_, p_150044_3_, p_150044_4_))
        {
            this.func_149697_b(p_150044_1_, p_150044_2_, p_150044_3_, p_150044_4_, p_150044_1_.getBlockMetadata(p_150044_2_, p_150044_3_, p_150044_4_), 0);
            p_150044_1_.func_147468_f(p_150044_2_, p_150044_3_, p_150044_4_);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_);
        this.func_150043_b(l);
    }

    private void func_150043_b(int p_150043_1_)
    {
        int j = p_150043_1_ & 7;
        boolean flag = (p_150043_1_ & 8) > 0;
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.1875F;
        float f3 = 0.125F;

        if (flag)
        {
            f3 = 0.0625F;
        }

        if (j == 1)
        {
            this.func_149676_a(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
        }
        else if (j == 2)
        {
            this.func_149676_a(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
        }
        else if (j == 3)
        {
            this.func_149676_a(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
        }
        else if (j == 4)
        {
            this.func_149676_a(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
        }
    }

    public void func_149699_a(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_) {}

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        int i1 = p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_);
        int j1 = i1 & 7;
        int k1 = 8 - (i1 & 8);

        if (k1 == 0)
        {
            return true;
        }
        else
        {
            p_149727_1_.setBlockMetadataWithNotify(p_149727_2_, p_149727_3_, p_149727_4_, j1 + k1, 3);
            p_149727_1_.func_147458_c(p_149727_2_, p_149727_3_, p_149727_4_, p_149727_2_, p_149727_3_, p_149727_4_);
            p_149727_1_.playSoundEffect((double)p_149727_2_ + 0.5D, (double)p_149727_3_ + 0.5D, (double)p_149727_4_ + 0.5D, "random.click", 0.3F, 0.6F);
            this.func_150042_a(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, j1);
            p_149727_1_.func_147464_a(p_149727_2_, p_149727_3_, p_149727_4_, this, this.func_149738_a(p_149727_1_));
            return true;
        }
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        if ((p_149749_6_ & 8) > 0)
        {
            int i1 = p_149749_6_ & 7;
            this.func_150042_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, i1);
        }

        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return (p_149709_1_.getBlockMetadata(p_149709_2_, p_149709_3_, p_149709_4_) & 8) > 0 ? 15 : 0;
    }

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        int i1 = p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_);

        if ((i1 & 8) == 0)
        {
            return 0;
        }
        else
        {
            int j1 = i1 & 7;
            return j1 == 5 && p_149748_5_ == 1 ? 15 : (j1 == 4 && p_149748_5_ == 2 ? 15 : (j1 == 3 && p_149748_5_ == 3 ? 15 : (j1 == 2 && p_149748_5_ == 4 ? 15 : (j1 == 1 && p_149748_5_ == 5 ? 15 : 0))));
        }
    }

    public boolean func_149744_f()
    {
        return true;
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (!p_149674_1_.isRemote)
        {
            int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);

            if ((l & 8) != 0)
            {
                if (this.field_150047_a)
                {
                    this.func_150046_n(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
                }
                else
                {
                    p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, l & 7, 3);
                    int i1 = l & 7;
                    this.func_150042_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, i1);
                    p_149674_1_.playSoundEffect((double)p_149674_2_ + 0.5D, (double)p_149674_3_ + 0.5D, (double)p_149674_4_ + 0.5D, "random.click", 0.3F, 0.5F);
                    p_149674_1_.func_147458_c(p_149674_2_, p_149674_3_, p_149674_4_, p_149674_2_, p_149674_3_, p_149674_4_);
                }
            }
        }
    }

    public void func_149683_g()
    {
        float f = 0.1875F;
        float f1 = 0.125F;
        float f2 = 0.125F;
        this.func_149676_a(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        if (!p_149670_1_.isRemote)
        {
            if (this.field_150047_a)
            {
                if ((p_149670_1_.getBlockMetadata(p_149670_2_, p_149670_3_, p_149670_4_) & 8) == 0)
                {
                    this.func_150046_n(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_);
                }
            }
        }
    }

    private void func_150046_n(World p_150046_1_, int p_150046_2_, int p_150046_3_, int p_150046_4_)
    {
        int l = p_150046_1_.getBlockMetadata(p_150046_2_, p_150046_3_, p_150046_4_);
        int i1 = l & 7;
        boolean flag = (l & 8) != 0;
        this.func_150043_b(l);
        List list = p_150046_1_.getEntitiesWithinAABB(EntityArrow.class, AxisAlignedBB.getAABBPool().getAABB((double)p_150046_2_ + this.field_149759_B, (double)p_150046_3_ + this.field_149760_C, (double)p_150046_4_ + this.field_149754_D, (double)p_150046_2_ + this.field_149755_E, (double)p_150046_3_ + this.field_149756_F, (double)p_150046_4_ + this.field_149757_G));
        boolean flag1 = !list.isEmpty();

        if (flag1 && !flag)
        {
            p_150046_1_.setBlockMetadataWithNotify(p_150046_2_, p_150046_3_, p_150046_4_, i1 | 8, 3);
            this.func_150042_a(p_150046_1_, p_150046_2_, p_150046_3_, p_150046_4_, i1);
            p_150046_1_.func_147458_c(p_150046_2_, p_150046_3_, p_150046_4_, p_150046_2_, p_150046_3_, p_150046_4_);
            p_150046_1_.playSoundEffect((double)p_150046_2_ + 0.5D, (double)p_150046_3_ + 0.5D, (double)p_150046_4_ + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (!flag1 && flag)
        {
            p_150046_1_.setBlockMetadataWithNotify(p_150046_2_, p_150046_3_, p_150046_4_, i1, 3);
            this.func_150042_a(p_150046_1_, p_150046_2_, p_150046_3_, p_150046_4_, i1);
            p_150046_1_.func_147458_c(p_150046_2_, p_150046_3_, p_150046_4_, p_150046_2_, p_150046_3_, p_150046_4_);
            p_150046_1_.playSoundEffect((double)p_150046_2_ + 0.5D, (double)p_150046_3_ + 0.5D, (double)p_150046_4_ + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (flag1)
        {
            p_150046_1_.func_147464_a(p_150046_2_, p_150046_3_, p_150046_4_, this, this.func_149738_a(p_150046_1_));
        }
    }

    private void func_150042_a(World p_150042_1_, int p_150042_2_, int p_150042_3_, int p_150042_4_, int p_150042_5_)
    {
        p_150042_1_.func_147459_d(p_150042_2_, p_150042_3_, p_150042_4_, this);

        if (p_150042_5_ == 1)
        {
            p_150042_1_.func_147459_d(p_150042_2_ - 1, p_150042_3_, p_150042_4_, this);
        }
        else if (p_150042_5_ == 2)
        {
            p_150042_1_.func_147459_d(p_150042_2_ + 1, p_150042_3_, p_150042_4_, this);
        }
        else if (p_150042_5_ == 3)
        {
            p_150042_1_.func_147459_d(p_150042_2_, p_150042_3_, p_150042_4_ - 1, this);
        }
        else if (p_150042_5_ == 4)
        {
            p_150042_1_.func_147459_d(p_150042_2_, p_150042_3_, p_150042_4_ + 1, this);
        }
        else
        {
            p_150042_1_.func_147459_d(p_150042_2_, p_150042_3_ - 1, p_150042_4_, this);
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_) {}
}