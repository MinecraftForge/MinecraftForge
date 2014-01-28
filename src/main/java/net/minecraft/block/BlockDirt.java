package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block
{
    public static final String[] field_150009_a = new String[] {"default", "default", "podzol"};
    @SideOnly(Side.CLIENT)
    private IIcon field_150008_b;
    @SideOnly(Side.CLIENT)
    private IIcon field_150010_M;
    private static final String __OBFID = "CL_00000228";

    protected BlockDirt()
    {
        super(Material.field_151578_c);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        if (p_149691_2_ == 2)
        {
            if (p_149691_1_ == 1)
            {
                return this.field_150008_b;
            }

            if (p_149691_1_ != 0)
            {
                return this.field_150010_M;
            }
        }

        return this.field_149761_L;
    }

    public int func_149692_a(int p_149692_1_)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149673_e(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_)
    {
        int i1 = p_149673_1_.getBlockMetadata(p_149673_2_, p_149673_3_, p_149673_4_);

        if (i1 == 2)
        {
            if (p_149673_5_ == 1)
            {
                return this.field_150008_b;
            }

            if (p_149673_5_ != 0)
            {
                Material material = p_149673_1_.func_147439_a(p_149673_2_, p_149673_3_ + 1, p_149673_4_).func_149688_o();

                if (material == Material.field_151597_y || material == Material.field_151596_z)
                {
                    return Blocks.grass.func_149673_e(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_, p_149673_5_);
                }

                Block block = p_149673_1_.func_147439_a(p_149673_2_, p_149673_3_ + 1, p_149673_4_);

                if (block != Blocks.dirt && block != Blocks.grass)
                {
                    return this.field_150010_M;
                }
            }
        }

        return this.field_149761_L;
    }

    protected ItemStack func_149644_j(int p_149644_1_)
    {
        if (p_149644_1_ == 1)
        {
            p_149644_1_ = 0;
        }

        return super.func_149644_j(p_149644_1_);
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        p_149666_3_.add(new ItemStack(this, 1, 0));
        p_149666_3_.add(new ItemStack(this, 1, 2));
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        super.func_149651_a(p_149651_1_);
        this.field_150008_b = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "podzol_top");
        this.field_150010_M = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "podzol_side");
    }

    public int func_149643_k(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        int l = p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);

        if (l == 1)
        {
            l = 0;
        }

        return l;
    }
}