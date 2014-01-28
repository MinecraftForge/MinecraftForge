package net.minecraft.stats;

import net.minecraft.util.IChatComponent;

public class StatBasic extends StatBase
{
    private static final String __OBFID = "CL_00001469";

    public StatBasic(String p_i45303_1_, IChatComponent p_i45303_2_, IStatType p_i45303_3_)
    {
        super(p_i45303_1_, p_i45303_2_, p_i45303_3_);
    }

    public StatBasic(String p_i45304_1_, IChatComponent p_i45304_2_)
    {
        super(p_i45304_1_, p_i45304_2_);
    }

    // JAVADOC METHOD $$ func_75971_g
    public StatBase registerStat()
    {
        super.registerStat();
        StatList.generalStats.add(this);
        return this;
    }
}