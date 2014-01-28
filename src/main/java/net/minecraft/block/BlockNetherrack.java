package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockNetherrack extends Block
{
    private static final String __OBFID = "CL_00000275";

    public BlockNetherrack()
    {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    public MapColor func_149728_f(int p_149728_1_)
    {
        return MapColor.field_151655_K;
    }
}