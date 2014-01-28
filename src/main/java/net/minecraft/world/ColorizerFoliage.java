package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorizerFoliage
{
    // JAVADOC FIELD $$ field_77471_a
    private static int[] foliageBuffer = new int[65536];
    private static final String __OBFID = "CL_00000135";

    public static void setFoliageBiomeColorizer(int[] par0ArrayOfInteger)
    {
        foliageBuffer = par0ArrayOfInteger;
    }

    // JAVADOC METHOD $$ func_77470_a
    public static int getFoliageColor(double par0, double par2)
    {
        par2 *= par0;
        int i = (int)((1.0D - par0) * 255.0D);
        int j = (int)((1.0D - par2) * 255.0D);
        return foliageBuffer[j << 8 | i];
    }

    // JAVADOC METHOD $$ func_77466_a
    public static int getFoliageColorPine()
    {
        return 6396257;
    }

    // JAVADOC METHOD $$ func_77469_b
    public static int getFoliageColorBirch()
    {
        return 8431445;
    }

    public static int getFoliageColorBasic()
    {
        return 4764952;
    }
}