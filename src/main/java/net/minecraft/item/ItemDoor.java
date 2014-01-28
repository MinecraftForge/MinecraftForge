package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDoor extends Item
{
    private Material doorMaterial;
    private static final String __OBFID = "CL_00000020";

    public ItemDoor(Material p_i45334_1_)
    {
        this.doorMaterial = p_i45334_1_;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 != 1)
        {
            return false;
        }
        else
        {
            ++par5;
            Block block;

            if (this.doorMaterial == Material.field_151575_d)
            {
                block = Blocks.wooden_door;
            }
            else
            {
                block = Blocks.iron_door;
            }

            if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack))
            {
                if (!block.func_149742_c(par3World, par4, par5, par6))
                {
                    return false;
                }
                else
                {
                    int i1 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                    func_150924_a(par3World, par4, par5, par6, i1, block);
                    --par1ItemStack.stackSize;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
    }

    public static void func_150924_a(World p_150924_0_, int p_150924_1_, int p_150924_2_, int p_150924_3_, int p_150924_4_, Block p_150924_5_)
    {
        byte b0 = 0;
        byte b1 = 0;

        if (p_150924_4_ == 0)
        {
            b1 = 1;
        }

        if (p_150924_4_ == 1)
        {
            b0 = -1;
        }

        if (p_150924_4_ == 2)
        {
            b1 = -1;
        }

        if (p_150924_4_ == 3)
        {
            b0 = 1;
        }

        int i1 = (p_150924_0_.func_147439_a(p_150924_1_ - b0, p_150924_2_, p_150924_3_ - b1).func_149721_r() ? 1 : 0) + (p_150924_0_.func_147439_a(p_150924_1_ - b0, p_150924_2_ + 1, p_150924_3_ - b1).func_149721_r() ? 1 : 0);
        int j1 = (p_150924_0_.func_147439_a(p_150924_1_ + b0, p_150924_2_, p_150924_3_ + b1).func_149721_r() ? 1 : 0) + (p_150924_0_.func_147439_a(p_150924_1_ + b0, p_150924_2_ + 1, p_150924_3_ + b1).func_149721_r() ? 1 : 0);
        boolean flag = p_150924_0_.func_147439_a(p_150924_1_ - b0, p_150924_2_, p_150924_3_ - b1) == p_150924_5_ || p_150924_0_.func_147439_a(p_150924_1_ - b0, p_150924_2_ + 1, p_150924_3_ - b1) == p_150924_5_;
        boolean flag1 = p_150924_0_.func_147439_a(p_150924_1_ + b0, p_150924_2_, p_150924_3_ + b1) == p_150924_5_ || p_150924_0_.func_147439_a(p_150924_1_ + b0, p_150924_2_ + 1, p_150924_3_ + b1) == p_150924_5_;
        boolean flag2 = false;

        if (flag && !flag1)
        {
            flag2 = true;
        }
        else if (j1 > i1)
        {
            flag2 = true;
        }

        p_150924_0_.func_147465_d(p_150924_1_, p_150924_2_, p_150924_3_, p_150924_5_, p_150924_4_, 2);
        p_150924_0_.func_147465_d(p_150924_1_, p_150924_2_ + 1, p_150924_3_, p_150924_5_, 8 | (flag2 ? 1 : 0), 2);
        p_150924_0_.func_147459_d(p_150924_1_, p_150924_2_, p_150924_3_, p_150924_5_);
        p_150924_0_.func_147459_d(p_150924_1_, p_150924_2_ + 1, p_150924_3_, p_150924_5_);
    }
}