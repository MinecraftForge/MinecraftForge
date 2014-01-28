package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemReed extends Item
{
    private Block field_150935_a;
    private static final String __OBFID = "CL_00001773";

    public ItemReed(Block p_i45329_1_)
    {
        this.field_150935_a = p_i45329_1_;
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block block = par3World.func_147439_a(par4, par5, par6);

        if (block == Blocks.snow_layer && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush)
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (par3World.func_147472_a(this.field_150935_a, par4, par5, par6, false, par7, (Entity)null, par1ItemStack))
            {
                int i1 = this.field_150935_a.func_149660_a(par3World, par4, par5, par6, par7, par8, par9, par10, 0);

                if (par3World.func_147465_d(par4, par5, par6, this.field_150935_a, i1, 3))
                {
                    if (par3World.func_147439_a(par4, par5, par6) == this.field_150935_a)
                    {
                        this.field_150935_a.func_149689_a(par3World, par4, par5, par6, par2EntityPlayer, par1ItemStack);
                        this.field_150935_a.func_149714_e(par3World, par4, par5, par6, i1);
                    }

                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.field_150935_a.field_149762_H.func_150496_b(), (this.field_150935_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150935_a.field_149762_H.func_150494_d() * 0.8F);
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }
}