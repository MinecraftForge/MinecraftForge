package net.minecraft.src;

public final class WorldSettings
{
    /** The seed for the map. */
    private final long seed;

    /** The type of the map. 0 is survival, 1 is creative. */
    private final int gameType;

    /**
     * Switch for the map features. 'true' for enabled, 'false' for disabled.
     */
    private final boolean mapFeaturesEnabled;

    /** True if hardcore mode is enabled */
    private final boolean hardcoreEnabled;
    private final WorldType terrainType;

    public WorldSettings(long par1, int par3, boolean par4, boolean par5, WorldType par6WorldType)
    {
        this.seed = par1;
        this.gameType = par3;
        this.mapFeaturesEnabled = par4;
        this.hardcoreEnabled = par5;
        this.terrainType = par6WorldType;
    }

    /**
     * Get the seed of the map.
     */
    public long getSeed()
    {
        return this.seed;
    }

    /**
     * Get the type of game the map is set at. 0 is survival, 1 is creative.
     */
    public int getGameType()
    {
        return this.gameType;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean getHardcoreEnabled()
    {
        return this.hardcoreEnabled;
    }

    /**
     * Get whether the map features generation is enabled or disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    /**
     * Checks to see if the int passed is a valid game type indicator.
     */
    public static int validGameType(int par0)
    {
        switch (par0)
        {
            case 0:
            case 1:
                return par0;

            default:
                return 0;
        }
    }
}
