package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo
{
    // JAVADOC FIELD $$ field_76115_a
    private final WorldInfo theWorldInfo;
    private static final String __OBFID = "CL_00000584";

    public DerivedWorldInfo(WorldInfo par1WorldInfo)
    {
        this.theWorldInfo = par1WorldInfo;
    }

    // JAVADOC METHOD $$ func_76066_a
    public NBTTagCompound getNBTTagCompound()
    {
        return this.theWorldInfo.getNBTTagCompound();
    }

    // JAVADOC METHOD $$ func_76082_a
    public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
    {
        return this.theWorldInfo.cloneNBTCompound(par1NBTTagCompound);
    }

    // JAVADOC METHOD $$ func_76063_b
    public long getSeed()
    {
        return this.theWorldInfo.getSeed();
    }

    // JAVADOC METHOD $$ func_76079_c
    public int getSpawnX()
    {
        return this.theWorldInfo.getSpawnX();
    }

    // JAVADOC METHOD $$ func_76075_d
    public int getSpawnY()
    {
        return this.theWorldInfo.getSpawnY();
    }

    // JAVADOC METHOD $$ func_76074_e
    public int getSpawnZ()
    {
        return this.theWorldInfo.getSpawnZ();
    }

    public long getWorldTotalTime()
    {
        return this.theWorldInfo.getWorldTotalTime();
    }

    // JAVADOC METHOD $$ func_76073_f
    public long getWorldTime()
    {
        return this.theWorldInfo.getWorldTime();
    }

    @SideOnly(Side.CLIENT)
    public long getSizeOnDisk()
    {
        return this.theWorldInfo.getSizeOnDisk();
    }

    // JAVADOC METHOD $$ func_76072_h
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.theWorldInfo.getPlayerNBTTagCompound();
    }

    // JAVADOC METHOD $$ func_76076_i
    public int getVanillaDimension()
    {
        return this.theWorldInfo.getVanillaDimension();
    }

    // JAVADOC METHOD $$ func_76065_j
    public String getWorldName()
    {
        return this.theWorldInfo.getWorldName();
    }

    // JAVADOC METHOD $$ func_76088_k
    public int getSaveVersion()
    {
        return this.theWorldInfo.getSaveVersion();
    }

    // JAVADOC METHOD $$ func_76057_l
    @SideOnly(Side.CLIENT)
    public long getLastTimePlayed()
    {
        return this.theWorldInfo.getLastTimePlayed();
    }

    // JAVADOC METHOD $$ func_76061_m
    public boolean isThundering()
    {
        return this.theWorldInfo.isThundering();
    }

    // JAVADOC METHOD $$ func_76071_n
    public int getThunderTime()
    {
        return this.theWorldInfo.getThunderTime();
    }

    // JAVADOC METHOD $$ func_76059_o
    public boolean isRaining()
    {
        return this.theWorldInfo.isRaining();
    }

    // JAVADOC METHOD $$ func_76083_p
    public int getRainTime()
    {
        return this.theWorldInfo.getRainTime();
    }

    // JAVADOC METHOD $$ func_76077_q
    public WorldSettings.GameType getGameType()
    {
        return this.theWorldInfo.getGameType();
    }

    // JAVADOC METHOD $$ func_76058_a
    @SideOnly(Side.CLIENT)
    public void setSpawnX(int par1) {}

    // JAVADOC METHOD $$ func_76056_b
    @SideOnly(Side.CLIENT)
    public void setSpawnY(int par1) {}

    public void incrementTotalWorldTime(long par1) {}

    // JAVADOC METHOD $$ func_76087_c
    @SideOnly(Side.CLIENT)
    public void setSpawnZ(int par1) {}

    // JAVADOC METHOD $$ func_76068_b
    public void setWorldTime(long par1) {}

    // JAVADOC METHOD $$ func_76081_a
    public void setSpawnPosition(int par1, int par2, int par3) {}

    public void setWorldName(String par1Str) {}

    // JAVADOC METHOD $$ func_76078_e
    public void setSaveVersion(int par1) {}

    // JAVADOC METHOD $$ func_76069_a
    public void setThundering(boolean par1) {}

    // JAVADOC METHOD $$ func_76090_f
    public void setThunderTime(int par1) {}

    // JAVADOC METHOD $$ func_76084_b
    public void setRaining(boolean par1) {}

    // JAVADOC METHOD $$ func_76080_g
    public void setRainTime(int par1) {}

    // JAVADOC METHOD $$ func_76089_r
    public boolean isMapFeaturesEnabled()
    {
        return this.theWorldInfo.isMapFeaturesEnabled();
    }

    // JAVADOC METHOD $$ func_76093_s
    public boolean isHardcoreModeEnabled()
    {
        return this.theWorldInfo.isHardcoreModeEnabled();
    }

    public WorldType getTerrainType()
    {
        return this.theWorldInfo.getTerrainType();
    }

    public void setTerrainType(WorldType par1WorldType) {}

    // JAVADOC METHOD $$ func_76086_u
    public boolean areCommandsAllowed()
    {
        return this.theWorldInfo.areCommandsAllowed();
    }

    // JAVADOC METHOD $$ func_76070_v
    public boolean isInitialized()
    {
        return this.theWorldInfo.isInitialized();
    }

    // JAVADOC METHOD $$ func_76091_d
    public void setServerInitialized(boolean par1) {}

    // JAVADOC METHOD $$ func_82574_x
    public GameRules getGameRulesInstance()
    {
        return this.theWorldInfo.getGameRulesInstance();
    }
}