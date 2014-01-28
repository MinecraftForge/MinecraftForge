package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.storage.WorldInfo;

public final class WorldSettings
{
    // JAVADOC FIELD $$ field_77174_a
    private final long seed;
    // JAVADOC FIELD $$ field_77172_b
    private final WorldSettings.GameType theGameType;
    // JAVADOC FIELD $$ field_77173_c
    private final boolean mapFeaturesEnabled;
    // JAVADOC FIELD $$ field_77170_d
    private final boolean hardcoreEnabled;
    private final WorldType terrainType;
    // JAVADOC FIELD $$ field_77168_f
    private boolean commandsAllowed;
    // JAVADOC FIELD $$ field_77169_g
    private boolean bonusChestEnabled;
    private String field_82751_h;
    private static final String __OBFID = "CL_00000147";

    public WorldSettings(long par1, WorldSettings.GameType par3EnumGameType, boolean par4, boolean par5, WorldType par6WorldType)
    {
        this.field_82751_h = "";
        this.seed = par1;
        this.theGameType = par3EnumGameType;
        this.mapFeaturesEnabled = par4;
        this.hardcoreEnabled = par5;
        this.terrainType = par6WorldType;
    }

    public WorldSettings(WorldInfo par1WorldInfo)
    {
        this(par1WorldInfo.getSeed(), par1WorldInfo.getGameType(), par1WorldInfo.isMapFeaturesEnabled(), par1WorldInfo.isHardcoreModeEnabled(), par1WorldInfo.getTerrainType());
    }

    // JAVADOC METHOD $$ func_77159_a
    public WorldSettings enableBonusChest()
    {
        this.bonusChestEnabled = true;
        return this;
    }

    public WorldSettings func_82750_a(String par1Str)
    {
        this.field_82751_h = par1Str;
        return this;
    }

    // JAVADOC METHOD $$ func_77166_b
    @SideOnly(Side.CLIENT)
    public WorldSettings enableCommands()
    {
        this.commandsAllowed = true;
        return this;
    }

    // JAVADOC METHOD $$ func_77167_c
    public boolean isBonusChestEnabled()
    {
        return this.bonusChestEnabled;
    }

    // JAVADOC METHOD $$ func_77160_d
    public long getSeed()
    {
        return this.seed;
    }

    // JAVADOC METHOD $$ func_77162_e
    public WorldSettings.GameType getGameType()
    {
        return this.theGameType;
    }

    // JAVADOC METHOD $$ func_77158_f
    public boolean getHardcoreEnabled()
    {
        return this.hardcoreEnabled;
    }

    // JAVADOC METHOD $$ func_77164_g
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    // JAVADOC METHOD $$ func_77163_i
    public boolean areCommandsAllowed()
    {
        return this.commandsAllowed;
    }

    // JAVADOC METHOD $$ func_77161_a
    public static WorldSettings.GameType getGameTypeById(int par0)
    {
        return WorldSettings.GameType.getByID(par0);
    }

    public String func_82749_j()
    {
        return this.field_82751_h;
    }

    public static enum GameType
    {
        NOT_SET(-1, ""),
        SURVIVAL(0, "survival"),
        CREATIVE(1, "creative"),
        ADVENTURE(2, "adventure");
        int id;
        String name;

        private static final String __OBFID = "CL_00000148";

        private GameType(int par3, String par4Str)
        {
            this.id = par3;
            this.name = par4Str;
        }

        // JAVADOC METHOD $$ func_77148_a
        public int getID()
        {
            return this.id;
        }

        // JAVADOC METHOD $$ func_77149_b
        public String getName()
        {
            return this.name;
        }

        // JAVADOC METHOD $$ func_77147_a
        public void configurePlayerCapabilities(PlayerCapabilities par1PlayerCapabilities)
        {
            if (this == CREATIVE)
            {
                par1PlayerCapabilities.allowFlying = true;
                par1PlayerCapabilities.isCreativeMode = true;
                par1PlayerCapabilities.disableDamage = true;
            }
            else
            {
                par1PlayerCapabilities.allowFlying = false;
                par1PlayerCapabilities.isCreativeMode = false;
                par1PlayerCapabilities.disableDamage = false;
                par1PlayerCapabilities.isFlying = false;
            }

            par1PlayerCapabilities.allowEdit = !this.isAdventure();
        }

        // JAVADOC METHOD $$ func_82752_c
        public boolean isAdventure()
        {
            return this == ADVENTURE;
        }

        // JAVADOC METHOD $$ func_77145_d
        public boolean isCreative()
        {
            return this == CREATIVE;
        }

        // JAVADOC METHOD $$ func_77144_e
        @SideOnly(Side.CLIENT)
        public boolean isSurvivalOrAdventure()
        {
            return this == SURVIVAL || this == ADVENTURE;
        }

        // JAVADOC METHOD $$ func_77146_a
        public static WorldSettings.GameType getByID(int par0)
        {
            WorldSettings.GameType[] agametype = values();
            int j = agametype.length;

            for (int k = 0; k < j; ++k)
            {
                WorldSettings.GameType gametype = agametype[k];

                if (gametype.id == par0)
                {
                    return gametype;
                }
            }

            return SURVIVAL;
        }

        // JAVADOC METHOD $$ func_77142_a
        @SideOnly(Side.CLIENT)
        public static WorldSettings.GameType getByName(String par0Str)
        {
            WorldSettings.GameType[] agametype = values();
            int i = agametype.length;

            for (int j = 0; j < i; ++j)
            {
                WorldSettings.GameType gametype = agametype[j];

                if (gametype.name.equals(par0Str))
                {
                    return gametype;
                }
            }

            return SURVIVAL;
        }
    }
}