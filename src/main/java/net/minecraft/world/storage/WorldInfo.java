package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class WorldInfo
{
    // JAVADOC FIELD $$ field_76100_a
    private long randomSeed;
    private WorldType terrainType;
    private String generatorOptions;
    // JAVADOC FIELD $$ field_76099_c
    private int spawnX;
    // JAVADOC FIELD $$ field_76096_d
    private int spawnY;
    // JAVADOC FIELD $$ field_76097_e
    private int spawnZ;
    // JAVADOC FIELD $$ field_82575_g
    private long totalTime;
    // JAVADOC FIELD $$ field_76094_f
    private long worldTime;
    // JAVADOC FIELD $$ field_76095_g
    private long lastTimePlayed;
    // JAVADOC FIELD $$ field_76107_h
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;
    // JAVADOC FIELD $$ field_76106_k
    private String levelName;
    // JAVADOC FIELD $$ field_76103_l
    private int saveVersion;
    // JAVADOC FIELD $$ field_76104_m
    private boolean raining;
    // JAVADOC FIELD $$ field_76101_n
    private int rainTime;
    // JAVADOC FIELD $$ field_76102_o
    private boolean thundering;
    // JAVADOC FIELD $$ field_76114_p
    private int thunderTime;
    // JAVADOC FIELD $$ field_76113_q
    private WorldSettings.GameType theGameType;
    // JAVADOC FIELD $$ field_76112_r
    private boolean mapFeaturesEnabled;
    // JAVADOC FIELD $$ field_76111_s
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private GameRules theGameRules;
    private Map<String, NBTBase> additionalProperties;
    private static final String __OBFID = "CL_00000587";

    protected WorldInfo()
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
    }

    public WorldInfo(NBTTagCompound par1NBTTagCompound)
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1NBTTagCompound.getLong("RandomSeed");

        if (par1NBTTagCompound.func_150297_b("generatorName", 8))
        {
            String s = par1NBTTagCompound.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(s);

            if (this.terrainType == null)
            {
                this.terrainType = WorldType.DEFAULT;
            }
            else if (this.terrainType.isVersioned())
            {
                int i = 0;

                if (par1NBTTagCompound.func_150297_b("generatorVersion", 99))
                {
                    i = par1NBTTagCompound.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
            }

            if (par1NBTTagCompound.func_150297_b("generatorOptions", 8))
            {
                this.generatorOptions = par1NBTTagCompound.getString("generatorOptions");
            }
        }

        this.theGameType = WorldSettings.GameType.getByID(par1NBTTagCompound.getInteger("GameType"));

        if (par1NBTTagCompound.func_150297_b("MapFeatures", 99))
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
        this.totalTime = par1NBTTagCompound.getLong("Time");

        if (par1NBTTagCompound.func_150297_b("DayTime", 99))
        {
            this.worldTime = par1NBTTagCompound.getLong("DayTime");
        }
        else
        {
            this.worldTime = this.totalTime;
        }

        this.lastTimePlayed = par1NBTTagCompound.getLong("LastPlayed");
        this.sizeOnDisk = par1NBTTagCompound.getLong("SizeOnDisk");
        this.levelName = par1NBTTagCompound.getString("LevelName");
        this.saveVersion = par1NBTTagCompound.getInteger("version");
        this.rainTime = par1NBTTagCompound.getInteger("rainTime");
        this.raining = par1NBTTagCompound.getBoolean("raining");
        this.thunderTime = par1NBTTagCompound.getInteger("thunderTime");
        this.thundering = par1NBTTagCompound.getBoolean("thundering");
        this.hardcore = par1NBTTagCompound.getBoolean("hardcore");

        if (par1NBTTagCompound.func_150297_b("initialized", 99))
        {
            this.initialized = par1NBTTagCompound.getBoolean("initialized");
        }
        else
        {
            this.initialized = true;
        }

        if (par1NBTTagCompound.func_150297_b("allowCommands", 99))
        {
            this.allowCommands = par1NBTTagCompound.getBoolean("allowCommands");
        }
        else
        {
            this.allowCommands = this.theGameType == WorldSettings.GameType.CREATIVE;
        }

        if (par1NBTTagCompound.func_150297_b("Player", 10))
        {
            this.playerTag = par1NBTTagCompound.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }

        if (par1NBTTagCompound.func_150297_b("GameRules", 10))
        {
            this.theGameRules.readGameRulesFromNBT(par1NBTTagCompound.getCompoundTag("GameRules"));
        }
    }

    public WorldInfo(WorldSettings par1WorldSettings, String par2Str)
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1WorldSettings.getSeed();
        this.theGameType = par1WorldSettings.getGameType();
        this.mapFeaturesEnabled = par1WorldSettings.isMapFeaturesEnabled();
        this.levelName = par2Str;
        this.hardcore = par1WorldSettings.getHardcoreEnabled();
        this.terrainType = par1WorldSettings.getTerrainType();
        this.generatorOptions = par1WorldSettings.func_82749_j();
        this.allowCommands = par1WorldSettings.areCommandsAllowed();
        this.initialized = false;
    }

    public WorldInfo(WorldInfo par1WorldInfo)
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1WorldInfo.randomSeed;
        this.terrainType = par1WorldInfo.terrainType;
        this.generatorOptions = par1WorldInfo.generatorOptions;
        this.theGameType = par1WorldInfo.theGameType;
        this.mapFeaturesEnabled = par1WorldInfo.mapFeaturesEnabled;
        this.spawnX = par1WorldInfo.spawnX;
        this.spawnY = par1WorldInfo.spawnY;
        this.spawnZ = par1WorldInfo.spawnZ;
        this.totalTime = par1WorldInfo.totalTime;
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
        this.allowCommands = par1WorldInfo.allowCommands;
        this.initialized = par1WorldInfo.initialized;
        this.theGameRules = par1WorldInfo.theGameRules;
    }

    // JAVADOC METHOD $$ func_76066_a
    public NBTTagCompound getNBTTagCompound()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.updateTagCompound(nbttagcompound, this.playerTag);
        return nbttagcompound;
    }

    // JAVADOC METHOD $$ func_76082_a
    public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        this.updateTagCompound(nbttagcompound1, par1NBTTagCompound);
        return nbttagcompound1;
    }

    private void updateTagCompound(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound)
    {
        par1NBTTagCompound.setLong("RandomSeed", this.randomSeed);
        par1NBTTagCompound.setString("generatorName", this.terrainType.getWorldTypeName());
        par1NBTTagCompound.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
        par1NBTTagCompound.setString("generatorOptions", this.generatorOptions);
        par1NBTTagCompound.setInteger("GameType", this.theGameType.getID());
        par1NBTTagCompound.setBoolean("MapFeatures", this.mapFeaturesEnabled);
        par1NBTTagCompound.setInteger("SpawnX", this.spawnX);
        par1NBTTagCompound.setInteger("SpawnY", this.spawnY);
        par1NBTTagCompound.setInteger("SpawnZ", this.spawnZ);
        par1NBTTagCompound.setLong("Time", this.totalTime);
        par1NBTTagCompound.setLong("DayTime", this.worldTime);
        par1NBTTagCompound.setLong("SizeOnDisk", this.sizeOnDisk);
        par1NBTTagCompound.setLong("LastPlayed", MinecraftServer.getSystemTimeMillis());
        par1NBTTagCompound.setString("LevelName", this.levelName);
        par1NBTTagCompound.setInteger("version", this.saveVersion);
        par1NBTTagCompound.setInteger("rainTime", this.rainTime);
        par1NBTTagCompound.setBoolean("raining", this.raining);
        par1NBTTagCompound.setInteger("thunderTime", this.thunderTime);
        par1NBTTagCompound.setBoolean("thundering", this.thundering);
        par1NBTTagCompound.setBoolean("hardcore", this.hardcore);
        par1NBTTagCompound.setBoolean("allowCommands", this.allowCommands);
        par1NBTTagCompound.setBoolean("initialized", this.initialized);
        par1NBTTagCompound.setTag("GameRules", this.theGameRules.writeGameRulesToNBT());

        if (par2NBTTagCompound != null)
        {
            par1NBTTagCompound.setTag("Player", par2NBTTagCompound);
        }
    }

    // JAVADOC METHOD $$ func_76063_b
    public long getSeed()
    {
        return this.randomSeed;
    }

    // JAVADOC METHOD $$ func_76079_c
    public int getSpawnX()
    {
        return this.spawnX;
    }

    // JAVADOC METHOD $$ func_76075_d
    public int getSpawnY()
    {
        return this.spawnY;
    }

    // JAVADOC METHOD $$ func_76074_e
    public int getSpawnZ()
    {
        return this.spawnZ;
    }

    public long getWorldTotalTime()
    {
        return this.totalTime;
    }

    // JAVADOC METHOD $$ func_76073_f
    public long getWorldTime()
    {
        return this.worldTime;
    }

    @SideOnly(Side.CLIENT)
    public long getSizeOnDisk()
    {
        return this.sizeOnDisk;
    }

    // JAVADOC METHOD $$ func_76072_h
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.playerTag;
    }

    // JAVADOC METHOD $$ func_76076_i
    public int getVanillaDimension()
    {
        return this.dimension;
    }

    // JAVADOC METHOD $$ func_76058_a
    @SideOnly(Side.CLIENT)
    public void setSpawnX(int par1)
    {
        this.spawnX = par1;
    }

    // JAVADOC METHOD $$ func_76056_b
    @SideOnly(Side.CLIENT)
    public void setSpawnY(int par1)
    {
        this.spawnY = par1;
    }

    public void incrementTotalWorldTime(long par1)
    {
        this.totalTime = par1;
    }

    // JAVADOC METHOD $$ func_76087_c
    @SideOnly(Side.CLIENT)
    public void setSpawnZ(int par1)
    {
        this.spawnZ = par1;
    }

    // JAVADOC METHOD $$ func_76068_b
    public void setWorldTime(long par1)
    {
        this.worldTime = par1;
    }

    // JAVADOC METHOD $$ func_76081_a
    public void setSpawnPosition(int par1, int par2, int par3)
    {
        this.spawnX = par1;
        this.spawnY = par2;
        this.spawnZ = par3;
    }

    // JAVADOC METHOD $$ func_76065_j
    public String getWorldName()
    {
        return this.levelName;
    }

    public void setWorldName(String par1Str)
    {
        this.levelName = par1Str;
    }

    // JAVADOC METHOD $$ func_76088_k
    public int getSaveVersion()
    {
        return this.saveVersion;
    }

    // JAVADOC METHOD $$ func_76078_e
    public void setSaveVersion(int par1)
    {
        this.saveVersion = par1;
    }

    // JAVADOC METHOD $$ func_76057_l
    @SideOnly(Side.CLIENT)
    public long getLastTimePlayed()
    {
        return this.lastTimePlayed;
    }

    // JAVADOC METHOD $$ func_76061_m
    public boolean isThundering()
    {
        return this.thundering;
    }

    // JAVADOC METHOD $$ func_76069_a
    public void setThundering(boolean par1)
    {
        this.thundering = par1;
    }

    // JAVADOC METHOD $$ func_76071_n
    public int getThunderTime()
    {
        return this.thunderTime;
    }

    // JAVADOC METHOD $$ func_76090_f
    public void setThunderTime(int par1)
    {
        this.thunderTime = par1;
    }

    // JAVADOC METHOD $$ func_76059_o
    public boolean isRaining()
    {
        return this.raining;
    }

    // JAVADOC METHOD $$ func_76084_b
    public void setRaining(boolean par1)
    {
        this.raining = par1;
    }

    // JAVADOC METHOD $$ func_76083_p
    public int getRainTime()
    {
        return this.rainTime;
    }

    // JAVADOC METHOD $$ func_76080_g
    public void setRainTime(int par1)
    {
        this.rainTime = par1;
    }

    // JAVADOC METHOD $$ func_76077_q
    public WorldSettings.GameType getGameType()
    {
        return this.theGameType;
    }

    // JAVADOC METHOD $$ func_76089_r
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    // JAVADOC METHOD $$ func_76060_a
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        this.theGameType = par1EnumGameType;
    }

    // JAVADOC METHOD $$ func_76093_s
    public boolean isHardcoreModeEnabled()
    {
        return this.hardcore;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    public void setTerrainType(WorldType par1WorldType)
    {
        this.terrainType = par1WorldType;
    }

    public String getGeneratorOptions()
    {
        return this.generatorOptions;
    }

    // JAVADOC METHOD $$ func_76086_u
    public boolean areCommandsAllowed()
    {
        return this.allowCommands;
    }

    // JAVADOC METHOD $$ func_76070_v
    public boolean isInitialized()
    {
        return this.initialized;
    }

    // JAVADOC METHOD $$ func_76091_d
    public void setServerInitialized(boolean par1)
    {
        this.initialized = par1;
    }

    // JAVADOC METHOD $$ func_82574_x
    public GameRules getGameRulesInstance()
    {
        return this.theGameRules;
    }

    // JAVADOC METHOD $$ func_85118_a
    public void addToCrashReport(CrashReportCategory par1CrashReportCategory)
    {
        par1CrashReportCategory.addCrashSectionCallable("Level seed", new Callable()
        {
            private static final String __OBFID = "CL_00000588";
            public String call()
            {
                return String.valueOf(WorldInfo.this.getSeed());
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level generator", new Callable()
        {
            private static final String __OBFID = "CL_00000589";
            public String call()
            {
                return String.format("ID %02d - %s, ver %d. Features enabled: %b", new Object[] {Integer.valueOf(WorldInfo.this.terrainType.getWorldTypeID()), WorldInfo.this.terrainType.getWorldTypeName(), Integer.valueOf(WorldInfo.this.terrainType.getGeneratorVersion()), Boolean.valueOf(WorldInfo.this.mapFeaturesEnabled)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level generator options", new Callable()
        {
            private static final String __OBFID = "CL_00000590";
            public String call()
            {
                return WorldInfo.this.generatorOptions;
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level spawn location", new Callable()
        {
            private static final String __OBFID = "CL_00000591";
            public String call()
            {
                return CrashReportCategory.getLocationInfo(WorldInfo.this.spawnX, WorldInfo.this.spawnY, WorldInfo.this.spawnZ);
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level time", new Callable()
        {
            private static final String __OBFID = "CL_00000592";
            public String call()
            {
                return String.format("%d game time, %d day time", new Object[] {Long.valueOf(WorldInfo.this.totalTime), Long.valueOf(WorldInfo.this.worldTime)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level dimension", new Callable()
        {
            private static final String __OBFID = "CL_00000593";
            public String call()
            {
                return String.valueOf(WorldInfo.this.dimension);
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level storage version", new Callable()
        {
            private static final String __OBFID = "CL_00000594";
            public String call()
            {
                String s = "Unknown?";

                try
                {
                    switch (WorldInfo.this.saveVersion)
                    {
                        case 19132:
                            s = "McRegion";
                            break;
                        case 19133:
                            s = "Anvil";
                    }
                }
                catch (Throwable throwable)
                {
                    ;
                }

                return String.format("0x%05X - %s", new Object[] {Integer.valueOf(WorldInfo.this.saveVersion), s});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level weather", new Callable()
        {
            private static final String __OBFID = "CL_00000595";
            public String call()
            {
                return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[] {Integer.valueOf(WorldInfo.this.rainTime), Boolean.valueOf(WorldInfo.this.raining), Integer.valueOf(WorldInfo.this.thunderTime), Boolean.valueOf(WorldInfo.this.thundering)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level game mode", new Callable()
        {
            private static final String __OBFID = "CL_00000597";
            public String call()
            {
                return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[] {WorldInfo.this.theGameType.getName(), Integer.valueOf(WorldInfo.this.theGameType.getID()), Boolean.valueOf(WorldInfo.this.hardcore), Boolean.valueOf(WorldInfo.this.allowCommands)});
            }
        });
    }

    /**
     * Allow access to additional mod specific world based properties
     * Used by FML to store mod list associated with a world, and maybe an id map
     * Used by Forge to store the dimensions available to a world
     * @param additionalProperties
     */
    public void setAdditionalProperties(Map<String,NBTBase> additionalProperties)
    {
        // one time set for this
        if (this.additionalProperties == null)
        {
            this.additionalProperties = additionalProperties;
        }
    }

    public NBTBase getAdditionalProperty(String additionalProperty)
    {
        return this.additionalProperties!=null? this.additionalProperties.get(additionalProperty) : null;
    }
}