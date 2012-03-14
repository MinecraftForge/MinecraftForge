package net.minecraft.src;

public class MaterialLiquid extends Material
{
    public MaterialLiquid(MapColor par1MapColor)
    {
        super(par1MapColor);
        this.setGroundCover();
        this.setNoPushMobility();
    }

    /**
     * Returns if blocks of these materials are liquids.
     */
    public boolean isLiquid()
    {
        return true;
    }

    /**
     * Returns if this material is considered solid or not
     */
    public boolean blocksMovement()
    {
        return false;
    }

    public boolean isSolid()
    {
        return false;
    }
}
