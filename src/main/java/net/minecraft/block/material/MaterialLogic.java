package net.minecraft.block.material;

public class MaterialLogic extends Material
{
    private static final String __OBFID = "CL_00000539";

    public MaterialLogic(MapColor par1MapColor)
    {
        super(par1MapColor);
        this.setAdventureModeExempt();
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