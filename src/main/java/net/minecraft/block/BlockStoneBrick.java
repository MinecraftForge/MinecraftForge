package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneBrick extends Block
{
    public static final String[] field_150142_a = new String[] {"default", "mossy", "cracked", "chiseled"};
    public static final String[] field_150141_b = new String[] {null, "mossy", "cracked", "carved"};
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150143_M;
    private static final String __OBFID = "CL_00000318";

    public BlockStoneBrick()
    {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        if (p_149691_2_ < 0 || p_149691_2_ >= field_150141_b.length)
        {
            p_149691_2_ = 0;
        }

        return this.field_150143_M[p_149691_2_];
    }

    public int func_149692_a(int p_149692_1_)
    {
        return p_149692_1_;
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        for (int i = 0; i < 4; ++i)
        {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_150143_M = new IIcon[field_150141_b.length];

        for (int i = 0; i < this.field_150143_M.length; ++i)
        {
            String s = this.func_149641_N();

            if (field_150141_b[i] != null)
            {
                s = s + "_" + field_150141_b[i];
            }

            this.field_150143_M[i] = p_149651_1_.registerIcon(s);
        }
    }
}