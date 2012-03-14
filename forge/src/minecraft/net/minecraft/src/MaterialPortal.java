package net.minecraft.src;

public class MaterialPortal extends Material
{
    public MaterialPortal(MapColor par1MapColor)
    {
        super(par1MapColor);
    }

    public boolean isSolid()
    {
        return false;
    }

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
     */
    public boolean getCanBlockGrass()
    {
        return false;
    }

    /**
     * Returns if this material is considered solid or not
     */
    public boolean blocksMovement()
    {
        return false;
    }
}
