package net.minecraft.stats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter
{
    protected final Map field_150875_a = new HashMap();
    private static final String __OBFID = "CL_00001481";

    // JAVADOC METHOD $$ func_77443_a
    public boolean hasAchievementUnlocked(Achievement par1Achievement)
    {
        return this.writeStat(par1Achievement) > 0;
    }

    // JAVADOC METHOD $$ func_77442_b
    public boolean canUnlockAchievement(Achievement par1Achievement)
    {
        return par1Achievement.parentAchievement == null || this.hasAchievementUnlocked(par1Achievement.parentAchievement);
    }

    public void func_150871_b(EntityPlayer p_150871_1_, StatBase p_150871_2_, int p_150871_3_)
    {
        if (!p_150871_2_.isAchievement() || this.canUnlockAchievement((Achievement)p_150871_2_))
        {
            this.func_150873_a(p_150871_1_, p_150871_2_, this.writeStat(p_150871_2_) + p_150871_3_);
        }
    }

    @SideOnly(Side.CLIENT)
    public int func_150874_c(Achievement p_150874_1_)
    {
        if (this.hasAchievementUnlocked(p_150874_1_))
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (Achievement achievement1 = p_150874_1_.parentAchievement; achievement1 != null && !this.hasAchievementUnlocked(achievement1); ++i)
            {
                achievement1 = achievement1.parentAchievement;
            }

            return i;
        }
    }

    public void func_150873_a(EntityPlayer p_150873_1_, StatBase p_150873_2_, int p_150873_3_)
    {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable)this.field_150875_a.get(p_150873_2_);

        if (tupleintjsonserializable == null)
        {
            tupleintjsonserializable = new TupleIntJsonSerializable();
            this.field_150875_a.put(p_150873_2_, tupleintjsonserializable);
        }

        tupleintjsonserializable.func_151188_a(p_150873_3_);
    }

    public int writeStat(StatBase par1StatBase)
    {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable)this.field_150875_a.get(par1StatBase);
        return tupleintjsonserializable == null ? 0 : tupleintjsonserializable.func_151189_a();
    }

    public IJsonSerializable func_150870_b(StatBase p_150870_1_)
    {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable)this.field_150875_a.get(p_150870_1_);
        return tupleintjsonserializable != null ? tupleintjsonserializable.func_151187_b() : null;
    }

    public IJsonSerializable func_150872_a(StatBase p_150872_1_, IJsonSerializable p_150872_2_)
    {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable)this.field_150875_a.get(p_150872_1_);

        if (tupleintjsonserializable == null)
        {
            tupleintjsonserializable = new TupleIntJsonSerializable();
            this.field_150875_a.put(p_150872_1_, tupleintjsonserializable);
        }

        tupleintjsonserializable.func_151190_a(p_150872_2_);
        return p_150872_2_;
    }
}