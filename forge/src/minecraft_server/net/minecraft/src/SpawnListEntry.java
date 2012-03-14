package net.minecraft.src;

public class SpawnListEntry extends WeightedRandomChoice
{
    public Class entityClass;
    public int minGroupCount;
    public int maxGroupCount;

    public SpawnListEntry(Class par1Class, int par2, int par3, int par4)
    {
        super(par2);
        this.entityClass = par1Class;
        this.minGroupCount = par3;
        this.maxGroupCount = par4;
    }
}
