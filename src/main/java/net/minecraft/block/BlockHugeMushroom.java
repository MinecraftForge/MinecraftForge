package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block
{
    private static final String[] field_149793_a = new String[] {"skin_brown", "skin_red"};
    private final int field_149792_b;
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149794_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_149795_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_149796_O;
    private static final String __OBFID = "CL_00000258";

    public BlockHugeMushroom(Material p_i45412_1_, int p_i45412_2_)
    {
        super(p_i45412_1_);
        this.field_149792_b = p_i45412_2_;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_2_ == 10 && p_149691_1_ > 1 ? this.field_149795_N : (p_149691_2_ >= 1 && p_149691_2_ <= 9 && p_149691_1_ == 1 ? this.field_149794_M[this.field_149792_b] : (p_149691_2_ >= 1 && p_149691_2_ <= 3 && p_149691_1_ == 2 ? this.field_149794_M[this.field_149792_b] : (p_149691_2_ >= 7 && p_149691_2_ <= 9 && p_149691_1_ == 3 ? this.field_149794_M[this.field_149792_b] : ((p_149691_2_ == 1 || p_149691_2_ == 4 || p_149691_2_ == 7) && p_149691_1_ == 4 ? this.field_149794_M[this.field_149792_b] : ((p_149691_2_ == 3 || p_149691_2_ == 6 || p_149691_2_ == 9) && p_149691_1_ == 5 ? this.field_149794_M[this.field_149792_b] : (p_149691_2_ == 14 ? this.field_149794_M[this.field_149792_b] : (p_149691_2_ == 15 ? this.field_149795_N : this.field_149796_O)))))));
    }

    public int func_149745_a(Random p_149745_1_)
    {
        int i = p_149745_1_.nextInt(10) - 7;

        if (i < 0)
        {
            i = 0;
        }

        return i;
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.func_150899_d(Block.func_149682_b(Blocks.brown_mushroom) + this.field_149792_b);
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.func_150899_d(Block.func_149682_b(Blocks.brown_mushroom) + this.field_149792_b);
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149794_M = new IIcon[field_149793_a.length];

        for (int i = 0; i < this.field_149794_M.length; ++i)
        {
            this.field_149794_M[i] = p_149651_1_.registerIcon(this.func_149641_N() + "_" + field_149793_a[i]);
        }

        this.field_149796_O = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "inside");
        this.field_149795_N = p_149651_1_.registerIcon(this.func_149641_N() + "_" + "skin_stem");
    }
}