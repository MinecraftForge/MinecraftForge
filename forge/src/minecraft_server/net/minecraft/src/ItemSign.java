package net.minecraft.src;

public class ItemSign extends Item
{
    public ItemSign(int par1)
    {
        super(par1);
        this.maxStackSize = 1;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        if (par7 == 0)
        {
            return false;
        }
        else if (!par3World.getBlockMaterial(par4, par5, par6).isSolid())
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

            if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6))
            {
                return false;
            }
            else if (!Block.signPost.canPlaceBlockAt(par3World, par4, par5, par6))
            {
                return false;
            }
            else
            {
                if (par7 == 1)
                {
                    int var8 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    par3World.setBlockAndMetadataWithNotify(par4, par5, par6, Block.signPost.blockID, var8);
                }
                else
                {
                    par3World.setBlockAndMetadataWithNotify(par4, par5, par6, Block.signWall.blockID, par7);
                }

                --par1ItemStack.stackSize;
                TileEntitySign var9 = (TileEntitySign)par3World.getBlockTileEntity(par4, par5, par6);

                if (var9 != null)
                {
                    par2EntityPlayer.displayGUIEditSign(var9);
                }

                return true;
            }
        }
    }
}
