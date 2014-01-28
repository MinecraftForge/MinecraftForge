package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional
{
    protected final boolean field_149914_a;
    private static final String __OBFID = "CL_00000226";

    protected BlockRedstoneDiode(boolean p_i45400_1_)
    {
        super(Material.field_151594_q);
        this.field_149914_a = p_i45400_1_;
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return !World.func_147466_a(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_) ? false : super.func_149742_c(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
    }

    public boolean func_149718_j(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        return !World.func_147466_a(p_149718_1_, p_149718_2_, p_149718_3_ - 1, p_149718_4_) ? false : super.func_149718_j(p_149718_1_, p_149718_2_, p_149718_3_, p_149718_4_);
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);

        if (!this.func_149910_g(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, l))
        {
            boolean flag = this.func_149900_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, l);

            if (this.field_149914_a && !flag)
            {
                p_149674_1_.func_147465_d(p_149674_2_, p_149674_3_, p_149674_4_, this.func_149898_i(), l, 2);
            }
            else if (!this.field_149914_a)
            {
                p_149674_1_.func_147465_d(p_149674_2_, p_149674_3_, p_149674_4_, this.func_149906_e(), l, 2);

                if (!flag)
                {
                    p_149674_1_.func_147454_a(p_149674_2_, p_149674_3_, p_149674_4_, this.func_149906_e(), this.func_149899_k(l), -1);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_1_ == 0 ? (this.field_149914_a ? Blocks.redstone_torch.func_149733_h(p_149691_1_) : Blocks.unlit_redstone_torch.func_149733_h(p_149691_1_)) : (p_149691_1_ == 1 ? this.field_149761_L : Blocks.double_stone_slab.func_149733_h(1));
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return p_149646_5_ != 0 && p_149646_5_ != 1;
    }

    public int func_149645_b()
    {
        return 36;
    }

    protected boolean func_149905_c(int p_149905_1_)
    {
        return this.field_149914_a;
    }

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        return this.func_149709_b(p_149748_1_, p_149748_2_, p_149748_3_, p_149748_4_, p_149748_5_);
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        int i1 = p_149709_1_.getBlockMetadata(p_149709_2_, p_149709_3_, p_149709_4_);

        if (!this.func_149905_c(i1))
        {
            return 0;
        }
        else
        {
            int j1 = func_149895_l(i1);
            return j1 == 0 && p_149709_5_ == 3 ? this.func_149904_f(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_, i1) : (j1 == 1 && p_149709_5_ == 4 ? this.func_149904_f(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_, i1) : (j1 == 2 && p_149709_5_ == 2 ? this.func_149904_f(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_, i1) : (j1 == 3 && p_149709_5_ == 5 ? this.func_149904_f(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_, i1) : 0)));
        }
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!this.func_149718_j(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_))
        {
            this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
            p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
            p_149695_1_.func_147459_d(p_149695_2_ + 1, p_149695_3_, p_149695_4_, this);
            p_149695_1_.func_147459_d(p_149695_2_ - 1, p_149695_3_, p_149695_4_, this);
            p_149695_1_.func_147459_d(p_149695_2_, p_149695_3_, p_149695_4_ + 1, this);
            p_149695_1_.func_147459_d(p_149695_2_, p_149695_3_, p_149695_4_ - 1, this);
            p_149695_1_.func_147459_d(p_149695_2_, p_149695_3_ - 1, p_149695_4_, this);
            p_149695_1_.func_147459_d(p_149695_2_, p_149695_3_ + 1, p_149695_4_, this);
        }
        else
        {
            this.func_149897_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
        }
    }

    protected void func_149897_b(World p_149897_1_, int p_149897_2_, int p_149897_3_, int p_149897_4_, Block p_149897_5_)
    {
        int l = p_149897_1_.getBlockMetadata(p_149897_2_, p_149897_3_, p_149897_4_);

        if (!this.func_149910_g(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_, l))
        {
            boolean flag = this.func_149900_a(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_, l);

            if ((this.field_149914_a && !flag || !this.field_149914_a && flag) && !p_149897_1_.func_147477_a(p_149897_2_, p_149897_3_, p_149897_4_, this))
            {
                byte b0 = -1;

                if (this.func_149912_i(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_, l))
                {
                    b0 = -3;
                }
                else if (this.field_149914_a)
                {
                    b0 = -2;
                }

                p_149897_1_.func_147454_a(p_149897_2_, p_149897_3_, p_149897_4_, this, this.func_149901_b(l), b0);
            }
        }
    }

    public boolean func_149910_g(IBlockAccess p_149910_1_, int p_149910_2_, int p_149910_3_, int p_149910_4_, int p_149910_5_)
    {
        return false;
    }

    protected boolean func_149900_a(World p_149900_1_, int p_149900_2_, int p_149900_3_, int p_149900_4_, int p_149900_5_)
    {
        return this.func_149903_h(p_149900_1_, p_149900_2_, p_149900_3_, p_149900_4_, p_149900_5_) > 0;
    }

    protected int func_149903_h(World p_149903_1_, int p_149903_2_, int p_149903_3_, int p_149903_4_, int p_149903_5_)
    {
        int i1 = func_149895_l(p_149903_5_);
        int j1 = p_149903_2_ + Direction.offsetX[i1];
        int k1 = p_149903_4_ + Direction.offsetZ[i1];
        int l1 = p_149903_1_.getIndirectPowerLevelTo(j1, p_149903_3_, k1, Direction.directionToFacing[i1]);
        return l1 >= 15 ? l1 : Math.max(l1, p_149903_1_.func_147439_a(j1, p_149903_3_, k1) == Blocks.redstone_wire ? p_149903_1_.getBlockMetadata(j1, p_149903_3_, k1) : 0);
    }

    protected int func_149902_h(IBlockAccess p_149902_1_, int p_149902_2_, int p_149902_3_, int p_149902_4_, int p_149902_5_)
    {
        int i1 = func_149895_l(p_149902_5_);

        switch (i1)
        {
            case 0:
            case 2:
                return Math.max(this.func_149913_i(p_149902_1_, p_149902_2_ - 1, p_149902_3_, p_149902_4_, 4), this.func_149913_i(p_149902_1_, p_149902_2_ + 1, p_149902_3_, p_149902_4_, 5));
            case 1:
            case 3:
                return Math.max(this.func_149913_i(p_149902_1_, p_149902_2_, p_149902_3_, p_149902_4_ + 1, 3), this.func_149913_i(p_149902_1_, p_149902_2_, p_149902_3_, p_149902_4_ - 1, 2));
            default:
                return 0;
        }
    }

    protected int func_149913_i(IBlockAccess p_149913_1_, int p_149913_2_, int p_149913_3_, int p_149913_4_, int p_149913_5_)
    {
        Block block = p_149913_1_.func_147439_a(p_149913_2_, p_149913_3_, p_149913_4_);
        return this.func_149908_a(block) ? (block == Blocks.redstone_wire ? p_149913_1_.getBlockMetadata(p_149913_2_, p_149913_3_, p_149913_4_) : p_149913_1_.isBlockProvidingPowerTo(p_149913_2_, p_149913_3_, p_149913_4_, p_149913_5_)) : 0;
    }

    public boolean func_149744_f()
    {
        return true;
    }

    public void func_149689_a(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        int l = ((MathHelper.floor_double((double)(p_149689_5_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
        p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 3);
        boolean flag = this.func_149900_a(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, l);

        if (flag)
        {
            p_149689_1_.func_147464_a(p_149689_2_, p_149689_3_, p_149689_4_, this, 1);
        }
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        this.func_149911_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
    }

    protected void func_149911_e(World p_149911_1_, int p_149911_2_, int p_149911_3_, int p_149911_4_)
    {
        int l = func_149895_l(p_149911_1_.getBlockMetadata(p_149911_2_, p_149911_3_, p_149911_4_));

        if (l == 1)
        {
            p_149911_1_.func_147460_e(p_149911_2_ + 1, p_149911_3_, p_149911_4_, this);
            p_149911_1_.func_147441_b(p_149911_2_ + 1, p_149911_3_, p_149911_4_, this, 4);
        }

        if (l == 3)
        {
            p_149911_1_.func_147460_e(p_149911_2_ - 1, p_149911_3_, p_149911_4_, this);
            p_149911_1_.func_147441_b(p_149911_2_ - 1, p_149911_3_, p_149911_4_, this, 5);
        }

        if (l == 2)
        {
            p_149911_1_.func_147460_e(p_149911_2_, p_149911_3_, p_149911_4_ + 1, this);
            p_149911_1_.func_147441_b(p_149911_2_, p_149911_3_, p_149911_4_ + 1, this, 2);
        }

        if (l == 0)
        {
            p_149911_1_.func_147460_e(p_149911_2_, p_149911_3_, p_149911_4_ - 1, this);
            p_149911_1_.func_147441_b(p_149911_2_, p_149911_3_, p_149911_4_ - 1, this, 3);
        }
    }

    public void func_149664_b(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_)
    {
        if (this.field_149914_a)
        {
            p_149664_1_.func_147459_d(p_149664_2_ + 1, p_149664_3_, p_149664_4_, this);
            p_149664_1_.func_147459_d(p_149664_2_ - 1, p_149664_3_, p_149664_4_, this);
            p_149664_1_.func_147459_d(p_149664_2_, p_149664_3_, p_149664_4_ + 1, this);
            p_149664_1_.func_147459_d(p_149664_2_, p_149664_3_, p_149664_4_ - 1, this);
            p_149664_1_.func_147459_d(p_149664_2_, p_149664_3_ - 1, p_149664_4_, this);
            p_149664_1_.func_147459_d(p_149664_2_, p_149664_3_ + 1, p_149664_4_, this);
        }

        super.func_149664_b(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
    }

    public boolean func_149662_c()
    {
        return false;
    }

    protected boolean func_149908_a(Block p_149908_1_)
    {
        return p_149908_1_.func_149744_f();
    }

    protected int func_149904_f(IBlockAccess p_149904_1_, int p_149904_2_, int p_149904_3_, int p_149904_4_, int p_149904_5_)
    {
        return 15;
    }

    public static boolean func_149909_d(Block p_149909_0_)
    {
        return Blocks.unpowered_repeater.func_149907_e(p_149909_0_) || Blocks.unpowered_comparator.func_149907_e(p_149909_0_);
    }

    public boolean func_149907_e(Block p_149907_1_)
    {
        return p_149907_1_ == this.func_149906_e() || p_149907_1_ == this.func_149898_i();
    }

    public boolean func_149912_i(World p_149912_1_, int p_149912_2_, int p_149912_3_, int p_149912_4_, int p_149912_5_)
    {
        int i1 = func_149895_l(p_149912_5_);

        if (func_149909_d(p_149912_1_.func_147439_a(p_149912_2_ - Direction.offsetX[i1], p_149912_3_, p_149912_4_ - Direction.offsetZ[i1])))
        {
            int j1 = p_149912_1_.getBlockMetadata(p_149912_2_ - Direction.offsetX[i1], p_149912_3_, p_149912_4_ - Direction.offsetZ[i1]);
            int k1 = func_149895_l(j1);
            return k1 != i1;
        }
        else
        {
            return false;
        }
    }

    protected int func_149899_k(int p_149899_1_)
    {
        return this.func_149901_b(p_149899_1_);
    }

    protected abstract int func_149901_b(int var1);

    protected abstract BlockRedstoneDiode func_149906_e();

    protected abstract BlockRedstoneDiode func_149898_i();

    public boolean func_149667_c(Block p_149667_1_)
    {
        return this.func_149907_e(p_149667_1_);
    }
}