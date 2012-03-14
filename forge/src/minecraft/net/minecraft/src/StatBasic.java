package net.minecraft.src;

public class StatBasic extends StatBase
{
    public StatBasic(int par1, String par2Str, IStatType par3IStatType)
    {
        super(par1, par2Str, par3IStatType);
    }

    public StatBasic(int par1, String par2Str)
    {
        super(par1, par2Str);
    }

    /**
     * Register the stat into StatList.
     */
    public StatBase registerStat()
    {
        super.registerStat();
        StatList.generalStats.add(this);
        return this;
    }
}
