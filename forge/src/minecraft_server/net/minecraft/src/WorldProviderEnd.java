package net.minecraft.src;

public class WorldProviderEnd extends WorldProvider
{
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

    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.0F;
    }

    public boolean canRespawnHere()
    {
        return false;
    }

    public boolean func_48567_d()
    {
        return false;
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
