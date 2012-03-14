package net.minecraft.src;

public class ItemLeaves extends ItemBlock
{
    public ItemLeaves(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * returns the argument if the item has metadata, 0 otherwise
     */
    public int getMetadata(int par1)
    {
        return par1 | 4;
    }
}
