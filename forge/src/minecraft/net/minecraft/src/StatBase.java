package net.minecraft.src;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StatBase
{
    /** The Stat ID */
    public final int statId;

    /** The Stat name */
    private final String statName;
    public boolean isIndependent;

    /** Holds the GUID of the stat. */
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

    /**
     * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
     * current instance.
     */
    public StatBase initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    /**
     * Register the stat into StatList.
     */
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

    /**
     * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
     */
    public boolean isAchievement()
    {
        return false;
    }

    public String func_27084_a(int par1)
    {
        return this.type.format(par1);
    }

    public String getName()
    {
        return this.statName;
    }

    public String toString()
    {
        return StatCollector.translateToLocal(this.statName);
    }

    static NumberFormat getNumberFormat()
    {
        return numberFormat;
    }

    static DecimalFormat getDecimalFormat()
    {
        return decimalFormat;
    }
}
