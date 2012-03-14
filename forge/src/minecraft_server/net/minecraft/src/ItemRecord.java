package net.minecraft.src;

public class ItemRecord extends Item
{
    /** The name of the record. */
    public final String recordName;

    protected ItemRecord(int par1, String par2Str)
    {
        super(par1);
        this.recordName = par2Str;
        this.maxStackSize = 1;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        if (par3World.getBlockId(par4, par5, par6) == Block.jukebox.blockID && par3World.getBlockMetadata(par4, par5, par6) == 0)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                ((BlockJukeBox)Block.jukebox).insertRecord(par3World, par4, par5, par6, this.shiftedIndex);
                par3World.playAuxSFXAtEntity((EntityPlayer)null, 1005, par4, par5, par6, this.shiftedIndex);
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
