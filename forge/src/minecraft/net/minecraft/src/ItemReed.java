package net.minecraft.src;

public class ItemReed extends Item
{
    /** The ID of the block the reed will spawn when used from inventory bar. */
    private int spawnID;

    public ItemReed(int par1, Block par2Block)
    {
        super(par1);
        this.spawnID = par2Block.blockID;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        int var8 = par3World.getBlockId(par4, par5, par6);

        if (var8 == Block.snow.blockID)
        {
            par7 = 1;
        }
        else if (var8 != Block.vine.blockID && var8 != Block.tallGrass.blockID && var8 != Block.deadBush.blockID)
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

        if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6))
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (par3World.canBlockBePlacedAt(this.spawnID, par4, par5, par6, false, par7))
            {
                Block var9 = Block.blocksList[this.spawnID];

                if (par3World.setBlockWithNotify(par4, par5, par6, this.spawnID))
                {
                    if (par3World.getBlockId(par4, par5, par6) == this.spawnID)
                    {
                        Block.blocksList[this.spawnID].onBlockPlaced(par3World, par4, par5, par6, par7);
                        Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, par4, par5, par6, par2EntityPlayer);
                    }

                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), var9.stepSound.getStepSound(), (var9.stepSound.getVolume() + 1.0F) / 2.0F, var9.stepSound.getPitch() * 0.8F);
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }
}
