package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends BlockCompressed
{
    private static final String __OBFID = "CL_00000287";

    public BlockCompressedPowered(MapColor p_i45416_1_)
    {
        super(p_i45416_1_);
        this.func_149647_a(CreativeTabs.tabRedstone);
    }

    public boolean func_149744_f()
    {
        return true;
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return 15;
    }
}