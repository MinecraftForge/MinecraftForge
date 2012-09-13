package net.minecraftforge.client;

public interface IBlockLightingHandler
{
    /**
     * Gets the light level of a block in the world.
     * This is used to modify the light level of a block. Forge will
     * worry about making sure that the light level is the most appropriate
     * of any returned values as well as what Minecraft itself believes
     * the value should be.
     *
     * This hook is only a visual change. This will not affect mechanics
     * such as mob spawning.
     *
     * @param x The x position of the block
     * @param y The y position of the block
     * @param z The z position of the block
     * @return int The light level of the block. Should be 0 if the mod doesn't care.
     */
    public int getBlockLightLevel(int x, int y, int z);
}
