package net.minecraft.block.material;

public class MaterialPortal extends Material
{
    private static final String __OBFID = "CL_00000545";

    public MaterialPortal(MapColor par1MapColor)
    {
        super(par1MapColor);
    }

    public boolean isSolid()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76228_b
    public boolean getCanBlockGrass()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76230_c
    public boolean blocksMovement()
    {
        return false;
    }
}