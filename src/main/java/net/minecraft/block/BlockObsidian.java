package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class BlockObsidian extends BlockStone
{
    private static final String __OBFID = "CL_00000279";

    public int func_149745_a(Random p_149745_1_)
    {
        return 1;
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.func_150898_a(Blocks.obsidian);
    }

    public MapColor func_149728_f(int p_149728_1_)
    {
        return MapColor.field_151654_J;
    }
}