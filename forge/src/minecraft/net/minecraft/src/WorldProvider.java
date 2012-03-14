package net.minecraft.src;

import net.minecraft.src.forge.DimensionManager;

public abstract class WorldProvider
{
    /** world object being used */
    public World worldObj;
    public WorldType terrainType;

    /** World chunk manager being used to generate chunks */
    public WorldChunkManager worldChunkMgr;

    /**
     * States whether the Hell world provider is used(true) or if the normal world provider is used(false)
     */
    public boolean isHellWorld = false;

    /**
     * A boolean that tells if a world does not have a sky. Used in calculating weather and skylight
     */
    public boolean hasNoSky = false;

    /** Light to brightness conversion table */
    public float[] lightBrightnessTable = new float[16];

    /** 0 for normal world -1 for hell */
    public int worldType = 0;

    /** Array for sunrise/sunset colors (RGBA) */
    private float[] colorsSunriseSunset = new float[4];

    /**
     * associate an existing world with a World provider, and setup its lightbrightness table
     */
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

    /**
     * creates a new world chunk manager for WorldProvider
     */
    protected void registerWorldChunkManager()
    {
        if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.field_48636_c)
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
        return (IChunkProvider)(this.terrainType == WorldType.field_48636_c ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled()) : new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled()));
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        int var3 = this.worldObj.getFirstUncoveredBlock(par1, par2);
        return var3 == Block.grass.blockID;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
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

    public int getMoonPhase(long par1, float par3)
    {
        return (int)(par1 / 24000L) % 8;
    }

    public boolean func_48217_e()
    {
        return true;
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    public float[] calcSunriseSunsetColors(float par1, float par2)
    {
        float var3 = 0.4F;
        float var4 = MathHelper.cos(par1 * (float)Math.PI * 2.0F) - 0.0F;
        float var5 = -0.0F;

        if (var4 >= var5 - var3 && var4 <= var5 + var3)
        {
            float var6 = (var4 - var5) / var3 * 0.5F + 0.5F;
            float var7 = 1.0F - (1.0F - MathHelper.sin(var6 * (float)Math.PI)) * 0.99F;
            var7 *= var7;
            this.colorsSunriseSunset[0] = var6 * 0.3F + 0.7F;
            this.colorsSunriseSunset[1] = var6 * var6 * 0.7F + 0.2F;
            this.colorsSunriseSunset[2] = var6 * var6 * 0.0F + 0.2F;
            this.colorsSunriseSunset[3] = var7;
            return this.colorsSunriseSunset;
        }
        else
        {
            return null;
        }
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    public Vec3D getFogColor(float par1, float par2)
    {
        float var3 = MathHelper.cos(par1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (var3 < 0.0F)
        {
            var3 = 0.0F;
        }

        if (var3 > 1.0F)
        {
            var3 = 1.0F;
        }

        float var4 = 0.7529412F;
        float var5 = 0.84705883F;
        float var6 = 1.0F;
        var4 *= var3 * 0.94F + 0.06F;
        var5 *= var3 * 0.94F + 0.06F;
        var6 *= var3 * 0.91F + 0.09F;
        return Vec3D.createVector((double)var4, (double)var5, (double)var6);
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return true;
    }

    public static WorldProvider getProviderForDimension(int par0)
    {
        return DimensionManager.getProvider(par0);
    }

    /**
     * the y level at which clouds are rendered.
     */
    public float getCloudHeight()
    {
        return 128.0F;
    }

    public boolean isSkyColored()
    {
        return true;
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
        return this.terrainType == WorldType.field_48636_c ? 4 : 64;
    }

    /**
     * returns true if there should be no sky, false otherwise
     */
    public boolean getWorldHasNoSky()
    {
        return this.terrainType != WorldType.field_48636_c && !this.hasNoSky;
    }

    public double func_46065_j()
    {
        return this.terrainType == WorldType.field_48636_c ? 1.0D : 0.03125D;
    }

    public boolean func_48218_b(int par1, int par2)
    {
        return false;
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
