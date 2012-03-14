package net.minecraft.src;

public class WorldProviderHell extends WorldProvider
{
    /**
     * creates a new world chunk manager for WorldProvider
     */
    public void registerWorldChunkManager()
    {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 1.0F, 0.0F);
        this.isHellWorld = true;
        this.hasNoSky = true;
        this.worldType = -1;
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    public Vec3D getFogColor(float par1, float par2)
    {
        return Vec3D.createVector(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
    }

    /**
     * Creates the light to brightness table
     */
    protected void generateLightBrightnessTable()
    {
        float var1 = 0.1F;

        for (int var2 = 0; var2 <= 15; ++var2)
        {
            float var3 = 1.0F - (float)var2 / 15.0F;
            this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
        }
    }

    /**
     * Returns the chunk provider back for the world provider
     */
    public IChunkProvider getChunkProvider()
    {
        return new ChunkProviderHell(this.worldObj, this.worldObj.getSeed());
    }

    public boolean func_48217_e()
    {
        return false;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        return false;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.5F;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return false;
    }

    public boolean func_48218_b(int par1, int par2)
    {
        return true;
    }

    @Override
    public String getSaveFolder() 
    {
        return "DIM-1";
    }

    @Override
    public String getWelcomeMessage() 
    {
        return "Entering the Nether";
    }

    @Override
    public String getDepartMessage() 
    {
        return "Leaving the Nether";
    }
    
    @Override
    public double getMovementFactor()
    {
        return 8.0;
    }
}
