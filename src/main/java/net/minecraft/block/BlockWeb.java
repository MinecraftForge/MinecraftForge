package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockWeb extends Block
{
    private static final String __OBFID = "CL_00000333";

    public BlockWeb()
    {
        super(Material.field_151569_G);
        this.func_149647_a(CreativeTabs.tabDecorations);
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        p_149670_5_.setInWeb();
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public int func_149645_b()
    {
        return 1;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.string;
    }

    protected boolean func_149700_E()
    {
        return true;
    }
}