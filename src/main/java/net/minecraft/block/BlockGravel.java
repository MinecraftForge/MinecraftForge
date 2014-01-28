package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockGravel extends BlockFalling
{
    private static final String __OBFID = "CL_00000252";

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        if (p_149650_3_ > 3)
        {
            p_149650_3_ = 3;
        }

        return p_149650_2_.nextInt(10 - p_149650_3_ * 3) == 0 ? Items.flint : Item.func_150898_a(this);
    }
}