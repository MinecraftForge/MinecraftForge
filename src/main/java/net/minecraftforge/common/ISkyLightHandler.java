package net.minecraftforge.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISkyLightHandler {
    /**
     * Calculates Sun Brightness.
     * Client Side
     * */
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1);
    
    /**
     * Calculates Star Brightness.
     * Client Side
     * */
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1);
    
    /**
     * Calculates Sky Light, for calculating block light or check if it is Daytime/
     * Both Side
     * */
    public int calculateSkylight(float par1);
}
