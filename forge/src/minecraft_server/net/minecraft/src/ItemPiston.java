package net.minecraft.src;

public class ItemPiston extends ItemBlock
{
    public ItemPiston(int par1)
    {
        super(par1);
    }

    /**
     * returns the argument if the item has metadata, 0 otherwise
     */
    public int getMetadata(int par1)
    {
        return 7;
    }
}
