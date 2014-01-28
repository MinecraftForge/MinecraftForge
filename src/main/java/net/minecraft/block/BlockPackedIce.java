package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockPackedIce extends Block
{
    private static final String __OBFID = "CL_00000283";

    public BlockPackedIce()
    {
        super(Material.field_151598_x);
        this.field_149765_K = 0.98F;
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }
}