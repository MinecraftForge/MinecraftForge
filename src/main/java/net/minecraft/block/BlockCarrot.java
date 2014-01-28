package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockCarrot extends BlockCrops
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149868_a;
    private static final String __OBFID = "CL_00000212";

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        if (p_149691_2_ < 7)
        {
            if (p_149691_2_ == 6)
            {
                p_149691_2_ = 5;
            }

            return this.field_149868_a[p_149691_2_ >> 1];
        }
        else
        {
            return this.field_149868_a[3];
        }
    }

    protected Item func_149866_i()
    {
        return Items.carrot;
    }

    protected Item func_149865_P()
    {
        return Items.carrot;
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149868_a = new IIcon[4];

        for (int i = 0; i < this.field_149868_a.length; ++i)
        {
            this.field_149868_a[i] = p_149651_1_.registerIcon(this.func_149641_N() + "_stage_" + i);
        }
    }
}