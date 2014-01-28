package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockColored extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150033_a;
    private static final String __OBFID = "CL_00000217";

    public BlockColored(Material p_i45398_1_)
    {
        super(p_i45398_1_);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return this.field_150033_a[p_149691_2_ % this.field_150033_a.length];
    }

    public int func_149692_a(int p_149692_1_)
    {
        return p_149692_1_;
    }

    public static int func_150032_b(int p_150032_0_)
    {
        return func_150031_c(p_150032_0_);
    }

    public static int func_150031_c(int p_150031_0_)
    {
        return ~p_150031_0_ & 15;
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        for (int i = 0; i < 16; ++i)
        {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_150033_a = new IIcon[16];

        for (int i = 0; i < this.field_150033_a.length; ++i)
        {
            this.field_150033_a[i] = p_149651_1_.registerIcon(this.func_149641_N() + "_" + ItemDye.field_150921_b[func_150031_c(i)]);
        }
    }

    public MapColor func_149728_f(int p_149728_1_)
    {
        return MapColor.func_151644_a(p_149728_1_);
    }
}