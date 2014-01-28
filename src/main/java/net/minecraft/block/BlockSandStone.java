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

public class BlockSandStone extends Block
{
    public static final String[] field_150157_a = new String[] {"default", "chiseled", "smooth"};
    private static final String[] field_150156_b = new String[] {"normal", "carved", "smooth"};
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150158_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_150159_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_150160_O;
    private static final String __OBFID = "CL_00000304";

    public BlockSandStone()
    {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        if (p_149691_1_ != 1 && (p_149691_1_ != 0 || p_149691_2_ != 1 && p_149691_2_ != 2))
        {
            if (p_149691_1_ == 0)
            {
                return this.field_150160_O;
            }
            else
            {
                if (p_149691_2_ < 0 || p_149691_2_ >= this.field_150158_M.length)
                {
                    p_149691_2_ = 0;
                }

                return this.field_150158_M[p_149691_2_];
            }
        }
        else
        {
            return this.field_150159_N;
        }
    }

    public int func_149692_a(int p_149692_1_)
    {
        return p_149692_1_;
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 2));
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_150158_M = new IIcon[field_150156_b.length];

        for (int i = 0; i < this.field_150158_M.length; ++i)
        {
            this.field_150158_M[i] = p_149651_1_.registerIcon(this.func_149641_N() + "_" + field_150156_b[i]);
        }

        this.field_150159_N = p_149651_1_.registerIcon(this.func_149641_N() + "_top");
        this.field_150160_O = p_149651_1_.registerIcon(this.func_149641_N() + "_bottom");
    }
}