package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockGlass extends BlockBreakable
{
    private static final String __OBFID = "CL_00000249";

    public BlockGlass(Material p_i45408_1_, boolean p_i45408_2_)
    {
        super("glass", p_i45408_1_, p_i45408_2_);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public int func_149701_w()
    {
        return 0;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    protected boolean func_149700_E()
    {
        return true;
    }
}