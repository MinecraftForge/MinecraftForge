package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoublePlant extends BlockBush implements IGrowable
{
    public static final String[] field_149892_a = new String[] {"sunflower", "syringa", "grass", "fern", "rose", "paeonia"};
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149893_M;
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149894_N;
    @SideOnly(Side.CLIENT)
    public IIcon[] field_149891_b;
    private static final String __OBFID = "CL_00000231";

    public BlockDoublePlant()
    {
        super(Material.field_151585_k);
        this.func_149711_c(0.0F);
        this.func_149672_a(field_149779_h);
        this.func_149663_c("doublePlant");
    }

    public int func_149645_b()
    {
        return 40;
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public int func_149885_e(IBlockAccess p_149885_1_, int p_149885_2_, int p_149885_3_, int p_149885_4_)
    {
        int l = p_149885_1_.getBlockMetadata(p_149885_2_, p_149885_3_, p_149885_4_);
        return !func_149887_c(l) ? l & 7 : p_149885_1_.getBlockMetadata(p_149885_2_, p_149885_3_ - 1, p_149885_4_) & 7;
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return super.func_149742_c(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_) && p_149742_1_.func_147437_c(p_149742_2_, p_149742_3_ + 1, p_149742_4_);
    }

    protected void func_149855_e(World p_149855_1_, int p_149855_2_, int p_149855_3_, int p_149855_4_)
    {
        if (!this.func_149718_j(p_149855_1_, p_149855_2_, p_149855_3_, p_149855_4_))
        {
            int l = p_149855_1_.getBlockMetadata(p_149855_2_, p_149855_3_, p_149855_4_);

            if (!func_149887_c(l))
            {
                this.func_149697_b(p_149855_1_, p_149855_2_, p_149855_3_, p_149855_4_, l, 0);

                if (p_149855_1_.func_147439_a(p_149855_2_, p_149855_3_ + 1, p_149855_4_) == this)
                {
                    p_149855_1_.func_147465_d(p_149855_2_, p_149855_3_ + 1, p_149855_4_, Blocks.air, 0, 2);
                }
            }

            p_149855_1_.func_147465_d(p_149855_2_, p_149855_3_, p_149855_4_, Blocks.air, 0, 2);
        }
    }

    public boolean func_149718_j(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        if (p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_, p_149718_4_) != this) return super.func_149718_j(p_149718_1_, p_149718_2_, p_149718_3_, p_149718_4_); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
        int l = p_149718_1_.getBlockMetadata(p_149718_2_, p_149718_3_, p_149718_4_);
        return func_149887_c(l) ? p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_ - 1, p_149718_4_) == this : p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_ + 1, p_149718_4_) == this && super.func_149718_j(p_149718_1_, p_149718_2_, p_149718_3_, p_149718_4_);
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        if (func_149887_c(p_149650_1_))
        {
            return null;
        }
        else
        {
            int k = func_149890_d(p_149650_1_);
            return k != 3 && k != 2 ? Item.func_150898_a(this) : null;
        }
    }

    public int func_149692_a(int p_149692_1_)
    {
        return func_149887_c(p_149692_1_) ? 0 : p_149692_1_ & 7;
    }

    public static boolean func_149887_c(int p_149887_0_)
    {
        return (p_149887_0_ & 8) != 0;
    }

    public static int func_149890_d(int p_149890_0_)
    {
        return p_149890_0_ & 7;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return func_149887_c(p_149691_2_) ? this.field_149893_M[0] : this.field_149893_M[p_149691_2_ & 7];
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149888_a(boolean p_149888_1_, int p_149888_2_)
    {
        return p_149888_1_ ? this.field_149894_N[p_149888_2_] : this.field_149893_M[p_149888_2_];
    }

    @SideOnly(Side.CLIENT)
    public int func_149720_d(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
    {
        int l = this.func_149885_e(p_149720_1_, p_149720_2_, p_149720_3_, p_149720_4_);
        return l != 2 && l != 3 ? 16777215 : p_149720_1_.getBiomeGenForCoords(p_149720_2_, p_149720_4_).func_150558_b(p_149720_2_, p_149720_3_, p_149720_4_);
    }

    public void func_149889_c(World p_149889_1_, int p_149889_2_, int p_149889_3_, int p_149889_4_, int p_149889_5_, int p_149889_6_)
    {
        p_149889_1_.func_147465_d(p_149889_2_, p_149889_3_, p_149889_4_, this, p_149889_5_, p_149889_6_);
        p_149889_1_.func_147465_d(p_149889_2_, p_149889_3_ + 1, p_149889_4_, this, 8, p_149889_6_);
    }

    public void func_149689_a(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        int l = ((MathHelper.floor_double((double)(p_149689_5_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
        p_149689_1_.func_147465_d(p_149689_2_, p_149689_3_ + 1, p_149689_4_, this, 8 | l, 2);
    }

    public void func_149636_a(World p_149636_1_, EntityPlayer p_149636_2_, int p_149636_3_, int p_149636_4_, int p_149636_5_, int p_149636_6_)
    {
        if (p_149636_1_.isRemote || p_149636_2_.getCurrentEquippedItem() == null || p_149636_2_.getCurrentEquippedItem().getItem() != Items.shears || func_149887_c(p_149636_6_) || !this.func_149886_b(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_, p_149636_2_))
        {
            super.func_149636_a(p_149636_1_, p_149636_2_, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_);
        }
    }

    public void func_149681_a(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_)
    {
        if (func_149887_c(p_149681_5_))
        {
            if (p_149681_1_.func_147439_a(p_149681_2_, p_149681_3_ - 1, p_149681_4_) == this)
            {
                if (!p_149681_6_.capabilities.isCreativeMode)
                {
                    int i1 = p_149681_1_.getBlockMetadata(p_149681_2_, p_149681_3_ - 1, p_149681_4_);
                    int j1 = func_149890_d(i1);

                    if (j1 != 3 && j1 != 2)
                    {
                        p_149681_1_.func_147480_a(p_149681_2_, p_149681_3_ - 1, p_149681_4_, true);
                    }
                    else
                    {
                        if (!p_149681_1_.isRemote && p_149681_6_.getCurrentEquippedItem() != null && p_149681_6_.getCurrentEquippedItem().getItem() == Items.shears)
                        {
                            this.func_149886_b(p_149681_1_, p_149681_2_, p_149681_3_, p_149681_4_, i1, p_149681_6_);
                        }

                        p_149681_1_.func_147468_f(p_149681_2_, p_149681_3_ - 1, p_149681_4_);
                    }
                }
                else
                {
                    p_149681_1_.func_147468_f(p_149681_2_, p_149681_3_ - 1, p_149681_4_);
                }
            }
        }
        else if (p_149681_6_.capabilities.isCreativeMode && p_149681_1_.func_147439_a(p_149681_2_, p_149681_3_ + 1, p_149681_4_) == this)
        {
            p_149681_1_.func_147465_d(p_149681_2_, p_149681_3_ + 1, p_149681_4_, Blocks.air, 0, 2);
        }

        super.func_149681_a(p_149681_1_, p_149681_2_, p_149681_3_, p_149681_4_, p_149681_5_, p_149681_6_);
    }

    private boolean func_149886_b(World p_149886_1_, int p_149886_2_, int p_149886_3_, int p_149886_4_, int p_149886_5_, EntityPlayer p_149886_6_)
    {
        int i1 = func_149890_d(p_149886_5_);

        if (i1 != 3 && i1 != 2)
        {
            return false;
        }
        else
        {
            p_149886_6_.addStat(StatList.mineBlockStatArray[Block.func_149682_b(this)], 1);
            byte b0 = 1;

            if (i1 == 3)
            {
                b0 = 2;
            }

            this.func_149642_a(p_149886_1_, p_149886_2_, p_149886_3_, p_149886_4_, new ItemStack(Blocks.tallgrass, 2, b0));
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149893_M = new IIcon[field_149892_a.length];
        this.field_149894_N = new IIcon[field_149892_a.length];

        for (int i = 0; i < this.field_149893_M.length; ++i)
        {
            this.field_149893_M[i] = p_149651_1_.registerIcon("double_plant_" + field_149892_a[i] + "_bottom");
            this.field_149894_N[i] = p_149651_1_.registerIcon("double_plant_" + field_149892_a[i] + "_top");
        }

        this.field_149891_b = new IIcon[2];
        this.field_149891_b[0] = p_149651_1_.registerIcon("double_plant_sunflower_front");
        this.field_149891_b[1] = p_149651_1_.registerIcon("double_plant_sunflower_back");
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        for (int i = 0; i < this.field_149893_M.length; ++i)
        {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
        }
    }

    public int func_149643_k(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        int l = p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
        return func_149887_c(l) ? func_149890_d(p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_ - 1, p_149643_4_)) : func_149890_d(l);
    }

    public boolean func_149851_a(World p_149851_1_, int p_149851_2_, int p_149851_3_, int p_149851_4_, boolean p_149851_5_)
    {
        int l = this.func_149885_e(p_149851_1_, p_149851_2_, p_149851_3_, p_149851_4_);
        return l != 2 && l != 3;
    }

    public boolean func_149852_a(World p_149852_1_, Random p_149852_2_, int p_149852_3_, int p_149852_4_, int p_149852_5_)
    {
        return true;
    }

    public void func_149853_b(World p_149853_1_, Random p_149853_2_, int p_149853_3_, int p_149853_4_, int p_149853_5_)
    {
        int l = this.func_149885_e(p_149853_1_, p_149853_3_, p_149853_4_, p_149853_5_);
        this.func_149642_a(p_149853_1_, p_149853_3_, p_149853_4_, p_149853_5_, new ItemStack(this, 1, l));
    }
}