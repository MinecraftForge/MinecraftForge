package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses
{
    EntityLiving entityObj;
    // JAVADOC FIELD $$ field_75524_b
    List seenEntities = new ArrayList();
    // JAVADOC FIELD $$ field_75525_c
    List unseenEntities = new ArrayList();
    private static final String __OBFID = "CL_00001628";

    public EntitySenses(EntityLiving par1EntityLiving)
    {
        this.entityObj = par1EntityLiving;
    }

    // JAVADOC METHOD $$ func_75523_a
    public void clearSensingCache()
    {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    // JAVADOC METHOD $$ func_75522_a
    public boolean canSee(Entity par1Entity)
    {
        if (this.seenEntities.contains(par1Entity))
        {
            return true;
        }
        else if (this.unseenEntities.contains(par1Entity))
        {
            return false;
        }
        else
        {
            this.entityObj.worldObj.theProfiler.startSection("canSee");
            boolean flag = this.entityObj.canEntityBeSeen(par1Entity);
            this.entityObj.worldObj.theProfiler.endSection();

            if (flag)
            {
                this.seenEntities.add(par1Entity);
            }
            else
            {
                this.unseenEntities.add(par1Entity);
            }

            return flag;
        }
    }
}