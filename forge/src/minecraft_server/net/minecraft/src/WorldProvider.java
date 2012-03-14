package net.minecraft.src;

import net.minecraft.src.forge.DimensionManager;

public abstract class WorldProvider
{
    public World worldObj;
    public WorldType terrainType;
    public WorldChunkManager worldChunkMgr;

    /**
     * States whether the Hell world provider is used(true) or if the normal world provider is used(false)
     */
    public boolean isHellWorld = false;
    public boolean hasNoSky = false;

    /** Light to brightness conversion table */
    public float[] lightBrightnessTable = new float[16];

    /** 0 for normal world -1 for hell */
    public int worldType = 0;
    private float[] colorsSunriseSunset = new float[4];

    public final void registerWorld(World par1World)
    {
        this.worldObj = par1World;
        this.terrainType = par1World.getWorldInfo().getTerrainType();
        this.registerWorldChunkManager();
        this.generateLightBrightnessTable();
    }

    /**
     * Creates the light to brightness table
     */
    protected void generateLightBrightnessTable()
    {
        float var1 = 0.0F;

        for (int var2 = 0; var2 <= 15; ++var2)
        {
            float var3 = 1.0F - (float)var2 / 15.0F;
            this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
        }
    }

    protected void registerWorldChunkManager()
    {
        if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.field_48458_c)
        {
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F, 0.5F);
        }
        else
        {
            this.worldChunkMgr = new WorldChunkManager(this.worldObj);
        }
    }

    /**
     * Returns the chunk provider back for the world provider
     */
    public IChunkProvider getChunkProvider()
    {
        return (IChunkProvider)(this.terrainType == WorldType.field_48458_c ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled()) : new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled()));
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        int var3 = this.worldObj.getFirstUncoveredBlock(par1, par2);
        return var3 == Block.grass.blockID;
    }

    public float calculateCelestialAngle(long par1, float par3)
    {
        int var4 = (int)(par1 % 24000L);
        float var5 = ((float)var4 + par3) / 24000.0F - 0.25F;

        if (var5 < 0.0F)
        {
            ++var5;
        }

        if (var5 > 1.0F)
        {
            --var5;
        }

        float var6 = var5;
        var5 = 1.0F - (float)((Math.cos((double)var5 * Math.PI) + 1.0D) / 2.0D);
        var5 = var6 + (var5 - var6) / 3.0F;
        return var5;
    }

    public boolean func_48567_d()
    {
        return true;
    }

    public boolean canRespawnHere()
    {
        return true;
    }

    public static WorldProvider getProviderForDimension(int par0)
    {
        return DimensionManager.getProvider(par0);
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension
     */
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return null;
    }

    public int getAverageGroundLevel()
    {
        return this.terrainType == WorldType.field_48458_c ? 4 : 64;
    }
    
    /**
     * Returns the sub-folder of the world folder that this WorldProvider saves to.
     * EXA: DIM1, DIM-1
     * @return The sub-folder name to save this world's chunks to.
     */
    public abstract String getSaveFolder();
    
    /**
     * A message to display to the user when they transfer to this dimension.
     * 
     * @return The message to be displayed
     */
    public abstract String getWelcomeMessage();
    
    /**
     * A Message to display to the user when they transfer out of this dismension.
     * 
     * @return The message to be displayed
     */
    public abstract String getDepartMessage();
    
    /**
     * The dimensions movement factor. Relative to normal overworld. 
     * It is applied to the players position when they transfer dimensions.
     * Exa: Nether movement is 8.0
     * @return The movement factor
     */
    public double getMovementFactor()
    {
        return 1.0;
    }
}
