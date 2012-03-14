package net.minecraft.src;

public class ItemBed extends Item
{
    public ItemBed(int par1)
    {
        super(par1);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        if (par7 != 1)
        {
            return false;
        }
        else
        {
            ++par5;
            BlockBed var8 = (BlockBed)Block.bed;
            int var9 = MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            byte var10 = 0;
            byte var11 = 0;

            if (var9 == 0)
            {
                var11 = 1;
            }

            if (var9 == 1)
            {
                var10 = -1;
            }

            if (var9 == 2)
            {
                var11 = -1;
            }

            if (var9 == 3)
            {
                var10 = 1;
            }

            if (par2EntityPlayer.canPlayerEdit(par4, par5, par6) && par2EntityPlayer.canPlayerEdit(par4 + var10, par5, par6 + var11))
            {
                if (par3World.isAirBlock(par4, par5, par6) && par3World.isAirBlock(par4 + var10, par5, par6 + var11) && par3World.isBlockNormalCube(par4, par5 - 1, par6) && par3World.isBlockNormalCube(par4 + var10, par5 - 1, par6 + var11))
                {
                    par3World.setBlockAndMetadataWithNotify(par4, par5, par6, var8.blockID, var9);

                    if (par3World.getBlockId(par4, par5, par6) == var8.blockID)
                    {
                        par3World.setBlockAndMetadataWithNotify(par4 + var10, par5, par6 + var11, var8.blockID, var9 + 8);
                    }

                    --par1ItemStack.stackSize;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
