package net.minecraftforge.client;

public interface ILightingHandler
{
    public enum LightingType {
        Entity,
        Block,
    }

	/**
	 * Get's a value representing how important this value is.
	 * 
	 * Larger numbers represent higher priority and will execute later in the cycle.
	 * 0 is taken to mean natural world lighting.
	 * 
	 * @return A value representing a 
	 */
	public int getLightingPriority();

    /**
     * Gets the light level of a block in the world.
     * 
     * This is used to modify the ambient light level of a block, usually provided by the sky.
     *
     * @param x The x position of the block.
     * @param y The y position of the block.
     * @param z The z position of the block.
     * @param currentLight The current lighting level of the block.
     * @param type The type of object requesting the light level. Most mods won't care about this.
     * @return The light level of the block. Should be -1 if the mod doesn't wish to change this value.
     */
    public int getSkyLightLevel(int x, int y, int z, int currentLight, LightingType type);
	
    /**
     * Gets the light level of a block in the world.
     * 
     * This is used to modify the light level of a block as affected by things like torches.
     *
     * @param x The x position of the block.
     * @param y The y position of the block.
     * @param z The z position of the block.
     * @param currentLight The current lighting level of the block.
     * @param type The type of object requesting the light level. Most mods won't care about this.
     * @return The light level of the block. Should be -1 if the mod doesn't wish to change this value.
     */
    public int getBlockLightLevel(int x, int y, int z, int currentLight, LightingType type);
}
