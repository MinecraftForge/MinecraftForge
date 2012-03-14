package net.minecraft.src;

import java.util.List;

public class WorldInfo
{
    /** Holds the seed of the currently world. */
    private long randomSeed;
    private WorldType terrainType;

    /** The player spawn X coordinate. */
    private int spawnX;

    /** The player spawn Y coordinate. */
    private int spawnY;

    /** The player spawn Z coordinate. */
    private int spawnZ;

    /** The current world time in range from 0 to 23999. */
    private long worldTime;

    /** The last time the player was in this world. */
    private long lastTimePlayed;

    /** The size of entire save of current world on the disk, isn't exactly. */
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;

    /** The name of the save defined at world creation. */
    private String levelName;

    /** Introduced in beta 1.3, is the save version for future control. */
    private int saveVersion;

    /** Is current raining? */
    private boolean raining;

    /** Number of ticks until next rain. */
    private int rainTime;

    /** Is thunderbolts failing now? */
    private boolean thundering;

    /** Number of ticks untils next thunderbolt. */
    private int thunderTime;
    private int gameType;

    /** are map structures going to be generated (e.g. strongholds) */
    private boolean mapFeaturesEnabled;

    /** Hardcore mode flag */
    private boolean hardcore;

    public WorldInfo(NBTTagCompound par1NBTTagCompound)
    {
        this.terrainType = WorldType.field_48635_b;
        this.hardcore = false;
        this.randomSeed = par1NBTTagCompound.getLong("RandomSeed");

        if (par1NBTTagCompound.hasKey("generatorName"))
        {
            String var2 = par1NBTTagCompound.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(var2);

            if (this.terrainType == null)
            {
                this.terrainType = WorldType.field_48635_b;
            }
            else if (this.terrainType.func_48626_e())
            {
                int var3 = 0;

                if (par1NBTTagCompound.hasKey("generatorVersion"))
                {
                    var3 = par1NBTTagCompound.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.func_48629_a(var3);
            }
        }

        this.gameType = par1NBTTagCompound.getInteger("GameType");

        if (par1NBTTagCompound.hasKey("MapFeatures"))
        {
            this.mapFeaturesEnabled = par1NBTTagCompound.getBoolean("MapFeatures");
        }
        else
        {
            this.mapFeaturesEnabled = true;
        }

        this.spawnX = par1NBTTagCompound.getInteger("SpawnX");
        this.spawnY = par1NBTTagCompound.getInteger("SpawnY");
        this.spawnZ = par1NBTTagCompound.getInteger("SpawnZ");
        this.worldTime = par1NBTTagCompound.getLong("Time");
        this.lastTimePlayed = par1NBTTagCompound.getLong("LastPlayed");
        this.sizeOnDisk = par1NBTTagCompound.getLong("SizeOnDisk");
        this.levelName = par1NBTTagCompound.getString("LevelName");
        this.saveVersion = par1NBTTagCompound.getInteger("version");
        this.rainTime = par1NBTTagCompound.getInteger("rainTime");
        this.raining = par1NBTTagCompound.getBoolean("raining");
        this.thunderTime = par1NBTTagCompound.getInteger("thunderTime");
        this.thundering = par1NBTTagCompound.getBoolean("thundering");
        this.hardcore = par1NBTTagCompound.getBoolean("hardcore");

        if (par1NBTTagCompound.hasKey("Player"))
        {
            this.playerTag = par1NBTTagCompound.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }
    }

    public WorldInfo(WorldSettings par1WorldSettings, String par2Str)
    {
        this.terrainType = WorldType.field_48635_b;
        this.hardcore = false;
        this.randomSeed = par1WorldSettings.getSeed();
        this.gameType = par1WorldSettings.getGameType();
        this.mapFeaturesEnabled = par1WorldSettings.isMapFeaturesEnabled();
        this.levelName = par2Str;
        this.hardcore = par1WorldSettings.getHardcoreEnabled();
        this.terrainType = par1WorldSettings.getTerrainType();
    }

    public WorldInfo(WorldInfo par1WorldInfo)
    {
        this.terrainType = WorldType.field_48635_b;
        this.hardcore = false;
        this.randomSeed = par1WorldInfo.randomSeed;
        this.terrainType = par1WorldInfo.terrainType;
        this.gameType = par1WorldInfo.gameType;
        this.mapFeaturesEnabled = par1WorldInfo.mapFeaturesEnabled;
        this.spawnX = par1WorldInfo.spawnX;
        this.spawnY = par1WorldInfo.spawnY;
        this.spawnZ = par1WorldInfo.spawnZ;
        this.worldTime = par1WorldInfo.worldTime;
        this.lastTimePlayed = par1WorldInfo.lastTimePlayed;
        this.sizeOnDisk = par1WorldInfo.sizeOnDisk;
        this.playerTag = par1WorldInfo.playerTag;
        this.dimension = par1WorldInfo.dimension;
        this.levelName = par1WorldInfo.levelName;
        this.saveVersion = par1WorldInfo.saveVersion;
        this.rainTime = par1WorldInfo.rainTime;
        this.raining = par1WorldInfo.raining;
        this.thunderTime = par1WorldInfo.thunderTime;
        this.thundering = par1WorldInfo.thundering;
        this.hardcore = par1WorldInfo.hardcore;
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound getNBTTagCompound()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.updateTagCompound(var1, this.playerTag);
        return var1;
    }

    /**
     * Generates the NBTTagCompound for the world info plus the provided entity list. Arg: entityList
     */
    public NBTTagCompound getNBTTagCompoundWithPlayers(List par1List)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        EntityPlayer var3 = null;
        NBTTagCompound var4 = null;

        if (par1List.size() > 0)
        {
            var3 = (EntityPlayer)par1List.get(0);
        }

        if (var3 != null)
        {
            var4 = new NBTTagCompound();
            var3.writeToNBT(var4);
        }

        this.updateTagCompound(var2, var4);
        return var2;
    }

    private void updateTagCompound(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound)
    {
        par1NBTTagCompound.setLong("RandomSeed", this.randomSeed);
        par1NBTTagCompound.setString("generatorName", this.terrainType.func_48628_a());
        par1NBTTagCompound.setInteger("generatorVersion", this.terrainType.func_48630_c());
        par1NBTTagCompound.setInteger("GameType", this.gameType);
        par1NBTTagCompound.setBoolean("MapFeatures", this.mapFeaturesEnabled);
        par1NBTTagCompound.setInteger("SpawnX", this.spawnX);
        par1NBTTagCompound.setInteger("SpawnY", this.spawnY);
        par1NBTTagCompound.setInteger("SpawnZ", this.spawnZ);
        par1NBTTagCompound.setLong("Time", this.worldTime);
        par1NBTTagCompound.setLong("SizeOnDisk", this.sizeOnDisk);
        par1NBTTagCompound.setLong("LastPlayed", System.currentTimeMillis());
        par1NBTTagCompound.setString("LevelName", this.levelName);
        par1NBTTagCompound.setInteger("version", this.saveVersion);
        par1NBTTagCompound.setInteger("rainTime", this.rainTime);
        par1NBTTagCompound.setBoolean("raining", this.raining);
        par1NBTTagCompound.setInteger("thunderTime", this.thunderTime);
        par1NBTTagCompound.setBoolean("thundering", this.thundering);
        par1NBTTagCompound.setBoolean("hardcore", this.hardcore);

        if (par2NBTTagCompound != null)
        {
            par1NBTTagCompound.setCompoundTag("Player", par2NBTTagCompound);
        }
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed()
    {
        return this.randomSeed;
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return this.spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return this.spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return this.spawnZ;
    }

    /**
     * Get current world time
     */
    public long getWorldTime()
    {
        return this.worldTime;
    }

    public long getSizeOnDisk()
    {
        return this.sizeOnDisk;
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.playerTag;
    }

    public int getDimension()
    {
        return this.dimension;
    }

    /**
     * Set the x spawn position to the passed in value
     */
    public void setSpawnX(int par1)
    {
        this.spawnX = par1;
    }

    /**
     * Sets the y spawn position
     */
    public void setSpawnY(int par1)
    {
        this.spawnY = par1;
    }

    /**
     * Set the z spawn position to the passed in value
     */
    public void setSpawnZ(int par1)
    {
        this.spawnZ = par1;
    }

    /**
     * Set current world time
     */
    public void setWorldTime(long par1)
    {
        this.worldTime = par1;
    }

    /**
     * Sets the player's NBTTagCompound to be loaded.
     */
    public void setPlayerNBTTagCompound(NBTTagCompound par1NBTTagCompound)
    {
        this.playerTag = par1NBTTagCompound;
    }

    /**
     * Sets the spawn position Args: x, y, z
     */
    public void setSpawnPosition(int par1, int par2, int par3)
    {
        this.spawnX = par1;
        this.spawnY = par2;
        this.spawnZ = par3;
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return this.levelName;
    }

    public void setWorldName(String par1Str)
    {
        this.levelName = par1Str;
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion()
    {
        return this.saveVersion;
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(int par1)
    {
        this.saveVersion = par1;
    }

    /**
     * Return the last time the player was in this world.
     */
    public long getLastTimePlayed()
    {
        return this.lastTimePlayed;
    }

    /**
     * Returns the current state of thunderbolts.
     */
    public boolean isThundering()
    {
        return this.thundering;
    }

    /**
     * Defines if is thundering now.
     */
    public void setThundering(boolean par1)
    {
        this.thundering = par1;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return this.thunderTime;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int par1)
    {
        this.thunderTime = par1;
    }

    /**
     * Gets the current state of raining.
     */
    public boolean isRaining()
    {
        return this.raining;
    }

    /**
     * Sets the current state of raining.
     */
    public void setRaining(boolean par1)
    {
        this.raining = par1;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return this.rainTime;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int par1)
    {
        this.rainTime = par1;
    }

    public int getGameType()
    {
        return this.gameType;
    }

    /**
     * are map structures going to be generated (e.g. strongholds)
     */
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcoreModeEnabled()
    {
        return this.hardcore;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    public void func_48619_a(WorldType par1WorldType)
    {
        this.terrainType = par1WorldType;
    }
}
