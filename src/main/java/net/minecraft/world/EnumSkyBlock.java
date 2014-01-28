package net.minecraft.world;

public enum EnumSkyBlock
{
    Sky(15),
    Block(0);
    public final int defaultLightValue;

    private static final String __OBFID = "CL_00000151";

    private EnumSkyBlock(int par3)
    {
        this.defaultLightValue = par3;
    }
}