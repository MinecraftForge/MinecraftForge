package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockGlowstone extends Block
{
    private static final String __OBFID = "CL_00000250";

    public BlockGlowstone(Material p_i45409_1_)
    {
        super(p_i45409_1_);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    public int func_149679_a(int p_149679_1_, Random p_149679_2_)
    {
        return MathHelper.clamp_int(this.func_149745_a(p_149679_2_) + p_149679_2_.nextInt(p_149679_1_ + 1), 1, 4);
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 2 + p_149745_1_.nextInt(3);
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.glowstone_dust;
    }

    public MapColor func_149728_f(int p_149728_1_)
    {
        return MapColor.field_151658_d;
    }
}