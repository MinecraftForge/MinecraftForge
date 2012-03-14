package net.minecraft.src;

public class StructurePieceTreasure extends WeightedRandomChoice
{
    /** The ID for this treasure item */
    public int itemID;

    /** The metadata to be used when creating the treasure item. */
    public int itemMetadata;

    /** This is how many items can be in each stack at minimun */
    public int minItemStack;

    /** This is how many items can be max in the itemstack */
    public int maxItemStack;

    public StructurePieceTreasure(int par1, int par2, int par3, int par4, int par5)
    {
        super(par5);
        this.itemID = par1;
        this.itemMetadata = par2;
        this.minItemStack = par3;
        this.maxItemStack = par4;
    }
}
