package net.minecraft.src;

import java.util.List;

public class WorldInfo
{
    private long randomSeed;
    private WorldType terrainType;

    /** The spawn zone position (X) */
    private int spawnX;

    /** The spawn zone position (Y) */
    private int spawnY;

    /** The spawn zone position (Z) */
    private int spawnZ;

    /** The world time (in ticks) */
    private long worldTime;

    /** The last time the player was in this world. */
    private long lastTimePlayed;
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;
    private String levelName;

    /** Introduced in beta 1.3, is the save version for future control. */
    private int saveVersion;

    /** hold the boolean if its raining or not */
    private boolean raining;

    /** holds the time of the rain */
    private int rainTime;
    private boolean thundering;

    /** hold the during of the thunder */
    private int thunderTime;

    /** Indicates the type of the game. 0 for survival, 1 for creative. */
    private int gameType;

    /** Are map structures going to be generated? (e.g. strongholds) */
    private boolean mapFeaturesEnabled;

    /** Hardcore mode flag */
    private boolean hardcore;

    public WorldInfo(NBTTagCompound par1NBTTagCompound)
    {
        this.terrainType = WorldType.field_48457_b;
        this.hardcore = false;
        this.randomSeed = par1NBTTagCompound.getLong("RandomSeed");

        if (par1NBTTagCompound.hasKey("generatorName"))
        {
            String var2 = par1NBTTagCompound.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(var2);

            if (this.terrainType == null)
            {
                this.terrainType = WorldType.field_48457_b;
            }
            else if (this.terrainType.func_48453_c())
            {
                int var3 = 0;

                if (par1NBTTagCompound.hasKey("generatorVersion"))
                {
                    var3 = par1NBTTagCompound.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.func_48451_a(var3);
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
        this.terrainType = WorldType.field_48457_b;
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
        this.terrainType = WorldType.field_48457_b;
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

    public NBTTagCompound getNBTTagCompound()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.updateTagCompound(var1, this.playerTag);
        return var1;
    }

    /**
     * stores the current level's dat to an nbt tag for future saving in level.dat
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
        par1NBTTagCompound.setString("generatorName", this.terrainType.func_48449_a());
        par1NBTTagCompound.setInteger("generatorVersion", this.terrainType.func_48452_b());
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

    public long getSeed()
    {
        return this.randomSeed;
    }

    public int getSpawnX()
    {
        return this.spawnX;
    }

    public int getSpawnY()
    {
        return this.spawnY;
    }

    public int getSpawnZ()
    {
        return this.spawnZ;
    }

    public long getWorldTime()
    {
        return this.worldTime;
    }

    public int getDimension()
    {
        return this.dimension;
    }

    public void setWorldTime(long par1)
    {
        this.worldTime = par1;
    }

    /**
     * Sets the spawn (zone) position
     */
    public void setSpawnPosition(int par1, int par2, int par3)
    {
        this.spawnX = par1;
        this.spawnY = par2;
        this.spawnZ = par3;
    }

    public void setWorldName(String par1Str)
    {
        this.levelName = par1Str;
    }

    public int getSaveVersion()
    {
        return this.saveVersion;
    }

    public void setSaveVersion(int par1)
    {
        this.saveVersion = par1;
    }

    /**
     * gets if it's thundering or not
     */
    public boolean isThundering()
    {
        return this.thundering;
    }

    /**
     * sets if it's thundering or not
     */
    public void setThundering(boolean par1)
    {
        this.thundering = par1;
    }

    /**
     * gets the during of the thunder
     */
    public int getThunderTime()
    {
        return this.thunderTime;
    }

    /**
     * sets the during of the Thunder
     */
    public void setThunderTime(int par1)
    {
        this.thunderTime = par1;
    }

    /**
     * returns whether it's raining
     */
    public boolean isRaining()
    {
        return this.raining;
    }

    /**
     * sets whether it's raining
     */
    public void setRaining(boolean par1)
    {
        this.raining = par1;
    }

    /**
     * gets the rainTime
     */
    public int getRainTime()
    {
        return this.rainTime;
    }

    /**
     * sets the rainTime
     */
    public void setRainTime(int par1)
    {
        this.rainTime = par1;
    }

    /**
     * Get the game type, 0 for survival, 1 for creative.
     */
    public int getGameType()
    {
        return this.gameType;
    }

    /**
     * Get whether the map features generation is enabled or disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    /**
     * Set the game type, <=0 for survival, >0 for creative.
     */
    public void setGameType(int par1)
    {
        this.gameType = par1;
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

    public void func_48392_a(WorldType par1WorldType)
    {
        this.terrainType = par1WorldType;
    }
}
