package net.minecraft.src;

public class WorldProviderEnd extends WorldProvider
{
    /**
     * creates a new world chunk manager for WorldProvider
     */
    public void registerWorldChunkManager()
    {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.5F, 0.0F);
        this.worldType = 1;
        this.hasNoSky = true;
    }

    /**
     * Returns the chunk provider back for the world provider
     */
    public IChunkProvider getChunkProvider()
    {
        return new ChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.0F;
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    public float[] calcSunriseSunsetColors(float par1, float par2)
    {
        return null;
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    public Vec3D getFogColor(float par1, float par2)
    {
        int var3 = 8421536;
        float var4 = MathHelper.cos(par1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (var4 < 0.0F)
        {
            var4 = 0.0F;
        }

        if (var4 > 1.0F)
        {
            var4 = 1.0F;
        }

        float var5 = (float)(var3 >> 16 & 255) / 255.0F;
        float var6 = (float)(var3 >> 8 & 255) / 255.0F;
        float var7 = (float)(var3 & 255) / 255.0F;
        var5 *= var4 * 0.0F + 0.15F;
        var6 *= var4 * 0.0F + 0.15F;
        var7 *= var4 * 0.0F + 0.15F;
        return Vec3D.createVector((double)var5, (double)var6, (double)var7);
    }

    public boolean isSkyColored()
    {
        return false;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return false;
    }

    public boolean func_48217_e()
    {
        return false;
    }

    /**
     * the y level at which clouds are rendered.
     */
    public float getCloudHeight()
    {
        return 8.0F;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        int var3 = this.worldObj.getFirstUncoveredBlock(par1, par2);
        return var3 == 0 ? false : Block.blocksList[var3].blockMaterial.blocksMovement();
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension
     */
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return new ChunkCoordinates(100, 50, 0);
    }

    public int getAverageGroundLevel()
    {
        return 50;
    }

    public boolean func_48218_b(int par1, int par2)
    {
        return true;
    }

    @Override
    public String getSaveFolder() 
    {
        return "DIM1";
    }

    @Override
    public String getWelcomeMessage() 
    {
        return "Entering the End";
    }

    @Override
    public String getDepartMessage() 
    {
        return "Leaving the End";
    }
}
