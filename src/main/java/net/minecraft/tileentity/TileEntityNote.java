package net.minecraft.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity
{
    public byte field_145879_a;
    public boolean field_145880_i;
    private static final String __OBFID = "CL_00000362";

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setByte("note", this.field_145879_a);
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        this.field_145879_a = p_145839_1_.getByte("note");

        if (this.field_145879_a < 0)
        {
            this.field_145879_a = 0;
        }

        if (this.field_145879_a > 24)
        {
            this.field_145879_a = 24;
        }
    }

    public void func_145877_a()
    {
        this.field_145879_a = (byte)((this.field_145879_a + 1) % 25);
        this.onInventoryChanged();
    }

    public void func_145878_a(World p_145878_1_, int p_145878_2_, int p_145878_3_, int p_145878_4_)
    {
        if (p_145878_1_.func_147439_a(p_145878_2_, p_145878_3_ + 1, p_145878_4_).func_149688_o() == Material.field_151579_a)
        {
            Material material = p_145878_1_.func_147439_a(p_145878_2_, p_145878_3_ - 1, p_145878_4_).func_149688_o();
            byte b0 = 0;

            if (material == Material.field_151576_e)
            {
                b0 = 1;
            }

            if (material == Material.field_151595_p)
            {
                b0 = 2;
            }

            if (material == Material.field_151592_s)
            {
                b0 = 3;
            }

            if (material == Material.field_151575_d)
            {
                b0 = 4;
            }

            p_145878_1_.func_147452_c(p_145878_2_, p_145878_3_, p_145878_4_, Blocks.noteblock, b0, this.field_145879_a);
        }
    }
}