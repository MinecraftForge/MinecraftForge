package net.minecraft.src;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StatBase
{
    public final int statId;
    private final String statName;
    public boolean isIndependent;
    public String statGuid;
    private final IStatType type;
    private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    public static IStatType simpleStatType = new StatTypeSimple();
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    public static IStatType timeStatType = new StatTypeTime();
    public static IStatType distanceStatType = new StatTypeDistance();

    public StatBase(int par1, String par2Str, IStatType par3IStatType)
    {
        this.isIndependent = false;
        this.statId = par1;
        this.statName = par2Str;
        this.type = par3IStatType;
    }

    public StatBase(int par1, String par2Str)
    {
        this(par1, par2Str, simpleStatType);
    }

    public StatBase initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    public StatBase registerStat()
    {
        if (StatList.oneShotStats.containsKey(Integer.valueOf(this.statId)))
        {
            throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.oneShotStats.get(Integer.valueOf(this.statId))).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
        }
        else
        {
            StatList.allStats.add(this);
            StatList.oneShotStats.put(Integer.valueOf(this.statId), this);
            this.statGuid = AchievementMap.getGuid(this.statId);
            return this;
        }
    }

    public String toString()
    {
        return StatCollector.translateToLocal(this.statName);
    }
}
