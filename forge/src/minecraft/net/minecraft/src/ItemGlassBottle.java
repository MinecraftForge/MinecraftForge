package net.minecraft.src;

public class ItemGlassBottle extends Item
{
    public ItemGlassBottle(int par1)
    {
        super(par1);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MovingObjectPosition var4 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

        if (var4 == null)
        {
            return par1ItemStack;
        }
        else
        {
            if (var4.typeOfHit == EnumMovingObjectType.TILE)
            {
                int var5 = var4.blockX;
                int var6 = var4.blockY;
                int var7 = var4.blockZ;

                if (!par2World.canMineBlock(par3EntityPlayer, var5, var6, var7))
                {
                    return par1ItemStack;
                }

                if (!par3EntityPlayer.canPlayerEdit(var5, var6, var7))
                {
                    return par1ItemStack;
                }

                if (par2World.getBlockMaterial(var5, var6, var7) == Material.water)
                {
                    --par1ItemStack.stackSize;

                    if (par1ItemStack.stackSize <= 0)
                    {
                        return new ItemStack(Item.potion);
                    }

                    if (!par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.potion)))
                    {
                        par3EntityPlayer.func_48153_a(new ItemStack(Item.potion.shiftedIndex, 1, 0));
                    }
                }
            }

            return par1ItemStack;
        }
    }
}
