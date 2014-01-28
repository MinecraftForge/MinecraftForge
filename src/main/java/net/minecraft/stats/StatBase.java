package net.minecraft.stats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class StatBase
{
    // JAVADOC FIELD $$ field_75975_e
    public final String statId;
    // JAVADOC FIELD $$ field_75978_a
    private final IChatComponent statName;
    public boolean isIndependent;
    private final IStatType type;
    private final IScoreObjectiveCriteria field_150957_c;
    private Class field_150956_d;
    private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    public static IStatType simpleStatType = new IStatType()
    {
        private static final String __OBFID = "CL_00001473";
        // JAVADOC METHOD $$ func_75843_a
        @SideOnly(Side.CLIENT)
        public String format(int par1)
        {
            return StatBase.numberFormat.format((long)par1);
        }
    };
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    public static IStatType timeStatType = new IStatType()
    {
        private static final String __OBFID = "CL_00001474";
        // JAVADOC METHOD $$ func_75843_a
        @SideOnly(Side.CLIENT)
        public String format(int par1)
        {
            double d0 = (double)par1 / 20.0D;
            double d1 = d0 / 60.0D;
            double d2 = d1 / 60.0D;
            double d3 = d2 / 24.0D;
            double d4 = d3 / 365.0D;
            return d4 > 0.5D ? StatBase.decimalFormat.format(d4) + " y" : (d3 > 0.5D ? StatBase.decimalFormat.format(d3) + " d" : (d2 > 0.5D ? StatBase.decimalFormat.format(d2) + " h" : (d1 > 0.5D ? StatBase.decimalFormat.format(d1) + " m" : d0 + " s")));
        }
    };
    public static IStatType distanceStatType = new IStatType()
    {
        private static final String __OBFID = "CL_00001475";
        // JAVADOC METHOD $$ func_75843_a
        @SideOnly(Side.CLIENT)
        public String format(int par1)
        {
            double d0 = (double)par1 / 100.0D;
            double d1 = d0 / 1000.0D;
            return d1 > 0.5D ? StatBase.decimalFormat.format(d1) + " km" : (d0 > 0.5D ? StatBase.decimalFormat.format(d0) + " m" : par1 + " cm");
        }
    };
    public static IStatType field_111202_k = new IStatType()
    {
        private static final String __OBFID = "CL_00001476";
        // JAVADOC METHOD $$ func_75843_a
        @SideOnly(Side.CLIENT)
        public String format(int par1)
        {
            return StatBase.decimalFormat.format((double)par1 * 0.1D);
        }
    };
    private static final String __OBFID = "CL_00001472";

    public StatBase(String p_i45307_1_, IChatComponent p_i45307_2_, IStatType p_i45307_3_)
    {
        this.statId = p_i45307_1_;
        this.statName = p_i45307_2_;
        this.type = p_i45307_3_;
        this.field_150957_c = new ObjectiveStat(this);
        IScoreObjectiveCriteria.field_96643_a.put(this.field_150957_c.func_96636_a(), this.field_150957_c);
    }

    public StatBase(String p_i45308_1_, IChatComponent p_i45308_2_)
    {
        this(p_i45308_1_, p_i45308_2_, simpleStatType);
    }

    // JAVADOC METHOD $$ func_75966_h
    public StatBase initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    // JAVADOC METHOD $$ func_75971_g
    public StatBase registerStat()
    {
        if (StatList.oneShotStats.containsKey(this.statId))
        {
            throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.oneShotStats.get(this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
        }
        else
        {
            StatList.allStats.add(this);
            StatList.oneShotStats.put(this.statId, this);
            return this;
        }
    }

    // JAVADOC METHOD $$ func_75967_d
    public boolean isAchievement()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public String func_75968_a(int par1)
    {
        return this.type.format(par1);
    }

    public IChatComponent func_150951_e()
    {
        IChatComponent ichatcomponent = this.statName.func_150259_f();
        ichatcomponent.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
        ichatcomponent.func_150256_b().func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
        return ichatcomponent;
    }

    public IChatComponent func_150955_j()
    {
        IChatComponent ichatcomponent = this.func_150951_e();
        IChatComponent ichatcomponent1 = (new ChatComponentText("[")).func_150257_a(ichatcomponent).func_150258_a("]");
        ichatcomponent1.func_150255_a(ichatcomponent.func_150256_b());
        return ichatcomponent1;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            StatBase statbase = (StatBase)par1Obj;
            return this.statId.equals(statbase.statId);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.statId.hashCode();
    }

    public String toString()
    {
        return "Stat{id=" + this.statId + ", nameId=" + this.statName + ", awardLocallyOnly=" + this.isIndependent + ", formatter=" + this.type + ", objectiveCriteria=" + this.field_150957_c + '}';
    }

    public IScoreObjectiveCriteria func_150952_k()
    {
        return this.field_150957_c;
    }

    public Class func_150954_l()
    {
        return this.field_150956_d;
    }

    public StatBase func_150953_b(Class p_150953_1_)
    {
        this.field_150956_d = p_150953_1_;
        return this;
    }
}