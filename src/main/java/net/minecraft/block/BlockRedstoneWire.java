package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block
{
    private boolean field_150181_a = true;
    private Set field_150179_b = new HashSet();
    @SideOnly(Side.CLIENT)
    private IIcon field_150182_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_150183_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_150184_O;
    @SideOnly(Side.CLIENT)
    private IIcon field_150180_P;
    private static final String __OBFID = "CL_00000295";

    public BlockRedstoneWire()
    {
        super(Material.field_151594_q);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
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
        return 5;
    }

    @SideOnly(Side.CLIENT)
    public int func_149720_d(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
    {
        return 8388608;
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return World.func_147466_a(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_) || p_149742_1_.func_147439_a(p_149742_2_, p_149742_3_ - 1, p_149742_4_) == Blocks.glowstone;
    }

    private void func_150177_e(World p_150177_1_, int p_150177_2_, int p_150177_3_, int p_150177_4_)
    {
        this.func_150175_a(p_150177_1_, p_150177_2_, p_150177_3_, p_150177_4_, p_150177_2_, p_150177_3_, p_150177_4_);
        ArrayList arraylist = new ArrayList(this.field_150179_b);
        this.field_150179_b.clear();

        for (int l = 0; l < arraylist.size(); ++l)
        {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(l);
            p_150177_1_.func_147459_d(chunkposition.field_151329_a, chunkposition.field_151327_b, chunkposition.field_151328_c, this);
        }
    }

    private void func_150175_a(World p_150175_1_, int p_150175_2_, int p_150175_3_, int p_150175_4_, int p_150175_5_, int p_150175_6_, int p_150175_7_)
    {
        int k1 = p_150175_1_.getBlockMetadata(p_150175_2_, p_150175_3_, p_150175_4_);
        byte b0 = 0;
        int i3 = this.func_150178_a(p_150175_1_, p_150175_5_, p_150175_6_, p_150175_7_, b0);
        this.field_150181_a = false;
        int l1 = p_150175_1_.getStrongestIndirectPower(p_150175_2_, p_150175_3_, p_150175_4_);
        this.field_150181_a = true;

        if (l1 > 0 && l1 > i3 - 1)
        {
            i3 = l1;
        }

        int i2 = 0;

        for (int j2 = 0; j2 < 4; ++j2)
        {
            int k2 = p_150175_2_;
            int l2 = p_150175_4_;

            if (j2 == 0)
            {
                k2 = p_150175_2_ - 1;
            }

            if (j2 == 1)
            {
                ++k2;
            }

            if (j2 == 2)
            {
                l2 = p_150175_4_ - 1;
            }

            if (j2 == 3)
            {
                ++l2;
            }

            if (k2 != p_150175_5_ || l2 != p_150175_7_)
            {
                i2 = this.func_150178_a(p_150175_1_, k2, p_150175_3_, l2, i2);
            }

            if (p_150175_1_.func_147439_a(k2, p_150175_3_, l2).func_149721_r() && !p_150175_1_.func_147439_a(p_150175_2_, p_150175_3_ + 1, p_150175_4_).func_149721_r())
            {
                if ((k2 != p_150175_5_ || l2 != p_150175_7_) && p_150175_3_ >= p_150175_6_)
                {
                    i2 = this.func_150178_a(p_150175_1_, k2, p_150175_3_ + 1, l2, i2);
                }
            }
            else if (!p_150175_1_.func_147439_a(k2, p_150175_3_, l2).func_149721_r() && (k2 != p_150175_5_ || l2 != p_150175_7_) && p_150175_3_ <= p_150175_6_)
            {
                i2 = this.func_150178_a(p_150175_1_, k2, p_150175_3_ - 1, l2, i2);
            }
        }

        if (i2 > i3)
        {
            i3 = i2 - 1;
        }
        else if (i3 > 0)
        {
            --i3;
        }
        else
        {
            i3 = 0;
        }

        if (l1 > i3 - 1)
        {
            i3 = l1;
        }

        if (k1 != i3)
        {
            p_150175_1_.setBlockMetadataWithNotify(p_150175_2_, p_150175_3_, p_150175_4_, i3, 2);
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_ - 1, p_150175_3_, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_ + 1, p_150175_3_, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_ - 1, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_ + 1, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_, p_150175_4_ - 1));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_, p_150175_4_ + 1));
        }
    }

    private void func_150172_m(World p_150172_1_, int p_150172_2_, int p_150172_3_, int p_150172_4_)
    {
        if (p_150172_1_.func_147439_a(p_150172_2_, p_150172_3_, p_150172_4_) == this)
        {
            p_150172_1_.func_147459_d(p_150172_2_, p_150172_3_, p_150172_4_, this);
            p_150172_1_.func_147459_d(p_150172_2_ - 1, p_150172_3_, p_150172_4_, this);
            p_150172_1_.func_147459_d(p_150172_2_ + 1, p_150172_3_, p_150172_4_, this);
            p_150172_1_.func_147459_d(p_150172_2_, p_150172_3_, p_150172_4_ - 1, this);
            p_150172_1_.func_147459_d(p_150172_2_, p_150172_3_, p_150172_4_ + 1, this);
            p_150172_1_.func_147459_d(p_150172_2_, p_150172_3_ - 1, p_150172_4_, this);
            p_150172_1_.func_147459_d(p_150172_2_, p_150172_3_ + 1, p_150172_4_, this);
        }
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.func_149726_b(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);

        if (!p_149726_1_.isRemote)
        {
            this.func_150177_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
            p_149726_1_.func_147459_d(p_149726_2_, p_149726_3_ + 1, p_149726_4_, this);
            p_149726_1_.func_147459_d(p_149726_2_, p_149726_3_ - 1, p_149726_4_, this);
            this.func_150172_m(p_149726_1_, p_149726_2_ - 1, p_149726_3_, p_149726_4_);
            this.func_150172_m(p_149726_1_, p_149726_2_ + 1, p_149726_3_, p_149726_4_);
            this.func_150172_m(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_ - 1);
            this.func_150172_m(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_ + 1);

            if (p_149726_1_.func_147439_a(p_149726_2_ - 1, p_149726_3_, p_149726_4_).func_149721_r())
            {
                this.func_150172_m(p_149726_1_, p_149726_2_ - 1, p_149726_3_ + 1, p_149726_4_);
            }
            else
            {
                this.func_150172_m(p_149726_1_, p_149726_2_ - 1, p_149726_3_ - 1, p_149726_4_);
            }

            if (p_149726_1_.func_147439_a(p_149726_2_ + 1, p_149726_3_, p_149726_4_).func_149721_r())
            {
                this.func_150172_m(p_149726_1_, p_149726_2_ + 1, p_149726_3_ + 1, p_149726_4_);
            }
            else
            {
                this.func_150172_m(p_149726_1_, p_149726_2_ + 1, p_149726_3_ - 1, p_149726_4_);
            }

            if (p_149726_1_.func_147439_a(p_149726_2_, p_149726_3_, p_149726_4_ - 1).func_149721_r())
            {
                this.func_150172_m(p_149726_1_, p_149726_2_, p_149726_3_ + 1, p_149726_4_ - 1);
            }
            else
            {
                this.func_150172_m(p_149726_1_, p_149726_2_, p_149726_3_ - 1, p_149726_4_ - 1);
            }

            if (p_149726_1_.func_147439_a(p_149726_2_, p_149726_3_, p_149726_4_ + 1).func_149721_r())
            {
                this.func_150172_m(p_149726_1_, p_149726_2_, p_149726_3_ + 1, p_149726_4_ + 1);
            }
            else
            {
                this.func_150172_m(p_149726_1_, p_149726_2_, p_149726_3_ - 1, p_149726_4_ + 1);
            }
        }
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);

        if (!p_149749_1_.isRemote)
        {
            p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_ + 1, p_149749_4_, this);
            p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_ - 1, p_149749_4_, this);
            p_149749_1_.func_147459_d(p_149749_2_ + 1, p_149749_3_, p_149749_4_, this);
            p_149749_1_.func_147459_d(p_149749_2_ - 1, p_149749_3_, p_149749_4_, this);
            p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_, p_149749_4_ + 1, this);
            p_149749_1_.func_147459_d(p_149749_2_, p_149749_3_, p_149749_4_ - 1, this);
            this.func_150177_e(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_);
            this.func_150172_m(p_149749_1_, p_149749_2_ - 1, p_149749_3_, p_149749_4_);
            this.func_150172_m(p_149749_1_, p_149749_2_ + 1, p_149749_3_, p_149749_4_);
            this.func_150172_m(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_ - 1);
            this.func_150172_m(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_ + 1);

            if (p_149749_1_.func_147439_a(p_149749_2_ - 1, p_149749_3_, p_149749_4_).func_149721_r())
            {
                this.func_150172_m(p_149749_1_, p_149749_2_ - 1, p_149749_3_ + 1, p_149749_4_);
            }
            else
            {
                this.func_150172_m(p_149749_1_, p_149749_2_ - 1, p_149749_3_ - 1, p_149749_4_);
            }

            if (p_149749_1_.func_147439_a(p_149749_2_ + 1, p_149749_3_, p_149749_4_).func_149721_r())
            {
                this.func_150172_m(p_149749_1_, p_149749_2_ + 1, p_149749_3_ + 1, p_149749_4_);
            }
            else
            {
                this.func_150172_m(p_149749_1_, p_149749_2_ + 1, p_149749_3_ - 1, p_149749_4_);
            }

            if (p_149749_1_.func_147439_a(p_149749_2_, p_149749_3_, p_149749_4_ - 1).func_149721_r())
            {
                this.func_150172_m(p_149749_1_, p_149749_2_, p_149749_3_ + 1, p_149749_4_ - 1);
            }
            else
            {
                this.func_150172_m(p_149749_1_, p_149749_2_, p_149749_3_ - 1, p_149749_4_ - 1);
            }

            if (p_149749_1_.func_147439_a(p_149749_2_, p_149749_3_, p_149749_4_ + 1).func_149721_r())
            {
                this.func_150172_m(p_149749_1_, p_149749_2_, p_149749_3_ + 1, p_149749_4_ + 1);
            }
            else
            {
                this.func_150172_m(p_149749_1_, p_149749_2_, p_149749_3_ - 1, p_149749_4_ + 1);
            }
        }
    }

    private int func_150178_a(World p_150178_1_, int p_150178_2_, int p_150178_3_, int p_150178_4_, int p_150178_5_)
    {
        if (p_150178_1_.func_147439_a(p_150178_2_, p_150178_3_, p_150178_4_) != this)
        {
            return p_150178_5_;
        }
        else
        {
            int i1 = p_150178_1_.getBlockMetadata(p_150178_2_, p_150178_3_, p_150178_4_);
            return i1 > p_150178_5_ ? i1 : p_150178_5_;
        }
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!p_149695_1_.isRemote)
        {
            boolean flag = this.func_149742_c(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);

            if (flag)
            {
                this.func_150177_e(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
            }
            else
            {
                this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, 0, 0);
                p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
            }

            super.func_149695_a(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
        }
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.redstone;
    }

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        return !this.field_150181_a ? 0 : this.func_149709_b(p_149748_1_, p_149748_2_, p_149748_3_, p_149748_4_, p_149748_5_);
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        if (!this.field_150181_a)
        {
            return 0;
        }
        else
        {
            int i1 = p_149709_1_.getBlockMetadata(p_149709_2_, p_149709_3_, p_149709_4_);

            if (i1 == 0)
            {
                return 0;
            }
            else if (p_149709_5_ == 1)
            {
                return i1;
            }
            else
            {
                boolean flag = func_150176_g(p_149709_1_, p_149709_2_ - 1, p_149709_3_, p_149709_4_, 1) || !p_149709_1_.func_147439_a(p_149709_2_ - 1, p_149709_3_, p_149709_4_).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_ - 1, p_149709_3_ - 1, p_149709_4_, -1);
                boolean flag1 = func_150176_g(p_149709_1_, p_149709_2_ + 1, p_149709_3_, p_149709_4_, 3) || !p_149709_1_.func_147439_a(p_149709_2_ + 1, p_149709_3_, p_149709_4_).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_ + 1, p_149709_3_ - 1, p_149709_4_, -1);
                boolean flag2 = func_150176_g(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_ - 1, 2) || !p_149709_1_.func_147439_a(p_149709_2_, p_149709_3_, p_149709_4_ - 1).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_, p_149709_3_ - 1, p_149709_4_ - 1, -1);
                boolean flag3 = func_150176_g(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_ + 1, 0) || !p_149709_1_.func_147439_a(p_149709_2_, p_149709_3_, p_149709_4_ + 1).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_, p_149709_3_ - 1, p_149709_4_ + 1, -1);

                if (!p_149709_1_.func_147439_a(p_149709_2_, p_149709_3_ + 1, p_149709_4_).func_149721_r())
                {
                    if (p_149709_1_.func_147439_a(p_149709_2_ - 1, p_149709_3_, p_149709_4_).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_ - 1, p_149709_3_ + 1, p_149709_4_, -1))
                    {
                        flag = true;
                    }

                    if (p_149709_1_.func_147439_a(p_149709_2_ + 1, p_149709_3_, p_149709_4_).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_ + 1, p_149709_3_ + 1, p_149709_4_, -1))
                    {
                        flag1 = true;
                    }

                    if (p_149709_1_.func_147439_a(p_149709_2_, p_149709_3_, p_149709_4_ - 1).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_, p_149709_3_ + 1, p_149709_4_ - 1, -1))
                    {
                        flag2 = true;
                    }

                    if (p_149709_1_.func_147439_a(p_149709_2_, p_149709_3_, p_149709_4_ + 1).func_149721_r() && func_150176_g(p_149709_1_, p_149709_2_, p_149709_3_ + 1, p_149709_4_ + 1, -1))
                    {
                        flag3 = true;
                    }
                }

                return !flag2 && !flag1 && !flag && !flag3 && p_149709_5_ >= 2 && p_149709_5_ <= 5 ? i1 : (p_149709_5_ == 2 && flag2 && !flag && !flag1 ? i1 : (p_149709_5_ == 3 && flag3 && !flag && !flag1 ? i1 : (p_149709_5_ == 4 && flag && !flag2 && !flag3 ? i1 : (p_149709_5_ == 5 && flag1 && !flag2 && !flag3 ? i1 : 0))));
            }
        }
    }

    public boolean func_149744_f()
    {
        return this.field_150181_a;
    }

    public static boolean func_150174_f(IBlockAccess p_150174_0_, int p_150174_1_, int p_150174_2_, int p_150174_3_, int p_150174_4_)
    {
        Block block = p_150174_0_.func_147439_a(p_150174_1_, p_150174_2_, p_150174_3_);

        if (block == Blocks.redstone_wire)
        {
            return true;
        }
        else if (!Blocks.unpowered_repeater.func_149907_e(block))
        {
            return block.canConnectRedstone(p_150174_0_, p_150174_1_, p_150174_2_, p_150174_3_, p_150174_4_);
        }
        else
        {
            int i1 = p_150174_0_.getBlockMetadata(p_150174_1_, p_150174_2_, p_150174_3_);
            return p_150174_4_ == (i1 & 3) || p_150174_4_ == Direction.rotateOpposite[i1 & 3];
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_149734_b(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    {
        int l = p_149734_1_.getBlockMetadata(p_149734_2_, p_149734_3_, p_149734_4_);

        if (l > 0)
        {
            double d0 = (double)p_149734_2_ + 0.5D + ((double)p_149734_5_.nextFloat() - 0.5D) * 0.2D;
            double d1 = (double)((float)p_149734_3_ + 0.0625F);
            double d2 = (double)p_149734_4_ + 0.5D + ((double)p_149734_5_.nextFloat() - 0.5D) * 0.2D;
            float f = (float)l / 15.0F;
            float f1 = f * 0.6F + 0.4F;

            if (l == 0)
            {
                f1 = 0.0F;
            }

            float f2 = f * f * 0.7F - 0.5F;
            float f3 = f * f * 0.6F - 0.7F;

            if (f2 < 0.0F)
            {
                f2 = 0.0F;
            }

            if (f3 < 0.0F)
            {
                f3 = 0.0F;
            }

            p_149734_1_.spawnParticle("reddust", d0, d1, d2, (double)f1, (double)f2, (double)f3);
        }
    }

    public static boolean func_150176_g(IBlockAccess p_150176_0_, int p_150176_1_, int p_150176_2_, int p_150176_3_, int p_150176_4_)
    {
        if (func_150174_f(p_150176_0_, p_150176_1_, p_150176_2_, p_150176_3_, p_150176_4_))
        {
            return true;
        }
        else if (p_150176_0_.func_147439_a(p_150176_1_, p_150176_2_, p_150176_3_) == Blocks.powered_repeater)
        {
            int i1 = p_150176_0_.getBlockMetadata(p_150176_1_, p_150176_2_, p_150176_3_);
            return p_150176_4_ == (i1 & 3);
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Items.redstone;
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_150182_M = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "cross");
        this.field_150183_N = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "line");
        this.field_150184_O = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "cross_overlay");
        this.field_150180_P = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "line_overlay");
        this.field_149761_L = this.field_150182_M;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon func_150173_e(String p_150173_0_)
    {
        return p_150173_0_.equals("cross") ? Blocks.redstone_wire.field_150182_M : (p_150173_0_.equals("line") ? Blocks.redstone_wire.field_150183_N : (p_150173_0_.equals("cross_overlay") ? Blocks.redstone_wire.field_150184_O : (p_150173_0_.equals("line_overlay") ? Blocks.redstone_wire.field_150180_P : null)));
    }
}