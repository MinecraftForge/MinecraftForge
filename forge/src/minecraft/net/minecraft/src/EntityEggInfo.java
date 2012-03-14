package net.minecraft.src;

public class EntityEggInfo
{
    /** The entityID of the spawned mob */
    public int spawnedID;

    /** Base color of the egg */
    public int primaryColor;

    /** Color of the egg spots */
    public int secondaryColor;

    public EntityEggInfo(int par1, int par2, int par3)
    {
        this.spawnedID = par1;
        this.primaryColor = par2;
        this.secondaryColor = par3;
    }
}
