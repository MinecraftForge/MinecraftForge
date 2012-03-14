package net.minecraft.src;

import java.util.ArrayList;

public class EntitySenses
{
    EntityLiving entityObj;
    ArrayList canSeeCachePositive = new ArrayList();
    ArrayList canSeeCacheNegative = new ArrayList();

    public EntitySenses(EntityLiving par1EntityLiving)
    {
        this.entityObj = par1EntityLiving;
    }

    /**
     * Returns the squared distance between this door and the given coordinate.
     */
    public void clearSensingCache()
    {
        this.canSeeCachePositive.clear();
        this.canSeeCacheNegative.clear();
    }

    /**
     * Checks, whether 'our' entity can see the entity given as argument (true) or not (false), caching the result.
     */
    public boolean canSee(Entity par1Entity)
    {
        if (this.canSeeCachePositive.contains(par1Entity))
        {
            return true;
        }
        else if (this.canSeeCacheNegative.contains(par1Entity))
        {
            return false;
        }
        else
        {
            Profiler.startSection("canSee");
            boolean var2 = this.entityObj.canEntityBeSeen(par1Entity);
            Profiler.endSection();

            if (var2)
            {
                this.canSeeCachePositive.add(par1Entity);
            }
            else
            {
                this.canSeeCacheNegative.add(par1Entity);
            }

            return var2;
        }
    }
}
