package net.minecraft.scoreboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ScoreObjective
{
    private final Scoreboard theScoreboard;
    private final String name;
    // JAVADOC FIELD $$ field_96685_c
    private final IScoreObjectiveCriteria objectiveCriteria;
    private String displayName;
    private static final String __OBFID = "CL_00000614";

    public ScoreObjective(Scoreboard par1Scoreboard, String par2Str, IScoreObjectiveCriteria par3ScoreObjectiveCriteria)
    {
        this.theScoreboard = par1Scoreboard;
        this.name = par2Str;
        this.objectiveCriteria = par3ScoreObjectiveCriteria;
        this.displayName = par2Str;
    }

    @SideOnly(Side.CLIENT)
    public Scoreboard getScoreboard()
    {
        return this.theScoreboard;
    }

    public String getName()
    {
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria()
    {
        return this.objectiveCriteria;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName(String par1Str)
    {
        this.displayName = par1Str;
        this.theScoreboard.func_96532_b(this);
    }
}