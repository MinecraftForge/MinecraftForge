package net.minecraftforge.client;

/**
 * Called from ColorizerGrass when rendering grass blocks.
 * 
 * @author Grenadier
 */
public interface IGrassColorizer
{
    /**
     * Calculates the grass color for blocks based on their biome data.
     * 
     * @param temperature
     *            The temperature of the biome.
     * @param rainfall
     *            The rainfall of the biome.
     * @param grassBuffer
     *            The default gradient image loaded into ColorizerGrass, as a 1D
     *            array.
     * @return The grass color to use for rendering, as an ARGB int (though A is
     *         ignored).
     */
    public int colorizeGrass(double temperature, double rainfall, int[] grassBuffer);
}