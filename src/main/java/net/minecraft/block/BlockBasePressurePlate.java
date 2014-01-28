package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block
{
    private String field_150067_a;
    private static final String __OBFID = "CL_00000194";

    protected BlockBasePressurePlate(String p_i45387_1_, Material p_i45387_2_)
    {
        super(p_i45387_2_);
        this.field_150067_a = p_i45387_1_;
        this.func_149647_a(CreativeTabs.tabRedstone);
        this.func_149675_a(true);
        this.func_150063_b(this.func_150066_d(15));
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        this.func_150063_b(p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_));
    }

    protected void func_150063_b(int p_150063_1_)
    {
        boolean flag = this.func_150060_c(p_150063_1_) > 0;
        float f = 0.0625F;

        if (flag)
        {
            this.func_149676_a(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
        }
        else
        {
            this.func_149676_a(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
        }
    }

    public int func_149738_a(World p_149738_1_)
    {
        return 20;
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

    public boolean func_149655_b(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
    {
        return true;
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return World.func_147466_a(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_) || BlockFence.func_149825_a(p_149742_1_.func_147439_a(p_149742_2_, p_149742_3_ - 1, p_149742_4_));
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        boolean flag = false;

        if (!World.func_147466_a(p_149695_1_, p_149695_2_, p_149695_3_ - 1, p_149695_4_) && !BlockFence.func_149825_a(p_149695_1_.func_147439_a(p_149695_2_, p_149695_3_ - 1, p_149695_4_)))
        {
            flag = true;
        }

        if (flag)
        {
            this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
            p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
        }
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (!p_149674_1_.isRemote)
        {
            int l = this.func_150060_c(p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_));

            if (l > 0)
            {
                this.func_150062_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, l);
            }
        }
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        if (!p_149670_1_.isRemote)
        {
            int l = this.func_150060_c(p_149670_1_.getBlockMetadata(p_149670_2_, p_149670_3_, p_149670_4_));

            if (l == 0)
            {
                this.func_150062_a(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, l);
            }
        }
    }

    protected void func_150062_a(World p_150062_1_, int p_150062_2_, int p_150062_3_, int p_150062_4_, int p_150062_5_)
    {
        int i1 = this.func_150065_e(p_150062_1_, p_150062_2_, p_150062_3_, p_150062_4_);
        boolean flag = p_150062_5_ > 0;
        boolean flag1 = i1 > 0;

        if (p_150062_5_ != i1)
        {
            p_150062_1_.setBlockMetadataWithNotify(p_150062_2_, p_150062_3_, p_150062_4_, this.func_150066_d(i1), 2);
            this.func_150064_a_(p_150062_1_, p_150062_2_, p_150062_3_, p_150062_4_);
            p_150062_1_.func_147458_c(p_150062_2_, p_150062_3_, p_150062_4_, p_150062_2_, p_150062_3_, p_150062_4_);
        }

        if (!flag1 && flag)
        {
            p_150062_1_.playSoundEffect((double)p_150062_2_ + 0.5D, (double)p_150062_3_ + 0.1D, (double)p_150062_4_ + 0.5D, "random.click", 0.3F, 0.5F);
        }
        else if (flag1 && !flag)
        {
            p_150062_1_.playSoundEffect((double)p_150062_2_ + 0.5D, (double)p_150062_3_ + 0.1D, (double)p_150062_4_ + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (flag1)
        {
            p_150062_1_.func_147464_a(p_150062_2_, p_150062_3_, p_150062_4_, this, this.func_149738_a(p_150062_1_));
        }
    }

    protected AxisAlignedBB func_150061_a(int p_150061_1_, int p_150061_2_, int p_150061_3_)
    {
        float f = 0.125F;
        return AxisAlignedBB.getAABBPool().getAABB((double)((float)p_150061_1_ + f), (double)p_150061_2_, (double)((float)p_150061_3_ + f), (double)((float)(p_150061_1_ + 1) - f), (double)p_150061_2_ + 0.25D, (double)((float)(p_150061_3_ + 1) - f));
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        if (this.func_150060_c(p_149749_6_) > 0)
        {
            this.func_150064_a_(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_);
        }

        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }

    protected void func_150064_a_(World p_150064_1_, int p_150064_2_, int p_150064_3_, int p_150064_4_)
    {
        p_150064_1_.func_147459_d(p_150064_2_, p_150064_3_, p_150064_4_, this);
        p_150064_1_.func_147459_d(p_150064_2_, p_150064_3_ - 1, p_150064_4_, this);
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return this.func_150060_c(p_149709_1_.getBlockMetadata(p_149709_2_, p_149709_3_, p_149709_4_));
    }

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        return p_149748_5_ == 1 ? this.func_150060_c(p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_)) : 0;
    }

    public boolean func_149744_f()
    {
        return true;
    }

    public void func_149683_g()
    {
        float f = 0.5F;
        float f1 = 0.125F;
        float f2 = 0.5F;
        this.func_149676_a(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    public int func_149656_h()
    {
        return 1;
    }

    protected abstract int func_150065_e(World var1, int var2, int var3, int var4);

    protected abstract int func_150060_c(int var1);

    protected abstract int func_150066_d(int var1);

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149761_L = p_149651_1_.registerIcon(this.field_150067_a);
    }
}