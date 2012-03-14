package net.minecraft.src;

public class ColorizerWater
{
    private static int[] waterBuffer = new int[65536];

    public static void setWaterBiomeColorizer(int[] par0ArrayOfInteger)
    {
        waterBuffer = par0ArrayOfInteger;
    }
}
