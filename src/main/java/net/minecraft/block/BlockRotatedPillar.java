package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockRotatedPillar extends Block
{
    @SideOnly(Side.CLIENT)
    protected IIcon field_150164_N;
    private static final String __OBFID = "CL_00000302";

    protected BlockRotatedPillar(Material p_i45425_1_)
    {
        super(p_i45425_1_);
    }

    public int func_149645_b()
    {
        return 31;
    }

    public int func_149660_a(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        int j1 = p_149660_9_ & 3;
        byte b0 = 0;

        switch (p_149660_5_)
        {
            case 0:
            case 1:
                b0 = 0;
                break;
            case 2:
            case 3:
                b0 = 8;
                break;
            case 4:
            case 5:
                b0 = 4;
        }

        return j1 | b0;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        int k = p_149691_2_ & 12;
        int l = p_149691_2_ & 3;
        return k == 0 && (p_149691_1_ == 1 || p_149691_1_ == 0) ? this.func_150161_d(l) : (k == 4 && (p_149691_1_ == 5 || p_149691_1_ == 4) ? this.func_150161_d(l) : (k == 8 && (p_149691_1_ == 2 || p_149691_1_ == 3) ? this.func_150161_d(l) : this.func_150163_b(l)));
    }

    public int func_149692_a(int p_149692_1_)
    {
        return p_149692_1_ & 3;
    }

    @SideOnly(Side.CLIENT)
    protected abstract IIcon func_150163_b(int var1);

    @SideOnly(Side.CLIENT)
    protected IIcon func_150161_d(int p_150161_1_)
    {
        return this.field_150164_N;
    }

    public int func_150162_k(int p_150162_1_)
    {
        return p_150162_1_ & 3;
    }

    protected ItemStack func_149644_j(int p_149644_1_)
    {
        return new ItemStack(Item.func_150898_a(this), 1, this.func_150162_k(p_149644_1_));
    }
}