package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer
{
    private static final String __OBFID = "CL_00000278";

    public BlockNote()
    {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.tabRedstone);
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        boolean flag = p_149695_1_.isBlockIndirectlyGettingPowered(p_149695_2_, p_149695_3_, p_149695_4_);
        TileEntityNote tileentitynote = (TileEntityNote)p_149695_1_.func_147438_o(p_149695_2_, p_149695_3_, p_149695_4_);

        if (tileentitynote != null && tileentitynote.field_145880_i != flag)
        {
            if (flag)
            {
                tileentitynote.func_145878_a(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
            }

            tileentitynote.field_145880_i = flag;
        }
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (p_149727_1_.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityNote tileentitynote = (TileEntityNote)p_149727_1_.func_147438_o(p_149727_2_, p_149727_3_, p_149727_4_);

            if (tileentitynote != null)
            {
                tileentitynote.func_145877_a();
                tileentitynote.func_145878_a(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_);
            }

            return true;
        }
    }

    public void func_149699_a(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_)
    {
        if (!p_149699_1_.isRemote)
        {
            TileEntityNote tileentitynote = (TileEntityNote)p_149699_1_.func_147438_o(p_149699_2_, p_149699_3_, p_149699_4_);

            if (tileentitynote != null)
            {
                tileentitynote.func_145878_a(p_149699_1_, p_149699_2_, p_149699_3_, p_149699_4_);
            }
        }
    }

    public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityNote();
    }

    public boolean func_149696_a(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_)
    {
        float f = (float)Math.pow(2.0D, (double)(p_149696_6_ - 12) / 12.0D);
        String s = "harp";

        if (p_149696_5_ == 1)
        {
            s = "bd";
        }

        if (p_149696_5_ == 2)
        {
            s = "snare";
        }

        if (p_149696_5_ == 3)
        {
            s = "hat";
        }

        if (p_149696_5_ == 4)
        {
            s = "bassattack";
        }

        p_149696_1_.playSoundEffect((double)p_149696_2_ + 0.5D, (double)p_149696_3_ + 0.5D, (double)p_149696_4_ + 0.5D, "note." + s, 3.0F, f);
        p_149696_1_.spawnParticle("note", (double)p_149696_2_ + 0.5D, (double)p_149696_3_ + 1.2D, (double)p_149696_4_ + 0.5D, (double)p_149696_6_ / 24.0D, 0.0D, 0.0D);
        return true;
    }
}