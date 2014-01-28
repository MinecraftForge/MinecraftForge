package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockAir extends Block
{
    private static final String __OBFID = "CL_00000190";

    protected BlockAir()
    {
        super(Material.field_151579_a);
    }

    public int func_149645_b()
    {
        return -1;
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149678_a(int p_149678_1_, boolean p_149678_2_)
    {
        return false;
    }

    public void func_149690_a(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_) {}
}