package net.minecraft.block.material;

public class MaterialTransparent extends Material
{
    private static final String __OBFID = "CL_00000540";

    public MaterialTransparent(MapColor par1MapColor)
    {
        super(par1MapColor);
        this.setReplaceable();
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