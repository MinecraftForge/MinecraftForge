package net.minecraft.src;

public class ItemMetadata extends ItemBlock
{
    private Block blockObj;

    public ItemMetadata(int par1, Block par2Block)
    {
        super(par1);
        this.blockObj = par2Block;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    public int getIconFromDamage(int par1)
    {
        return this.blockObj.getBlockTextureFromSideAndMetadata(2, par1);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(int par1)
    {
        return par1;
    }
}
