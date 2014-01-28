package net.minecraft.scoreboard;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria
{
    private static final String __OBFID = "CL_00000623";

    public ScoreHealthCriteria(String par1Str)
    {
        super(par1Str);
    }

    public int func_96635_a(List par1List)
    {
        float f = 0.0F;
        EntityPlayer entityplayer;

        for (Iterator iterator = par1List.iterator(); iterator.hasNext(); f += entityplayer.getHealth() + entityplayer.getAbsorptionAmount())
        {
            entityplayer = (EntityPlayer)iterator.next();
        }

        if (par1List.size() > 0)
        {
            f /= (float)par1List.size();
        }

        return MathHelper.ceiling_float_int(f);
    }

    public boolean isReadOnly()
    {
        return true;
    }
}