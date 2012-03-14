package net.minecraft.src;

public class ItemColored extends ItemBlock
{
    private final Block blockRef;
    private String[] blockNames;

    public ItemColored(int par1, boolean par2)
    {
        super(par1);
        this.blockRef = Block.blocksList[this.getBlockID()];

        if (par2)
        {
            this.setMaxDamage(0);
            this.setHasSubtypes(true);
        }
    }

    public int getColorFromDamage(int par1, int par2)
    {
        return this.blockRef.getRenderColor(par1);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    public int getIconFromDamage(int par1)
    {
        return this.blockRef.getBlockTextureFromSideAndMetadata(0, par1);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(int par1)
    {
        return par1;
    }

    /**
     * sets the array of strings to be used for name lookups from item damage to metadata
     */
    public ItemColored setBlockNames(String[] par1ArrayOfStr)
    {
        this.blockNames = par1ArrayOfStr;
        return this;
    }

    public String getItemNameIS(ItemStack par1ItemStack)
    {
        if (this.blockNames == null)
        {
            return super.getItemNameIS(par1ItemStack);
        }
        else
        {
            int var2 = par1ItemStack.getItemDamage();
            return var2 >= 0 && var2 < this.blockNames.length ? super.getItemNameIS(par1ItemStack) + "." + this.blockNames[var2] : super.getItemNameIS(par1ItemStack);
        }
    }
}
