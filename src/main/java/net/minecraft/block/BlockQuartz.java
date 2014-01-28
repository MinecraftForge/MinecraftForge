package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockQuartz extends Block
{
    public static final String[] field_150191_a = new String[] {"default", "chiseled", "lines"};
    private static final String[] field_150189_b = new String[] {"side", "chiseled", "lines", null, null};
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150192_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_150193_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_150194_O;
    @SideOnly(Side.CLIENT)
    private IIcon field_150190_P;
    @SideOnly(Side.CLIENT)
    private IIcon field_150188_Q;
    private static final String __OBFID = "CL_00000292";

    public BlockQuartz()
    {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        if (p_149691_2_ != 2 && p_149691_2_ != 3 && p_149691_2_ != 4)
        {
            if (p_149691_1_ != 1 && (p_149691_1_ != 0 || p_149691_2_ != 1))
            {
                if (p_149691_1_ == 0)
                {
                    return this.field_150188_Q;
                }
                else
                {
                    if (p_149691_2_ < 0 || p_149691_2_ >= this.field_150192_M.length)
                    {
                        p_149691_2_ = 0;
                    }

                    return this.field_150192_M[p_149691_2_];
                }
            }
            else
            {
                return p_149691_2_ == 1 ? this.field_150193_N : this.field_150190_P;
            }
        }
        else
        {
            return p_149691_2_ == 2 && (p_149691_1_ == 1 || p_149691_1_ == 0) ? this.field_150194_O : (p_149691_2_ == 3 && (p_149691_1_ == 5 || p_149691_1_ == 4) ? this.field_150194_O : (p_149691_2_ == 4 && (p_149691_1_ == 2 || p_149691_1_ == 3) ? this.field_150194_O : this.field_150192_M[p_149691_2_]));
        }
    }

    public int func_149660_a(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        if (p_149660_9_ == 2)
        {
            switch (p_149660_5_)
            {
                case 0:
                case 1:
                    p_149660_9_ = 2;
                    break;
                case 2:
                case 3:
                    p_149660_9_ = 4;
                    break;
                case 4:
                case 5:
                    p_149660_9_ = 3;
            }
        }

        return p_149660_9_;
    }

    public int func_149692_a(int p_149692_1_)
    {
        return p_149692_1_ != 3 && p_149692_1_ != 4 ? p_149692_1_ : 2;
    }

    protected ItemStack func_149644_j(int p_149644_1_)
    {
        return p_149644_1_ != 3 && p_149644_1_ != 4 ? super.func_149644_j(p_149644_1_) : new ItemStack(Item.func_150898_a(this), 1, 2);
    }

    public int func_149645_b()
    {
        return 39;
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
        this.field_150192_M = new IIcon[field_150189_b.length];

        for (int i = 0; i < this.field_150192_M.length; ++i)
        {
            if (field_150189_b[i] == null)
            {
                this.field_150192_M[i] = this.field_150192_M[i - 1];
            }
            else
            {
                this.field_150192_M[i] = p_149651_1_.registerIcon(this.func_149641_N() + "_" + field_150189_b[i]);
            }
        }

        this.field_150190_P = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "top");
        this.field_150193_N = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "chiseled_top");
        this.field_150194_O = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "lines_top");
        this.field_150188_Q = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "bottom");
    }

    public MapColor func_149728_f(int p_149728_1_)
    {
        return MapColor.field_151677_p;
    }
}