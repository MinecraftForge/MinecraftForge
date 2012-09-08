package net.minecraftforge.client;

/**
 * Called from ColorizerFoliage when rendering grass blocks.
 * 
 * @author Grenadier
 */
public interface IFoliageColorizer
{
    /**
     * Calculates the grass color for blocks based on their biome data.
     * 
     * @param par0
     *            The temperature of the biome.
     * @param par2
     *            The humidity of the biome.
     * @param foliageBuffer
     *            The default gradient image loaded into ColorizerFoliage, as a
     *            1D array.
     * @return The foliage color to use for rendering.
     */
    public int colorizeFoliage(double temperature, double humidity, int[] foliageBuffer);

    /**
     * Gets the foliage color for spruce trees.
     */
    public int colorizePineFoliage();

    /**
     * Gets the foliage color for birch trees.
     */
    public int colorizeBirchFoliage();

    /**
     * Get's the foliage color for oak trees.
     * 
     * @return An ARGB int, where the alpha channel is ignored.
     */
    public int colorizeBasicFoliage();
}