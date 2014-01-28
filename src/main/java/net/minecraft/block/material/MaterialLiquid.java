package net.minecraft.block.material;

public class MaterialLiquid extends Material
{
    private static final String __OBFID = "CL_00000541";

    public MaterialLiquid(MapColor par1MapColor)
    {
        super(par1MapColor);
        this.setReplaceable();
        this.setNoPushMobility();
    }

    // JAVADOC METHOD $$ func_76224_d
    public boolean isLiquid()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_76230_c
    public boolean blocksMovement()
    {
        return false;
    }

    public boolean isSolid()
    {
        return false;
    }
}