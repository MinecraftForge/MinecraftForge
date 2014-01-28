package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item
{
    private static final String __OBFID = "CL_00000064";

    public ItemSign()
    {
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 == 0)
        {
            return false;
        }
        else if (!par3World.func_147439_a(par4, par5, par6).func_149688_o().isSolid())
        {
            return false;
        }
        else
        {
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

            if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
            {
                return false;
            }
            else if (!Blocks.standing_sign.func_149742_c(par3World, par4, par5, par6))
            {
                return false;
            }
            else if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                if (par7 == 1)
                {
                    int i1 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    par3World.func_147465_d(par4, par5, par6, Blocks.standing_sign, i1, 3);
                }
                else
                {
                    par3World.func_147465_d(par4, par5, par6, Blocks.wall_sign, par7, 3);
                }

                --par1ItemStack.stackSize;
                TileEntitySign tileentitysign = (TileEntitySign)par3World.func_147438_o(par4, par5, par6);

                if (tileentitysign != null)
                {
                    par2EntityPlayer.func_146100_a(tileentitysign);
                }

                return true;
            }
        }
    }
}