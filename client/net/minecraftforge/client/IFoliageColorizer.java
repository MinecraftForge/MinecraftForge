package net.minecraftforge.client;

/**
 * Called from ColorizerFoliage when rendering grass blocks.
 * 
 * @author Grenadier
 */
public interface IFoliageColorizer
{
    /**
     * Calculates the foliage color for blocks based on their biome data.
     * 
     * @param temperature
     *            The temperature of the biome.
     * @param rainfall
     *            The rainfall of the biome.
     * @param foliageBuffer
     *            The default gradient image loaded into ColorizerFoliage, as a
     *            1D array.
     * @return The foliage color to use for rendering, as an ARGB int (though A
     *         is ignored).
     */
    public int colorizeFoliage(double temperature, double rainfall, int[] foliageBuffer);

    /**
     * Gets the foliage color for spruce trees.
     * 
     * @return The foliage color to use for rendering, as an ARGB int (though A
     *         is ignored).
     */
    public int colorizePineFoliage();

    /**
     * Gets the foliage color for birch trees.
     * 
     * @return The foliage color to use for rendering, as an ARGB int (though A
     *         is ignored).
     */
    public int colorizeBirchFoliage();

    /**
     * Get's the foliage color for oak trees.
     * 
     * @return The foliage color to use for rendering, as an ARGB int (though A
     *         is ignored).
     */
    public int colorizeBasicFoliage();
}